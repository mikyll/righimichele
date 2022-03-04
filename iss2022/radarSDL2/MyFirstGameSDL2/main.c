#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <ctype.h>
#include "SDL.h"
#include "SDL_image.h"
#include "SDL_net.h"

#define PI 3.14159

#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))

#define SCREEN_WIDTH 1280
#define SCREEN_HEIGHT 720

#define SOCKET_NUM 8
#define PORT 4123
#define MAX_PACKET_SIZE 512

#define ERROR 0xff

typedef struct {
	SDL_Window* window;
	SDL_Renderer* renderer;
	int objDetected;
	int susDetected;
}App;

typedef struct {
	int x;
	int y;
	int w;
	int h;
	int detected;
	SDL_Texture* texture;
}Entity;

typedef struct {
	int timestamp;
	int distance; // -1 non valido, [0, 3500]mm valido
}Packet; // NB: a seconda del canale su cui viene inviato, rappresenta il lato del radar

App app;
Entity radar;
Entity object;
Entity sus;
IPaddress* ipAddress;
Uint16 ports[SOCKET_NUM];
UDPsocket sock;
SDLNet_SocketSet socketset;
UDPsocket udpsocket[SOCKET_NUM];
UDPpacket* recvPacket;

void initSDL();
void initSDLNet();
void cleanup();
SDL_Texture* loadTexture(char* filename);
void prepareScene();
void presentScene();
void blit(SDL_Texture* texture, int x, int y);
void initRadar();
void initObject();
void initSus();
void doRadar();
void detectObject(int x, int y);
void detectSus(int x, int y);
void doInput();
void doReceive();
static void capFrameRate(long* then, float* remainder);

int main()
{
	long then;
	float remainder;

	then = SDL_GetTicks();

	memset(&app, 0, sizeof(App));
	memset(&radar, 0, sizeof(Entity));

	initSDL();
	initSDLNet();
	atexit(cleanup);
	
	initRadar();
	initObject();
	initSus();

	int l = radar.w / 2;
	int x1 = SCREEN_WIDTH / 2, y1 = SCREEN_HEIGHT / 2, x2 = SCREEN_WIDTH / 2, y2 = SCREEN_HEIGHT / 2;
	double angle = 0.0, a1, a2, a3;

	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE);

	uint32_t startTime = SDL_GetTicks();
	uint32_t stopTime;
	double elapsedTime;
	while (1)
	{
		prepareScene();
		blit(radar.texture, radar.x, radar.y);

		doReceive();
		doInput();

		if (angle == 360.0)
		{
			stopTime = SDL_GetTicks();
			angle = 0.0;
			elapsedTime = (stopTime - startTime) / 1000.0;
			//printf("\nELAPSED TIME: %f\n\n", elapsedTime); // test
			startTime = SDL_GetTicks();
			app.objDetected = 0;
			app.susDetected = 0;
			if (object.detected)
				app.objDetected = 1;
			if (sus.detected)
				app.susDetected = 1;
		}

		a1 = angle * (double)(PI / 180.0);
		a2 = (angle - 3.0) * (double)(PI / 180.0);
		a3 = (angle - 6.0) * (double)(PI / 180.0);
		
		x2 = x1 + (cos(a1) * l);
		y2 = y1 + (sin(a1) * l);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE);
		SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);
		//printf("x2=%d, y2=%d, angle=%3.2f, a=%3.2f, cos(a)=%f, sin(a)=%f\n", x2, y2, angle, a1, cos(a1), sin(a1)); // test


		x2 = x1 + (cos(a2) * l);
		y2 = y1 + (sin(a2) * l);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE/2);
		SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);
		//printf("x2=%d, y2=%d, angle=%3.2f, a=%3.2f, cos(a)=%f, sin(a)=%f\n", x2, y2, angle, a1, cos(a1), sin(a1)); // test


		x2 = x1 + (cos(a3) * l);
		y2 = y1 + (sin(a3) * l);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE/3);
		SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);
		//printf("x2=%d, y2=%d, angle=%3.2f, a=%3.2f, cos(a)=%f, sin(a)=%f\n", x2, y2, angle, a1, cos(a1), sin(a1)); // test

		angle += 1.0;

		
		if (app.objDetected)
		{
			detectObject(radar.x, radar.y);
			blit(object.texture, object.x, object.y);
		}
		if (app.susDetected)
		{
			detectSus(radar.x, radar.y);
			blit(sus.texture, sus.x, sus.y);
		}

		presentScene();

		capFrameRate(&then, &remainder);
	}

	return 0;
}

void initSDL()
{
	int rendererFlags, windowFlags;
	rendererFlags = SDL_RENDERER_ACCELERATED;
	windowFlags = 0;

	// init SDL Video
	if (SDL_Init(SDL_INIT_VIDEO) < 0)
	{
		printf("Couldn't initialize SDL: %s\n", SDL_GetError());
		exit(1);
	}

	if(!(app.window = SDL_CreateWindow("SDL2 Radar", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, windowFlags)))
	{
		printf("Failed to open% d x% d window : % s\n", SCREEN_WIDTH, SCREEN_HEIGHT, SDL_GetError());
		exit(1);
	}

	SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "linear");

	if(!(app.renderer = SDL_CreateRenderer(app.window, -1, rendererFlags)))
	{
		printf("Failed to create renderer: %s\n", SDL_GetError());
		exit(1);
	}

	// Init SDL Image
	if (IMG_Init(IMG_INIT_PNG | IMG_INIT_JPG) < 0)
	{
		printf("Couldn't initialize SDLImage: %s\n", SDL_GetError());
		exit(1);
	}
}
void initSDLNet()
{
	int i, numused;

	// init SDL Net
	if (SDLNet_Init() < 0)
	{
		printf("Couldn't initialize SDLNet: %s\n", SDLNet_GetError());
		exit(1);
	}

	if (SDLNet_ResolveHost(&ipAddress, NULL, 4123) == -1) {
		fprintf(stderr, "ER: SDLNet_ResolveHost: %sn", SDLNet_GetError());
		exit(-1);
	}

	// Create a socket set
	if (!(socketset = SDLNet_AllocSocketSet(SOCKET_NUM)))
	{
		printf("SDLNet_AllocSocketSet: %s\n", SDLNet_GetError());
		exit(1); //most of the time this is a major error, but do what you want.
	}

	// Open sockets (server): faccio una socket per ciascun lato (8)
	for (i = 0; i < 8; i++)
	{
		udpsocket[i] = SDLNet_UDP_Open(PORT + i);
		if (!udpsocket[i])
		{
			printf("SDLNet_UDP_Open[%d]: %s\n", i, SDLNet_GetError());
			exit(2);
		}
		printf("listening on 0.0.0.0:%d\n", PORT + i);

		numused = SDLNet_UDP_AddSocket(socketset, udpsocket[i]);
		if (numused == -1) {
			printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
			exit(2);
		}
	}

	if(!(recvPacket = SDLNet_AllocPacket(MAX_PACKET_SIZE)))
	{
		printf("Could not allocate packet\n");
		exit(2);
	}
	
}
void cleanup()
{
	SDL_DestroyRenderer(app.renderer);
	SDL_DestroyWindow(app.window);

	SDLNet_FreeSocketSet(socketset);

	SDLNet_Quit();
	SDL_Quit();
}

void initRadar()
{
	radar.texture = loadTexture("radar.png");
	SDL_QueryTexture(radar.texture, NULL, NULL, &radar.w, &radar.h);

	radar.x = SCREEN_WIDTH / 2;
	radar.y = SCREEN_HEIGHT / 2;
}
void initObject()
{
	object.texture = loadTexture("object.png");
	SDL_QueryTexture(object.texture, NULL, NULL, &object.w, &object.h);

	object.x = SCREEN_WIDTH / 2;
	object.y = SCREEN_HEIGHT / 2;
}
void initSus()
{
	sus.texture = loadTexture("sus_object.png");
	SDL_QueryTexture(sus.texture, NULL, NULL, &sus.w, &sus.h);

	sus.x = SCREEN_WIDTH / 2;
	sus.y = SCREEN_HEIGHT / 2;
}

SDL_Texture* loadTexture(char* filename)
{
	SDL_Texture* texture;

	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Loading %s", filename);

	texture = IMG_LoadTexture(app.renderer, filename);

	return texture;
}

void prepareScene()
{
	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE); // SDL_ALPHA_OPAQUE = 255
	SDL_RenderClear(app.renderer);
}
void presentScene()
{
	SDL_RenderPresent(app.renderer);
}
void blit(SDL_Texture* texture, int x, int y)
{
	SDL_Rect dest;

	dest.x = x;
	dest.y = y;
	SDL_QueryTexture(texture, NULL, NULL, &dest.w, &dest.h);
	dest.x -= (dest.w / 2);
	dest.y -= (dest.h / 2);

	SDL_RenderCopy(app.renderer, texture, NULL, &dest, NULL, SDL_FLIP_NONE);
}

void doRadar()
{
	blit(radar.texture, radar.x, radar.y);
}
void detectObject(int x, int y)
{
	object.x = x + radar.w / 4;
	object.y = y;
}
void detectSus(int x, int y)
{
	sus.x = x + radar.w / 4;
	sus.y = y;
}

void doKeyUp(SDL_KeyboardEvent* event)
{
	if(event->repeat == 0)
	{
		if(event->keysym.sym == SDLK_SPACE) // event->keysym.scancode == SDL_SCANCODE_DOWN
		{
			object.detected = 0;
		}
		if (event->keysym.sym == SDLK_s) // event->keysym.scancode == SDL_SCANCODE_DOWN
		{
			sus.detected = 0;
		}
	}
}

// simula la rilevazione di un oggetto
void doKeyDown(SDL_KeyboardEvent* event)
{
	if(event->repeat == 0)
	{
		if(event->keysym.sym == SDLK_SPACE)
		{
			object.detected = 1;
		}
		if (event->keysym.sym == SDLK_s) // event->keysym.scancode == SDL_SCANCODE_DOWN
		{
			sus.detected = 1;
		}
	}
}

void doInput()
{
	SDL_Event event;

	while (SDL_PollEvent(&event))
	{
		switch (event.type)
		{
		case SDL_QUIT:
			exit(0);
			break;

		case SDL_KEYDOWN:
			doKeyDown(&event.key);
			break;

		case SDL_KEYUP:
			doKeyUp(&event.key);
			break;

		default:
			break;
		}
	}
}

void doReceive()
{
	int numready, numpkts, i;
	char tmp[MAX_PACKET_SIZE + 1];

	numready = SDLNet_CheckSockets(socketset, 0);
	if (numready == -1) {
		printf("SDLNet_CheckSockets: %s\n", SDLNet_GetError());
		
		// most of the time this is a system error, where perror might help.
		perror("SDLNet_CheckSockets");
	}
	else if (numready) {
		printf("There are %d sockets with activity!\n", numready);
		
		// check all sockets with SDLNet_SocketReady and handle the active ones.
		for (i = 0; i < SOCKET_NUM; i++)
		{
			if (SDLNet_SocketReady(udpsocket[i])) {
				numpkts = SDLNet_UDP_Recv(udpsocket, &recvPacket);
				if (numpkts) {
					recvPacket->data;

					memcpy(tmp, recvPacket->data, recvPacket->len);
					tmp[recvPacket->len] = '\0';
					printf("[%d] Received: %s\n", i, tmp);
				}
			}
		}
		
	}
}

static void capFrameRate(long* then, float* remainder)
{
	long wait, frameTime;

	wait = 1 + *remainder;

	*remainder -= (int)*remainder;

	frameTime = SDL_GetTicks() - *then;

	wait -= frameTime;

	if (wait < 1)
	{
		wait = 1;
	}

	SDL_Delay(wait);

	*remainder += 0.667;

	*then = SDL_GetTicks();
}
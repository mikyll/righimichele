#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <ctype.h>
#include "SDL.h"
#include "SDL_image.h"

#define PI 3.14159

#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))

#define SCREEN_WIDTH 1280
#define SCREEN_HEIGHT 720

typedef struct {
	SDL_Window* window;
	SDL_Renderer* renderer;
	int sonar;
}App;

typedef struct {
	int x;
	int y;
}SpawnPoint;

typedef struct {
	int x;
	int y;
	int w;
	int h;
	int detect;
	SDL_Texture* texture;
}Entity;

App app;
SpawnPoint spawnPoints[4];
Entity radar;
Entity object;
int tot_points;

void initSDL();
void cleanup();
SDL_Texture* loadTexture(char* filename);
void prepareScene();
void presentScene();
void blit(SDL_Texture* texture, int x, int y);
void initRadar();
void initObject();
void doRadar();
void detectObject();
void doInput();
static void capFrameRate(long* then, float* remainder);

int main()
{
	long then;
	float remainder;

	then = SDL_GetTicks();

	memset(&app, 0, sizeof(App));
	memset(&radar, 0, sizeof(Entity));

	initSDL();
	atexit(cleanup);
	
	initRadar();
	initObject();

	int l = radar.w / 2;
	int x1 = SCREEN_WIDTH / 2, y1 = SCREEN_HEIGHT / 2, x2 = SCREEN_WIDTH / 2, y2 = SCREEN_HEIGHT / 2;
	double angle = 0.0, a1, a2, a3;

	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE);
	SDL_RenderClear(app.renderer);

	//prepareScene();

	SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE);
	SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);

	presentScene();
	uint32_t startTime = SDL_GetTicks();
	uint32_t stopTime;
	double elapsedTime;
	while (1)
	{
		prepareScene();
		blit(radar.texture, radar.x, radar.y);

		doInput();

		if (angle == 360.0)
		{
			stopTime = SDL_GetTicks();
			angle = 0.0;
			elapsedTime = (stopTime - startTime) / 1000.0;
			//printf("\nELAPSED TIME: %f\n\n", elapsedTime); // test
			startTime = SDL_GetTicks();
			radar.detect = 0;
			if (app.sonar)
			{
				radar.detect = 1;
			}
		}

		a1 = angle * (double)(PI / 180.0);
		a2 = (angle - 0.1) * (double)(PI / 180.0);
		a3 = (angle - 0.2) * (double)(PI / 180.0);
		//printf("x2=%d, y2=%d, angle=%3.2f, a=%3.2f, cos(a)=%f, sin(a)=%f\n", x2, y2, angle, a1, cos(a1), sin(a1)); // test
		
		x2 = x1 + (cos(a1) * l);
		y2 = y1 + (sin(a1) * l);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE);
		SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);

		x2 = x1 + (cos(a2) * l);
		y2 = y1 + (sin(a2) * l);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE/2);
		SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);

		x2 = x1 + (cos(a3) * l);
		y2 = y1 + (sin(a3) * l);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE/3);
		SDL_RenderDrawLine(app.renderer, x1, y1, x2, y2);

		angle += 1.0;

		
		if (radar.detect)
		{
			detectObject();
			blit(object.texture, object.x, object.y);
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

	if (SDL_Init(SDL_INIT_VIDEO) < 0)
	{
		printf("Couldn't initialize SDL: %s\n", SDL_GetError());
		exit(1);
	}

	if(!(app.window = SDL_CreateWindow("My First SDL2 Game", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, windowFlags)))
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

	IMG_Init(IMG_INIT_PNG | IMG_INIT_JPG);
}
void initRadar()
{
	radar.texture = loadTexture("radar.png");
	SDL_QueryTexture(radar.texture, NULL, NULL, &radar.w, &radar.h);

	radar.x = SCREEN_WIDTH / 2;
	radar.y = SCREEN_HEIGHT / 2;
	radar.detect = 0;
}
void initObject()
{
	object.texture = loadTexture("object.png");
	SDL_QueryTexture(object.texture, NULL, NULL, &object.w, &object.h);

	object.x = SCREEN_WIDTH / 2;
	object.y = SCREEN_HEIGHT / 2;
}
void cleanup()
{
	SDL_DestroyRenderer(app.renderer);

	SDL_DestroyWindow(app.window);

	SDL_Quit();
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
void detectObject()
{
	object.x = radar.x + radar.w / 4;
	object.y = radar.y;
}

void doKeyUp(SDL_KeyboardEvent* event)
{
	if(event->repeat == 0)
	{
		if(event->keysym.sym == SDLK_SPACE) // event->keysym.scancode == SDL_SCANCODE_DOWN
		{
			app.sonar = 0;
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
			app.sonar = 1;
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

void getMessage()
{
	// check if there are messages from gpio ... (read buffer length, if there's some value to read, reads it);
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
#include "main.h"

/*void doReceive();
void detectSus(int x, int y);
void detectObj(int nSock, float distance);

void drawRadar();
void drawRadarLine();
void drawObject(int dir);
void drawSus();
void drawObjectText(int dir, int objN, int offset);
void drawSusText(int offset);
void drawFPStext(int fps);*/

static void capFrameRate(long* then, float* remainder);

static int t = 1000 / FPS;
static int r;

int main(int argc, char** argv)
{
	long then;
	float remainder;

	r = (1000 / FPS) - (float)t;

	int i, j, k;
	Uint64 start, end;
	float elapsed;

	memset(&app, 0, sizeof(App));

	if (argc == 2 && strcmp(argv[1], "-d") == 0)
		app.debug = 1;

	srand(time(NULL));

	// Init SDL2 libraries
	initSDL();
	initSDLNet();
	initSDLMixer();
	initSDLTtf();

	atexit(cleanup);

	initStage();

	initInteraction();

	then = SDL_GetTicks();
	remainder = 0;

	while (1)
	{
		start = SDL_GetPerformanceCounter();

		prepareScene();

		doInput();

		doReceive();

		app.delegate.logic();

		app.delegate.draw();

		presentScene();

		capFrameRate(&then, &remainder);

		end = SDL_GetPerformanceCounter();
		elapsed = (end - start) / (float)SDL_GetPerformanceFrequency();
		((int)radarLine->angle % (360 / DIR_NUM)) == 0 ? stage.fps = (int)1.0f / elapsed : stage.fps;
	}

	return 0;
}

static void capFrameRate(long* then, float* remainder)
{
	long wait, frameTime;

	wait = t + *remainder;

	*remainder -= (int)*remainder;

	frameTime = SDL_GetTicks() - *then;

	wait -= frameTime;

	if (wait < 1)
	{
		wait = 1;
	}

	SDL_Delay(wait);

	*remainder += r;

	*then = SDL_GetTicks();
}

/*static void capFrameRate(long* then, float* remainder)
{
	long wait, frameTime;

	wait = *remainder;

	*remainder -= (int)*remainder;

	frameTime = SDL_GetTicks() - *then;

	wait -= frameTime;

	if (wait < 1)
	{
		wait = 1;
	}

	SDL_Delay(wait);

	*remainder += 16.667; // caps FPS to ~60 (remainder: 1000 / 60 = 16.66667)
	//*remainder += 5; // a complete radar cycle in ~2 sec
	//*remainder += 2.25; // a complete radar cycle in ~1 sec

	*then = SDL_GetTicks();
}*/
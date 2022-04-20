#include "main.h"

static void doArguments(int argc, char** argv);
static void capFrameRate(long* then, float* remainder);

static long t = 1000 / FPS;
static int r;

int main(int argc, char** argv)
{
	long then;
	float remainder;
	Uint64 start, end;
	float elapsed;

	memset(&app, 0, sizeof(App));

	doArguments(argc, argv);

	r = (1000 / FPS) - (float)t;

	srand(time(NULL));

	// Init SDL2 libraries
	initSDL();
	initSDLnet();
	initSDLmixer();
	initSDLttf();

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
		((int)radarLine->angle % (360 / 8)) == 0 ? stage.fps = (int)1.0f / elapsed : stage.fps;
	}

	return 0;
}

static void doArguments(int argc, char** argv)
{
	int i;

	for (i = 1; i < argc; i++)
	{
		if (STRCMP(argv[i], == , "-d") || STRCMP(argv[i], == , "--debug"))
		{
			app.args.flags[FLAG_DEBUG] = 1;
			app.args.debug = 1;
		}
		else if (STRCMP(argv[i], == , "-s") || STRCMP(argv[i], == , "--sounds"))
		{
			app.args.flags[FLAG_SOUNDS] = 1;
			app.args.sounds = 1;
		}
		else if (STRCMP(argv[i], == , "--tcp"))
		{
			app.args.flags[FLAG_TCP_SERVER_PORT] = 1;
			i++;
			app.args.TCPport = atoi(argv[i]);
			if (app.args.TCPport < 1024 || app.args.TCPport > 65535 || app.args.TCPport == DEFAULT_UDP_PORT)
			{
				// use default port
				app.args.TCPport = DEFAULT_TCP_SERVER_PORT;
			}
		}
		else if (STRCMP(argv[i], == , "--udp"))
		{
			app.args.flags[FLAG_UDP_PORT] = 1;
			i++;
			app.args.UDPport = atoi(argv[i]);
			if (app.args.UDPport < 1024 || app.args.UDPport > 65535 || app.args.UDPport == DEFAULT_TCP_SERVER_PORT)
			{
				// use default port
				app.args.UDPport = DEFAULT_UDP_PORT;
			}
		}
		else
		{
			app.args.flags[FLAG_ERR] = 1;
			break;
		}
	}

	// test
	printf("app.args.flags[FLAG_DEBUG]=%d - app.args.debug=%d\n", app.args.flags[FLAG_DEBUG], app.args.debug);

	if (app.args.flags[FLAG_ERR])
	{
		// ignore(?)
	}
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
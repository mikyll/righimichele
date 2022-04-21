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
	initSDLttf();
	if(!app.args.mute)
		initSDLmixer();

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
	int i, j;

	for (i = 1, j = 1; i < argc; i++, j++)
	{
		// Option -c
		if (STRCMP(argv[i], == , "-c") || STRCMP(argv[i], == , "--max-coordinates-text"))
		{
			app.args.flags[FLAG_MAX_COORDS_TEXT] = 1;
			app.args.maxCoordinatesText = 1;
			i++;

			if (i == argc)
			{
				app.args.flags[FLAG_ERR] = 1;

				// Use default num
				SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' must be followed by a valid number (0 < NUM <= 20). Default one will be used (%d).\n", j, argv[i - 1], DEFAULT_MAX_COORDS_TEXT);
				app.args.maxCoordinatesText = DEFAULT_MAX_COORDS_TEXT;
				break;
			}
			app.args.maxCoordinatesText = atoi(argv[i]);
			if (app.args.maxCoordinatesText <= 0 || app.args.maxCoordinatesText > 20)
			{
				app.args.flags[FLAG_ERR] = 1;

				// Use default num
				SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' must be followed by a valid number (0 < NUM <= 20). Default one will be used (%d).\n", j, argv[i - 1], DEFAULT_MAX_COORDS_TEXT);
				app.args.maxCoordinatesText = DEFAULT_MAX_COORDS_TEXT;
			}
		}
		// Option -d
		else if (STRCMP(argv[i], == , "-d") || STRCMP(argv[i], == , "--debug"))
		{
			app.args.flags[FLAG_DEBUG] = 1;
			app.args.debug = 1;
		}
		else if (STRCMP(argv[i], == , "-f") || STRCMP(argv[i], == , "--fps"))
		{
			app.args.flags[FLAG_FPS] = 1;
			app.args.fps = 1;
		}
		// Option -m
		else if (STRCMP(argv[i], == , "-m") || STRCMP(argv[i], == , "--mute"))
		{
			app.args.flags[FLAG_MUTE] = 1;
			app.args.mute = 1;
		}
		// Option -t
		else if (STRCMP(argv[i], == , "-t") || STRCMP(argv[i], == , "--tcp-port"))
		{
			app.args.flags[FLAG_TCP_SERVER_PORT] = 1;
			i++;

			if (i == argc)
			{
				app.args.flags[FLAG_ERR] = 1;

				// Use default port
				SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' must be followed by a port. Default one will be used (%d).\n", j, argv[i - 1], DEFAULT_TCP_SERVER_PORT);
				app.args.TCPport = DEFAULT_TCP_SERVER_PORT;
				break;
			}
			app.args.TCPport = atoi(argv[i]);
			if (app.args.TCPport < 1024 || app.args.TCPport > 65535 || app.args.flags[FLAG_UDP_PORT] ? app.args.TCPport == app.args.UDPport : app.args.TCPport == DEFAULT_UDP_PORT)
			{
				app.args.flags[FLAG_ERR] = 1;

				// Use default port
				SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' must be followed by a valid port (1024 >= PORT <= 65535). Default one will be used (%d).\n", j, argv[i - 1], DEFAULT_TCP_SERVER_PORT);
				app.args.TCPport = DEFAULT_TCP_SERVER_PORT;
			}
		}
		// Option -u
		else if (STRCMP(argv[i], == , "-u") || STRCMP(argv[i], == , "--udp-port"))
		{
			app.args.flags[FLAG_UDP_PORT] = 1;
			i++;

			if (i == argc)
			{
				app.args.flags[FLAG_ERR] = 1;

				// Use default port
				SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' must be followed by a port. Default one will be used (%d).\n", j, argv[i - 1], DEFAULT_UDP_PORT);
				app.args.UDPport = DEFAULT_UDP_PORT;
				break;
			}
			app.args.UDPport = atoi(argv[i]);
			if (app.args.UDPport < 1024 || app.args.UDPport > 65535 || app.args.flags[FLAG_TCP_SERVER_PORT] ? app.args.UDPport == app.args.TCPport : app.args.UDPport == DEFAULT_TCP_SERVER_PORT)
			{
				app.args.flags[FLAG_ERR] = 1;

				// Use default port
				SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' must be followed by a valid port (1024 >= PORT <= 65535). Default one will be used (%d).\n", j, argv[i - 1], DEFAULT_UDP_PORT);
				app.args.UDPport = DEFAULT_UDP_PORT;
			}
		}
		// Option ERROR
		else
		{
			app.args.flags[FLAG_ERR] = 1;
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "Parameter #%d '%s' is not a valid option.", j, argv[i]);
			continue;
		}
	}

	if (app.args.flags[FLAG_ERR])
	{
		printf("\nUSAGE\n\t%s [OPTION]...\n\n", argv[0]);
		printf("OPTIONS\n");
		printf("\t -c, --max-coordinates-text <num>\n\t\tspecify the maximum number of coordinates that can be shown on screen for the detected obstacles\n\n");
		printf("\t -d, --debug\n\t\tspecifies that program is running in debug mode, and prints useful information\n\n");
		printf("\t -f, --fps\n\t\tshow the FPS\n\n");
		printf("\t -m, --mute\n\t\tdisable the audio for the program\n\n");
		printf("\t -t, --tcp-port <port>\n\t\tset the port to be used to receive TCP connections (default is %d)\n\n", DEFAULT_TCP_SERVER_PORT);
		printf("\t -u, --udp-port <port>\n\t\tset the port to be used to receive UDP packets (default is %d)\n\n", DEFAULT_UDP_PORT);
	}
	if (!app.args.flags[FLAG_MAX_COORDS_TEXT])
		app.args.maxCoordinatesText = DEFAULT_MAX_COORDS_TEXT;
	if (!app.args.flags[FLAG_TCP_SERVER_PORT])
		app.args.TCPport = DEFAULT_TCP_SERVER_PORT;
	if (!app.args.flags[FLAG_UDP_PORT])
		app.args.UDPport = DEFAULT_UDP_PORT;
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
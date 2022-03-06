#include "init.h"

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

	if (!(app.window = SDL_CreateWindow("SDL2 Radar", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, windowFlags)))
	{
		printf("Failed to open% d x% d window : % s\n", SCREEN_WIDTH, SCREEN_HEIGHT, SDL_GetError());
		exit(1);
	}

	SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, "linear");

	if (!(app.renderer = SDL_CreateRenderer(app.window, -1, rendererFlags)))
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

void cleanup()
{
	SDL_DestroyRenderer(app.renderer);
	SDL_DestroyWindow(app.window);

	SDLNet_FreeSocketSet(socketset);

	SDLNet_Quit();
	SDL_Quit();
}
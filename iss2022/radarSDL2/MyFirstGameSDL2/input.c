#include "input.h"

void doKeyUp(SDL_KeyboardEvent* event)
{
	if (event->repeat == 0)
	{
		if (event->keysym.sym == SDLK_s) // event->keysym.scancode == SDL_SCANCODE_DOWN
		{
			app.susDetected = 0;
		}
	}
}

// simula la rilevazione di un oggetto
void doKeyDown(SDL_KeyboardEvent* event)
{
	if (event->repeat == 0)
	{
		if (event->keysym.sym == SDLK_s) // event->keysym.scancode == SDL_SCANCODE_DOWN
		{
			app.susDetected = 1;
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
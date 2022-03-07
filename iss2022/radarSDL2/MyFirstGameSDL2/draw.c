#include "draw.h"

SDL_Texture* loadTexture(char* filename);

void initRadar()
{
	radar.texture = loadTexture("gfx/radar.png");
	SDL_QueryTexture(radar.texture, NULL, NULL, &radar.w, &radar.h);

	radar.x = SCREEN_WIDTH / 2;
	radar.y = SCREEN_HEIGHT - radar.h / 2;
	radar.angle = 0.0;
	radar.l = radar.w / 2;
}
void initObjects()
{
	int i;
	for (i = 0; i < SOCKET_NUM; i++)
	{
		objects[i].texture = loadTexture("gfx/object.png");
		SDL_QueryTexture(objects[i].texture, NULL, NULL, &objects[i].w, &objects[i].h);

		objects[i].detected = 0;
		objects[i].x = SCREEN_WIDTH / 2;
		objects[i].y = SCREEN_HEIGHT / 2;
	}
}
void initSus()
{
	sus.texture = loadTexture("gfx/sus_object.png");
	SDL_QueryTexture(sus.texture, NULL, NULL, &sus.w, &sus.h);

	sus.detected = 0;
	sus.x = SCREEN_WIDTH / 2;
	sus.y = SCREEN_HEIGHT / 2;
}

void prepareScene(void)
{
	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE);
	SDL_RenderClear(app.renderer);
}

void presentScene(void)
{
	SDL_RenderPresent(app.renderer);
}

SDL_Texture* loadTexture(char* filename)
{
	SDL_Texture* texture;

	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Loading %s", filename);

	texture = IMG_LoadTexture(app.renderer, filename);

	return texture;
}

void blit(SDL_Texture* texture, int x, int y, int center)
{
	SDL_Rect dest;

	dest.x = x;
	dest.y = y;
	SDL_QueryTexture(texture, NULL, NULL, &dest.w, &dest.h);
	
	if (center)
	{
		dest.x -= (dest.w / 2);
		dest.y -= (dest.h / 2);
	}

	SDL_RenderCopy(app.renderer, texture, NULL, &dest, NULL, SDL_FLIP_NONE);
}

void blitScaled(SDL_Texture* texture, int x, int y, float sx, float sy)
{
	SDL_Rect dest;

	dest.x = x;
	dest.y = y;
	SDL_QueryTexture(texture, NULL, NULL, &dest.w, &dest.h);
	if (sx > 0.0 && sy > 0.0)
	{
		dest.w = (int)(dest.w * sx);
		dest.h = (int)(dest.h * sy);
	}

	SDL_RenderCopy(app.renderer, texture, NULL, &dest);
}
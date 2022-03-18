#include "draw.h"

SDL_Texture* loadTexture(char* filename);

void initRadar()
{
	radar.texture = loadTexture("gfx/radar.png");
	SDL_QueryTexture(radar.texture, NULL, NULL, &radar.w, &radar.h);

	radar.x = WINDOW_WIDTH / 2;
	radar.y = WINDOW_HEIGHT - radar.h / 2;
	radar.angle = 0.0;
	radar.l = radar.w / 2;
}
void initRadarLine()
{
	radarLine.texture = loadTexture("gfx/radar_line.png");
	SDL_QueryTexture(radarLine.texture, NULL, NULL, &radarLine.w, &radarLine.h);

	radarLine.x = WINDOW_WIDTH / 2;
	radarLine.y = WINDOW_HEIGHT - radarLine.h / 2;
	radarLine.angle = 0.0;
}
void initObjects()
{
	int i;
	for (i = 0; i < DIR_NUM; i++)
	{
		objects[i].texture = loadTexture("gfx/object.png");
		SDL_QueryTexture(objects[i].texture, NULL, NULL, &objects[i].w, &objects[i].h);

		objects[i].detected = 0;
		objects[i].x = WINDOW_WIDTH / 2;
		objects[i].y = WINDOW_HEIGHT / 2;
	}
}
void initSus()
{
	sus.texture = loadTexture("gfx/sus_object.png");
	SDL_QueryTexture(sus.texture, NULL, NULL, &sus.w, &sus.h);

	sus.detected = 0;
	sus.x = WINDOW_WIDTH / 2;
	sus.y = WINDOW_HEIGHT / 2;
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

	SDL_RenderCopy(app.renderer, texture, NULL, &dest);
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

void blitRotated(SDL_Texture* texture, int x, int y, int center, float angle)
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

	SDL_RenderCopyEx(app.renderer, texture, NULL, &dest, angle, NULL, SDL_FLIP_NONE);
}

void blitScaledRotated(SDL_Texture* texture, int x, int y, float sx, float sy, float angle)
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

	SDL_RenderCopyEx(app.renderer, texture, NULL, &dest, angle, NULL, SDL_FLIP_NONE);
}
#include "text.h"

static TTF_Font* font;
static SDL_Color white = { 255, 255, 255, 255 };

void initFonts();
TTF_Font* loadFont(char* filename);
SDL_Texture* toTexture(SDL_Surface* surface, int destroySurface);

void initSDLTtf()
{
	if (TTF_Init() < 0)
	{
		printf("Couldn't initialize SDL TTF: %s\n", SDL_GetError());
		exit(1);
	}
	initFonts();
}

void initFonts()
{
	font = loadFont("font/EnterCommand.ttf");
}

TTF_Font* loadFont(char* filename)
{
	TTF_Font* font;

	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Loading %s", filename);

	font = TTF_OpenFont(filename, FONT_SIZE);

	return font;
}

SDL_Texture* getTextTexture(char* text)
{
	SDL_Surface* surface;

	surface = TTF_RenderUTF8_Blended(font, text, white);

	return toTexture(surface, 1);
}

SDL_Texture* toTexture(SDL_Surface* surface, int destroySurface)
{
	SDL_Texture* texture;

	texture = SDL_CreateTextureFromSurface(app.renderer, surface);

	if (destroySurface)
	{
		SDL_FreeSurface(surface);
	}

	return texture;
}

void drawNormalText(SDL_Texture* text, int x, int y)
{
	blit(text, x, y, 0);
}

void drawScaledText(SDL_Texture* text, int x, int y, float sx, float sy)
{
	blitScaled(text, x, y, sx, sy);
}
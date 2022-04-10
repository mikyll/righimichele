#ifndef _text_h
#define _text_h

#include "common.h"

#include "SDL2/SDL_ttf.h"

extern void blit(SDL_Texture* texture, int x, int y, int center);
extern void blitScaled(SDL_Texture* texture, int x, int y, float sx, float sy);

extern App app;

#endif
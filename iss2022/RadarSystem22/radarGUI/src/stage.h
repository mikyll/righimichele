#ifndef _stage_h
#define _stage_h

#include "common.h"

extern SDL_Texture* loadTexture(char* filename);
extern void blit(SDL_Texture* texture, int x, int y, int center);
extern void blitRotated(SDL_Texture* texture, int x, int y, int center, float angle);
extern void playSound(int id, int channel);
extern SDL_Texture* getTextTexture(char* text);
extern void drawNormalText(SDL_Texture* text, int x, int y);
extern void drawScaledText(SDL_Texture* text, int x, int y, float sx, float sy);

extern App app;
extern Stage stage;
extern Interaction interaction;

#endif
#ifndef _main_h
#define _main_h

#include "common.h"

#include "SDL2/SDL_net.h"


extern void cleanup();
extern void initSDL();
extern void initSDLnet();
extern void initSDLmixer();
extern void initSDLttf();
extern void initStage();
extern void prepareScene();
extern void presentScene();
extern void doInput();

extern void initInteraction();
extern void doReceive();

App app;
Stage stage;
extern Entity* radarLine;

#endif
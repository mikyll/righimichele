#ifndef _net_h
#define _net_h

#include "common.h"
#include "SDL2/SDL_net.h"

void initInteraction();
void doReceive();

extern App app;

Interaction interaction;


#endif
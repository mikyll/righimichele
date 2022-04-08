#include "common.h"

#include "SDL2/SDL_net.h"

#define ACK_IP "192.168.1.4"

void initInteraction();
void doReceive();

extern App app;
extern Radar radar;

Interaction interaction;
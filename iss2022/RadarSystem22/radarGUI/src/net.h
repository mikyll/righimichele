#include "common.h"

#include "SDL2/SDL_net.h"

#define ACK_IP "192.168.1.4"

void initInteraction();

extern App app;
extern Radar radar;

Interaction interaction;

Uint16 ports[DIR_NUM];
UDPsocket sock;
SDLNet_SocketSet socketset;
UDPsocket udpSockets[DIR_NUM];
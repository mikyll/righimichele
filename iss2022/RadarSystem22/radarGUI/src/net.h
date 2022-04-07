#include "common.h"

#include "SDL2/SDL_net.h"

#define ACK_IP "192.168.1.4"

#define UDP_SOCKET_PORT		4000
#define SERVER_SOCKET_PORT	4001

#define MAX_MSG_LENGTH		1024

void initInteraction();
void doReceive();

extern App app;
extern Radar radar;

Interaction interaction;
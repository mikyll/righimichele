#include "common.h"

#include "SDL_net.h"

#define ACK_IP "192.168.1.4"

float receiveDistanceFromSocket(int nSock);

extern App app;
extern Radar radar;
extern Entity objects[DIR_NUM];

IPaddress* ipAddress;
Uint16 ports[DIR_NUM];
UDPsocket sock;
SDLNet_SocketSet socketset;
UDPsocket udpSockets[DIR_NUM];
UDPpacket* udpPackets[DIR_NUM];
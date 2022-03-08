#include "common.h"

#include "SDL_net.h"

#define ACK_IP "192.168.1.4"

float receiveDistanceFromSocket(int nSock);

extern App app;
extern Radar radar;
extern Entity objects[SOCKET_NUM];

IPaddress* ipAddress;
Uint16 ports[SOCKET_NUM];
UDPsocket sock;
SDLNet_SocketSet socketset;
UDPsocket udpSockets[SOCKET_NUM];
UDPpacket* udpPackets[SOCKET_NUM];
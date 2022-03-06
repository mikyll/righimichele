#include "common.h"

#include "SDL_net.h"

extern App app;
extern Radar radar;
extern Entity objects[SOCKET_NUM];
extern IPaddress* ipAddress;
extern Uint16 ports[SOCKET_NUM];
extern UDPsocket sock;
extern SDLNet_SocketSet socketset;
extern UDPsocket udpsocket[SOCKET_NUM];
extern UDPpacket* recvPacket[SOCKET_NUM];
extern float distance[SOCKET_NUM]; // CHECK (la distanza è in App)
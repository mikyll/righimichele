#include "common.h"

#include "SDL_net.h"

extern void initSDL(void);
extern void initSDLNet(void);
extern void cleanup(void);
extern void doInput(void);
extern void prepareScene(void);
extern void draw(void);
extern void presentScene(void);

App app;
Radar radar;
Entity objects[SOCKET_NUM];
Entity sus;
IPaddress* ipAddress;
Uint16 ports[SOCKET_NUM];
UDPsocket sock;
SDLNet_SocketSet socketset;
UDPsocket udpsocket[SOCKET_NUM];
UDPpacket* recvPacket[SOCKET_NUM];
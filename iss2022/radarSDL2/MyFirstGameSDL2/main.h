#include "common.h"

#include "SDL_net.h"

extern void initSDL(void);
extern void initSDLNetServer(int port);
extern void initSDLMixer(void);
extern void initSDLTtf(void);
extern void cleanup(void);
extern void doInput(void);
extern void prepareScene(void);
extern void presentScene(void);
extern void playSound(int id, int channel);
extern SDL_Texture* getTextTexture(char* text);
extern void drawNormalText(SDL_Texture* text, int x, int y);
extern void drawScaledText(SDL_Texture* text, int x, int y, float sx, float sy);

App app;
Radar radar;
Entity objects[SOCKET_NUM];
Entity sus;

extern IPaddress* ipAddress;
extern Uint16 ports[SOCKET_NUM];
extern UDPsocket sock;
extern SDLNet_SocketSet socketset;
extern UDPsocket udpSockets[SOCKET_NUM];
extern UDPpacket* udpPackets[SOCKET_NUM];

SDL_Texture* textFPS;
SDL_Texture* coordinates[SOCKET_NUM];
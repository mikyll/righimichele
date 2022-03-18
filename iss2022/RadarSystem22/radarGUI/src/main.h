#include "common.h"

#include "SDL2/SDL_net.h"

extern void initSDL(void);
extern void initSDLNetServer(int port);
extern void initSDLMixer(void);
extern void initSDLTtf(void);
extern void cleanup(void);
extern void doInput(void);
extern float receiveDistanceFromSocket(int nSock);
extern void prepareScene(void);
extern void presentScene(void);
extern void playSound(int id, int channel);
extern SDL_Texture* getTextTexture(char* text);
extern void drawNormalText(SDL_Texture* text, int x, int y);
extern void drawScaledText(SDL_Texture* text, int x, int y, float sx, float sy);
extern void blitRotated(SDL_Texture* texture, int x, int y, int center, float angle);

App app;
Radar radar;
Radar radarLine;
Entity objects[DIR_NUM];
Entity sus;

// net
/*extern IPaddress* ipAddress;
extern Uint16 ports[DIR_NUM];
extern UDPsocket sock;
extern SDLNet_SocketSet socketset;
extern UDPsocket udpSockets[DIR_NUM];
extern UDPpacket* udpPackets[DIR_NUM];*/

SDL_Texture* textFPS;
SDL_Texture* coordinates[DIR_NUM];
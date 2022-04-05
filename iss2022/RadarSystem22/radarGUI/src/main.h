#include "common.h"

#include "SDL2/SDL_net.h"


// Refactor
extern void cleanup(void);
extern void initSDL(void);
extern void initSDLNetServer(int port);
extern void initSDLMixer(void);
extern void initSDLTtf(void);
extern void initStage(void);
extern void prepareScene(void);
extern void presentScene(void);
extern void doInput();


extern void initInteraction();
extern void doReceive();

App app;
Stage stage;
extern Entity* radarLine;

// ---------------


/*extern void initSDL(void);
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

SDL_Texture* textFPS;
SDL_Texture* coordinates[DIR_NUM];*/
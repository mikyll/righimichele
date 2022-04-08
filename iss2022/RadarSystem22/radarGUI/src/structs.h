#include <SDL2/SDL_net.h>

typedef struct Entity Entity;
typedef struct Message Message;
typedef struct Distance Distance;

struct Message {
	char data[MAX_MSG_LENGTH];
	Message* next;
};

struct Distance {
	int distance;
	int angle;
	Distance* next;
};

typedef struct {
	void (*logic)(void);
	void (*draw)(void);
} Delegate;

typedef struct {
	SDL_Window* window;
	SDL_Renderer* renderer;
	Delegate delegate;
	int susDetected;
	int soundEnabled;
} App;

struct Entity {
	int x;
	int y;
	int w;
	int h;
	int angle;
	int alpha;
	Entity* next;
	SDL_Texture* texture;
};

typedef struct {
	int x;
	int y;
	int w;
	int h;
	int radius;
	float angle;
	SDL_Texture* texture;
}Radar;

typedef struct {
	int fps;
	Entity obstacleHead, * obstacleTail;
	Distance distanceHead, * distanceTail;
}Stage;

typedef struct {
	IPaddress* ipAddress;
	int used;
	SDLNet_SocketSet socketSet;
	SDLNet_GenericSocket sockets[MAX_SOCKET];
	int socketType[MAX_SOCKET];
	void (*receive)(void);
	Message messageHead, * messageTail; // linked list containing each distance received from clients
} Interaction;
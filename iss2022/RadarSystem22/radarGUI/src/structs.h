#include <SDL2/SDL_net.h>

typedef struct Entity Entity;
typedef struct Distance Distance;

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
	IPaddress* ipAddress;
	SDLNet_SocketSet socketset;
	SDLNet_GenericSocket sockets[MAX_SOCKET];
	Distance distanceHead, * distanceTail;
	void (*send)(void);
	void (*receive)(void);
} Interaction;

typedef struct {
	SDL_Window* window;
	SDL_Renderer* renderer;
	Delegate delegate;
	int susDetected;
	int soundEnabled;
}App;

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
	int fps;
	// detect queue;
	// text queue;
	Entity obstacleHead, * obstacleTail;
}Stage;

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
	int timestamp;
	int distance; // -1 non valido, [0, 200]mm valido
}Packet; // NB: a seconda del canale su cui viene inviato, rappresenta il lato del radar
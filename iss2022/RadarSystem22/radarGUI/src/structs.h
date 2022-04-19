#ifndef _structs_h
#define _structs_h

#include <SDL2/SDL_net.h>

typedef struct Entity Entity;
typedef struct Message Message;
typedef struct Distance Distance;
typedef struct ObstacleCoordinate ObstacleCoordinate;

typedef struct {
	void (*logic)(void);
	void (*draw)(void);
} Delegate;

typedef struct {
	SDL_Window* window;
	SDL_Renderer* renderer;
	Delegate delegate;
	int susDetected;
	int debug;
} App;

struct Message {
	char data[MAX_MSG_LENGTH];
	Message* next;
};

struct Distance {
	int distance;
	int angle;
	Distance* next;
};

struct ObstacleCoordinate {
	int distance;
	int angle;
	int health;
	SDL_Texture* text;
	ObstacleCoordinate* next;
};

typedef struct {
	ObstacleCoordinate* first;
	ObstacleCoordinate* last;
	int length;
	int capacity;
} ObstacleCoordinateQueue;

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
	Entity obstacleHead, * obstacleTail;
	Distance distanceHead, * distanceTail;
	ObstacleCoordinateQueue* obsCoordQ;
} Stage;

typedef struct {
	IPaddress* ipAddress;
	int used;
	SDLNet_SocketSet socketSet;
	SDLNet_GenericSocket sockets[MAX_SOCKET];
	int socketType[MAX_SOCKET];
	void (*receive)(void);
	Message messageHead, * messageTail; // linked list containing messages received from each client
} Interaction;

#endif
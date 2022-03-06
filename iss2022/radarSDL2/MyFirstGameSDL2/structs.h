typedef struct {
	SDL_Window* window;
	SDL_Renderer* renderer;
	int soundEnabled;
	int objDetected[SOCKET_NUM];
	int susDetected;
}App;

typedef struct {
	int x;
	int y;
	int w;
	int h;
	int l;
	float angle;
	SDL_Texture* texture;
}Radar;

typedef struct {
	int x;
	int y;
	int w;
	int h;
	int detected;
	SDL_Texture* texture;
}Entity;

typedef struct {
	int timestamp;
	int distance; // -1 non valido, [0, 200]mm valido
}Packet; // NB: a seconda del canale su cui viene inviato, rappresenta il lato del radar
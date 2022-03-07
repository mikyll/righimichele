#define SCREEN_WIDTH	520
#define SCREEN_HEIGHT	600

#define PI				3.14159

#define MAX_D			200.0
#define MIN_D			0.0

#define DEFAULT_IP		4123
#define DEFAULT_PORT	4123
#define SOCKET_NUM		8
#define MAX_PACKET_SIZE 512

#define MAX_SND_CHANNELS 8

#define FONT_SIZE    48

#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))
#define STRNCPY(dest, src, n) strncpy(dest, src, n); dest[n - 1] = '\0'

enum Directions
{
	D_E,	// EAST
	D_SE,	// SOUTH-EAST
	D_S,	// SOUTH
	D_SW,	// SOUTH-WEST
	D_W,	// WEST
	D_NW,	// NORTH-WEST
	D_N,	// NORTH
	D_NE,	// NORTH-EAST
	D_NUM,	// DIRECTIONS_NUM
};

enum AngleDegrees
{
	A_E =	360,	// EAST
	A_SE =	45,		// SOUTH-EAST
	A_S =	90,		// SOUTH
	A_SW =	135,	// SOUTH-WEST
	A_W =	180,	// WEST
	A_NW =	225,	// NORTH-WEST
	A_N =	270,	// NORTH
	A_NE =	315,	// NORTH-EAST
};

enum Channels
{
	CH_ANY = -1,
	CH_OBJ,
	CH_SUS,
};

enum Sounds
{
	SND_OBJ_DETECTED,
	SND_SUS_DETECTED,
	SND_MAX,
};

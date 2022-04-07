#define WINDOW_TITLE		"Radar GUI"

#define WINDOW_WIDTH		520
#define WINDOW_HEIGHT		600

#define FPS					60

#define CPS					2	// Cycle Per Second

#define ANGLE_INCREMENT		(360 / FPS / CPS)

#define PI					3.14159

#define SPC1 2.25	// 1 Second Per Cycle
#define SPC2 5		// 2 Seconds Per Cycle

#define MAX_D				200.0
#define MIN_D				0.0

#define DEFAULT_IP			"127.0.0.1"
#define DEFAULT_PORT		4000
#define ACK_PORT_OFFSET		100
#define MAX_PACKET_SIZE		512

#define MAX_SOCKET			4

#define MAX_SND_CHANNELS	8

#define FONT_SIZE			28

#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))
#define STRNCPY(dest, src, n) strncpy(dest, src, n); dest[n - 1] = '\0'

enum Directions
{
	DIR_E,		// EAST
	DIR_SE,		// SOUTH-EAST
	DIR_S,		// SOUTH
	DIR_SW,		// SOUTH-WEST
	DIR_W,		// WEST
	DIR_NW,		// NORTH-WEST
	DIR_N,		// NORTH
	DIR_NE,		// NORTH-EAST
	DIR_NUM,	// DIRECTIONS_NUM
};

enum Degrees
{
	DEG_E = 360,	// EAST
	DEG_SE = 45,	// SOUTH-EAST
	DEG_S = 90,		// SOUTH
	DEG_SW = 135,	// SOUTH-WEST
	DEG_W = 180,	// WEST
	DEG_NW = 225,	// NORTH-WEST
	DEG_N = 270,	// NORTH
	DEG_NE = 315,	// NORTH-EAST
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

enum SockType
{
	SOCK_UDP,
	SOCK_TCP,
};
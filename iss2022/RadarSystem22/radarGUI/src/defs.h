#ifndef _defs_h
#define _defs_h

#define WINDOW_TITLE			"Radar GUI"

#define WINDOW_WIDTH			600
#define WINDOW_HEIGHT			650
#define BOTTOM_OFFSET			20

#define FPS						60

#define SCP						2	// Cycle Per Second

#define ANGLE_INCREMENT			(360 / FPS / SCP)

#define PI						3.14159

#define SPC1					2.25	// 1 Second Per Cycle
#define SPC2					5		// 2 Seconds Per Cycle

#define MAX_D					200
#define MIN_D					0

#define MAX_NUM_COORDS			5

#define DEFAULT_UDP_PORT		4000
#define DEFAULT_TCP_SERVER_PORT	4001
#define ACK_PORT_OFFSET			100

#define MAX_PACKET_SIZE			512
#define MAX_MSG_LENGTH			64

#define MAX_SOCKET				4

#define MAX_SND_CHANNELS		8

#define FONT_SIZE				28


#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))
#define STRNCPY(dest, src, n) strncpy(dest, src, n); dest[n - 1] = '\0'

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
	SOCK_TCP_SERVER,
	SOCK_TCP,
};

#endif
#define SCREEN_WIDTH             520
#define SCREEN_HEIGHT            600

#define PI 3.14159

#define SOCKET_NUM 8
#define PORT 4123
#define MAX_PACKET_SIZE 512

#define MIN(a,b) (((a)<(b))?(a):(b))
#define MAX(a,b) (((a)>(b))?(a):(b))

#define MAX_SND_CHANNELS 8

enum
{
	CH_ANY = -1,
	CH_OBJECT,
	CH_SUS
};

enum
{
	SND_OBJECT_DETECTED,
	SND_SUS_DETECTED,
	SND_MAX
};

// enum...
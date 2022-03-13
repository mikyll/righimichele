#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <ctype.h>
#include <time.h>

#include "SDL2/SDL_net.h"
#include "SDL2/SDL.h"

#if(defined __unix__ && __arm__)
	#include <signal.h>
	#include <unistd.h>
	#include "wiringPi.h"
	
	#define GPIO_TRIGGER 4	// GPIO 23
	#define GPIO_ECHO 5		// GPIO 24
	#define GPIO_LED 6		// GPIO 25
#endif


enum Flags
{
	FLAG_IP,	// -i/--ip <ip_address>
	FLAG_PORT,	// -p/--port <port> || -d/--direction <direction>
	FLAG_RAND,	// -r/--random
	FLAG_DIST,	// -d/--distance <distance>
	FLAG_ERR,
	FLAG_MAX,
};

#define DEFAULT_IP "127.0.0.1"	//"192.168.137.1" //"192.168.1.3" //"192.168.50.189"
#define DEFAULT_SND_PORT 4000
#define ACK_PORT_OFFSET	100
#define DEFAULT_DLIM	20.0	// (cm)
#define TIMEOUT		5000		// (ms)
#define TIMER_INCREMENT	500		// (ms)

void printUsage(char** argv);
void cleanup();
float distance();
void turnOnLed();
void turnOffLed();
float randRange(float min, float max);

int main(int argc, char** argv)
{
	UDPsocket sendSocket, receiveSocket;
	IPaddress srvadd;
	UDPpacket* p;
	int argFlags[FLAG_MAX], quit, sndPort, rand, i, timer;
	float d, DLIM;
	char ip[16];

	for(i = 0; i < FLAG_MAX; i++)
		argFlags[i] = 0;

	// arguments check
	for (i = 1; i < argc; i++)
	{
		if (strcmp(argv[i], "-i") == 0 || strcmp(argv[i], "--ip") == 0)
		{
			if (argFlags[FLAG_IP])
			{
				argFlags[FLAG_ERR] = 1;
				break;
			}
			i++;
			strcpy(ip, argv[i]);
			argFlags[FLAG_IP] = 1;
		}
		else if (strcmp(argv[i], "-p") == 0 || strcmp(argv[i], "--port") == 0)
		{
			if (argFlags[FLAG_PORT])
			{
				argFlags[FLAG_ERR] = 1;
				break;
			}
			i++;
			sndPort = atoi(argv[i]);
			argFlags[FLAG_PORT] = 1;
		}
		/* // TO-DO
		else if (strcmp(argv[i], "-d") == 0 || strcmp(argv[i], "--direction") == 0)
		{
			if(argFlags[FLAG_PORT])
			{
				printUsage();
				exit(1);
			}
			i++;
			sndPort = 
			argFlags[FLAG_PORT] = 1;
		}*/
		else if (strcmp(argv[i], "-r") == 0 || strcmp(argv[i], "--random") == 0)
		{
			rand = 1;
			argFlags[FLAG_RAND] = 1;
		}
		else if (strcmp(argv[i], "-D") == 0 || strcmp(argv[i], "--distance") == 0)
		{
			if(argFlags[FLAG_DIST])
			{
				argFlags[FLAG_ERR] = 1;
				break;
			}
			i++;
			sscanf(argv[i], "%f", &DLIM);
			argFlags[FLAG_DIST] = 1;
		}
		else {
			argFlags[FLAG_ERR] = 1;
			break;
		}
	}

	if (argFlags[FLAG_ERR])
	{
		printUsage(argv);
		exit(1);
	}
	if (!argFlags[FLAG_IP])
		strcpy(ip, DEFAULT_IP);
	if (!argFlags[FLAG_PORT])
		sndPort = DEFAULT_SND_PORT;
	if (!argFlags[FLAG_RAND])
		rand = 0;
	if (!argFlags[FLAG_DIST])
		DLIM = DEFAULT_DLIM;

	// Init random seed
	srand(time(NULL));

#if(defined __unix__ && __arm__)
	// Setup wiringPi
	wiringPiSetup();
	pinMode(GPIO_TRIGGER, OUTPUT);
	pinMode(GPIO_ECHO, INPUT);
	pinMode(GPIO_LED, OUTPUT);

	// set handler for cleanup
	struct sigaction sigIntHandler;

	sigIntHandler.sa_handler = cleanup;
	sigemptyset(&sigIntHandler.sa_mask);
	sigIntHandler.sa_flags = 0;

	sigaction(SIGINT, &sigIntHandler, NULL);
#else
	rand = 1;
#endif

	printf("CLIENT UDP\n\n");

	// 1. Init SDL
	if (SDL_Init(0) != 0) {
		fprintf(stderr, "ER: SDL_Init: %sn", SDL_GetError());
		exit(EXIT_FAILURE);
	}
	if (SDLNet_Init() == -1) {
		fprintf(stderr, "ER: SDLNet_Init: %sn", SDLNet_GetError());
		exit(EXIT_FAILURE);
	}

	// 2. Open a socket on a random usable port
	if (!(sendSocket = SDLNet_UDP_Open(0)))
	{
		fprintf(stderr, "SDLNet_UDP_Open: %s\n", SDLNet_GetError());
		exit(EXIT_FAILURE);
	}
	if (!(receiveSocket = SDLNet_UDP_Open(sndPort + ACK_PORT_OFFSET)))
	{
		fprintf(stderr, "SDLNet_UDP_Open: %s\n", SDLNet_GetError());
		exit(EXIT_FAILURE);
	}

	// 3. Resolve the server address
	if (SDLNet_ResolveHost(&srvadd, ip, sndPort))
	{
		fprintf(stderr, "SDLNet_ResolveHost(%s %d): %s\n", ip, sndPort, SDLNet_GetError());
		exit(EXIT_FAILURE);
	}

	// 4. Allocate memory for packets
	if (!(p = SDLNet_AllocPacket(512)))
	{
		fprintf(stderr, "SDLNet_AllocPacket: %s\n", SDLNet_GetError());
		exit(EXIT_FAILURE);
	}

	// 5. Fill and send the packet
	while (1)
	{
#if(defined __unix__ && __arm__)
		d = (rand == 0 ? distance() : randRange(0.0, 320.0));
		printf("Distance: %3.1f cm", (float) d);
		
		// Turn ON/OFF led
		if (d <= DLIM)
		{
			turnOnLed()
		}
		else {
			turnOffLed()
		}
#else
	d = randRange(0.0, 320.0);
#endif

		char buffer[64];
		int ret = snprintf(buffer, sizeof buffer, "%3.1f", (float) d);
		if (ret < 0) {
			return EXIT_FAILURE;
		}

		p->data = buffer;
		p->address.host = srvadd.host; // Set the destination host
		p->address.port = srvadd.port; // And destination port
		p->len = strlen((char*)p->data) + 1;

		// Send distance
		printf("Sending packet to %s:%d...\n", ip, sndPort);
		if(!SDLNet_UDP_Send(sendSocket, -1, p)) // This sets the p->channel
		{
			printf("ERROR: SDLNet_UDP_Send: %s\n", SDLNet_GetError());
			return 1;
		}

		// Receive ACK (wait)
		printf("Waiting ACK on port %d...\n", sndPort + ACK_PORT_OFFSET);
		for (timer = 0;;)
		{
			if (SDLNet_UDP_Recv(receiveSocket, p))
			{
				printf("\nReceived ACK\n\n");
				break;
			}
			else if (timer < TIMEOUT)
			{
				timer += 500;
				SDL_Delay(TIMER_INCREMENT);
			}
			else {
				printf("\nTimeout\n\n");
				break;
			}
		}

		// Debug
		/*
		printf("UDP Packet incoming\n");
		printf("\tChan: %d\n", p->channel);
		printf("\tData: %s\n", (char*)p->data);
		printf("\tLen: %d\n", p->len);
		printf("\tMaxlen: %d\n", p->maxlen);
		printf("\tStatus: %d\n", p->status);
		printf("\tAddress: %x %x\n", p->address.host, p->address.port);
		*/
	}

	// 6. Free the memory and quit
	SDLNet_FreePacket(p);
	SDLNet_Quit();

	return 0;
}
void printUsage(char ** argv)
{
	printf("USAGE:\n\t%s [-i/--ip <ip_address>] [-p/--port <port>] [-r/--random] [-D/--distance <distance>]\n\n\n-i/--ip <ip_address>, speficies the IP address of the remote host on which send the packets (default is %s)\n\n-p/--port <port>, specifies the remote port on which send the packets (default is %d)\n\n-r/--random, specifies that the distance will be generated with a RNG\n\n-D/--distance <distance>, specifies the distance lower limit under which the led will light up (default is %3.1f)\n\n", argv[0], DEFAULT_IP, DEFAULT_SND_PORT, DEFAULT_DLIM);
}
float randRange(float min, float max)
{
	return min + rand() / (RAND_MAX / (max - min + 1) + 1);
}

#if(defined __unix__ && __arm__)
void cleanup()
{
	digitalWrite(GPIO_LED, LOW);
	digitalWrite(GPIO_TRIGGER, LOW);
	printf("\nCleanup...\n\n");
	exit(0);
}
// to calculate the distance:
float distance()
{
	float startTime, stopTime, timeElapsed;

	// Set TRIGGER to HIGH
	digitalWrite(GPIO_TRIGGER, HIGH);

	// Set TRIGGER to LOW (after 1 millisecond)
	delayMicroseconds(100);
	digitalWrite(GPIO_TRIGGER, LOW);

	// Save start time
	while (digitalRead(GPIO_ECHO) == 0)
		startTime = micros();

	// Save stop time
	while (digitalRead(GPIO_ECHO) == 1)
		stopTime = micros();

	//printf("start: %d\nstop: %d\n", startTime, stopTime); // test

	// time difference between start and arrival
	timeElapsed = (stopTime - startTime) / 1000000;

	// multiply with the sonic speed (34300 cm/s) and divide by 2
	return (timeElapsed * 34300) / 2;
}

void turnOnLed()
{
	digitalWrite(GPIO_LED, HIGH);
	printf(" (LED ON)\n");
}

void turnOffLed()
{
	digitalWrite(GPIO_LED, LOW);
	printf(" (LED OFF)\n");
}
#endif


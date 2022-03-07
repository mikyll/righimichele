#include "net.h"

void initSDLNet()
{
	int i, numused;

	// init SDL Net
	if (SDLNet_Init() < 0)
	{
		printf("Couldn't initialize SDLNet: %s\n", SDLNet_GetError());
		exit(1);
	}

	if (SDLNet_ResolveHost(&ipAddress, NULL, 4123) == -1) {
		fprintf(stderr, "ER: SDLNet_ResolveHost: %sn", SDLNet_GetError());
		exit(1);
	}

	// Create a socket set
	if (!(socketset = SDLNet_AllocSocketSet(SOCKET_NUM)))
	{
		printf("SDLNet_AllocSocketSet: %s\n", SDLNet_GetError());
		exit(1); //most of the time this is a major error, but do what you want.
	}

	// Open sockets (server): faccio una socket per ciascun lato del radar (8)
	for (i = 0; i < 8; i++)
	{
		udpsocket[i] = SDLNet_UDP_Open(PORT + i);
		if (!udpsocket[i])
		{
			printf("SDLNet_UDP_Open[%d]: %s\n", i, SDLNet_GetError());
			exit(1);
		}
		printf("listening on 0.0.0.0:%hd\n", PORT + i);

		numused = SDLNet_UDP_AddSocket(socketset, udpsocket[i]);
		if (numused == -1) {
			printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
			exit(1);
		}
	}

	// Allocate memory for packets
	for (i = 0; i < SOCKET_NUM; i++)
	{
		if (!(recvPacket[i] = SDLNet_AllocPacket(MAX_PACKET_SIZE)))
		{
			printf("Could not allocate packet\n");
			exit(1);
		}
	}
}

void doReceive()
{
	int numready, i;
	char tmp[MAX_PACKET_SIZE + 1];

	numready = SDLNet_CheckSockets(socketset, 0);
	if (numready == -1) {
		printf("SDLNet_CheckSockets: %s\n", SDLNet_GetError());

		// most of the time this is a system error, where perror might help.
		perror("SDLNet_CheckSockets");
	}
	else if (numready) {
		printf("There are %d sockets with activity!\n", numready);

		// check all sockets with SDLNet_SocketReady and handle the active ones.
		for (i = 0; i < SOCKET_NUM; i++)
		{
			if (SDLNet_SocketReady(udpsocket[i])) {
				if (SDLNet_UDP_Recv(udpsocket[i], recvPacket[i]))
				{
					printf("UDP Packet incoming\n"); // test
					printf("\tChan: %d\n", recvPacket[i]->channel);
					printf("\tData: %s\n", (char*)recvPacket[i]->data);
					printf("\tLen: %d\n", recvPacket[i]->len);
					printf("\tMaxlen: %d\n", recvPacket[i]->maxlen);
					printf("\tStatus: %d\n", recvPacket[i]->status);
					printf("\tAddress: %x %x\n", recvPacket[i]->address.host, recvPacket[i]->address.port);

					double distance;
					sscanf(recvPacket[i]->data, "%f", &distance);
					if (distance > 4.0 && distance < 200.0)
					{
						app.objDetected[i] = 1;
						// prendo la x del radar, l e aggiorno la posizione (x, y) del pallino in base a questo e anche in base all'angolo

						objects[i].detected = 1;
					}
					else {
						app.objDetected[i] = 0;
						objects[i].detected = 0;
					}
				}
			}
			else {
				app.objDetected[i] = 0;
				objects[i].detected = 0;
			}
		}
	}
}
void doReceiveFromSocket(int sock)
{
	int numready;
	char tmp[MAX_PACKET_SIZE + 1];

	numready = SDLNet_CheckSockets(socketset, 0);
	if (numready == -1) {
		printf("SDLNet_CheckSockets: %s\n", SDLNet_GetError());

		// most of the time this is a system error, where perror might help.
		perror("SDLNet_CheckSockets");
	}
	else if (numready) {
		printf("There are %d sockets with activity!\n", numready);

		// check all sockets with SDLNet_SocketReady and handle the active ones.
		if (SDLNet_SocketReady(udpsocket[sock])) {
			if (SDLNet_UDP_Recv(udpsocket[sock], recvPacket[sock]))
			{
				/*printf("UDP Packet incoming\n");
				printf("\tChan: %d\n", recvPacket[sock]->channel);
				printf("\tData: %s\n", (char*)recvPacket[sock]->data);
				printf("\tLen: %d\n", recvPacket[sock]->len);
				printf("\tMaxlen: %d\n", recvPacket[sock]->maxlen);
				printf("\tStatus: %d\n", recvPacket[sock]->status);
				printf("\tAddress: %x %x\n", recvPacket[sock]->address.host, recvPacket[sock]->address.port);*/

				float distance;
				sscanf(recvPacket[sock]->data, "%f", &distance);
				printf("distance: %f\n", distance);
				if (distance > 4.0 && distance < 200.0)
				{
					app.objDetected[sock] = 1;
					// prendo x ed l del radar, e aggiorno la posizione (x, y) del pallino in base a questo e anche in base all'angolo
					float l = (float)radar.l / 200.0;

					objects[sock].x = radar.x + distance * l;
					objects[sock].y = radar.y;
				}
				else {
					app.objDetected[sock] = 0;
				}
			}
		}
		else {
			app.objDetected[sock] = 0;
		}
	}
	else {
		app.objDetected[sock] = 0;
	}
}
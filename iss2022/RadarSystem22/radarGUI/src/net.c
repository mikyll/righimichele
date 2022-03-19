#include "net.h"

UDPsocket ackSocket;
IPaddress ackAddress;

const int ACK_PORT = 4200;

void getIPfromNetwork(IPaddress address, char* ip, int* port);
void getIPfromHost(IPaddress address, char* ip, int* port);

void initSDLNetServer(int port)
{
	int i, numused;

	// 1. Init SDL Net
	if (SDLNet_Init() < 0)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "Couldn't initialize SDLNet:  %s", SDLNet_GetError());
		exit(1);
	}

	// 2. Resolve Host (if NULL the address is broadcast: 0.0.0.0)
	if (SDLNet_ResolveHost(&ipAddress, NULL, 4123) == -1)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "SDLNet_ResolveHost failed: %s", SDLNet_GetError());
		exit(1);
	}

	// 3. Create a socket set
	if (!(socketset = SDLNet_AllocSocketSet(DIR_NUM)))
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "SDLNet_AllocSocketSet failed: %s", SDLNet_GetError());
		exit(1); //most of the time this is a major error, but do what you want.
	}

	// 4. Open sockets and add them to the set (8, one for each radar direction)
	for (i = 0; i < DIR_NUM; i++)
	{
		// 4.1. Open socket #i
		udpSockets[i] = SDLNet_UDP_Open(port + i);
		if (!udpSockets[i])
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_Open[%d] failed: %s", port + i, SDLNet_GetError());
			exit(1);
		}
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Listening on 0.0.0.0:%hd", port + i);

		// 4.2 Add socket #i to the socket set
		numused = SDLNet_UDP_AddSocket(socketset, udpSockets[i]);
		if (numused == -1) {
			printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
			exit(1);
		}
	}

	// 4.3 Open ACK socket on first available port
	if (!(ackSocket = SDLNet_UDP_Open(0)))
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_Open[ACK] failed: %s", SDLNet_GetError());
		exit(1);
	}
	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "ACK socket opened correcly");

	// 5. Allocate memory for packets
	for (i = 0; i < DIR_NUM; i++)
	{
		if (!(udpPackets[i] = SDLNet_AllocPacket(MAX_PACKET_SIZE)))
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "Could not allocate packet: %s", SDLNet_GetError());
			exit(1);
		}
	}
}

// Returns the distance received or -1 if the packet wasn't valid, if the socket wasn't ready or if there aren't socket with values
float receiveDistanceFromSocket(int nSock)
{
	UDPpacket* p;
	char buffer[64];
	int numready;
	float distance = -1;

	// 1. Check if there are sockets with activity
	numready = SDLNet_CheckSockets(socketset, 0);
	if (numready == -1)
	{
		printf("SDLNet_CheckSockets: %s\n", SDLNet_GetError());
		// NB: Most of the time this is a system error, where perror might help.
		perror("SDLNet_CheckSockets");
	}
	else if (numready) // There is at least 1 socket with activity
	{
		// 2. Check if the specific socket has packets ready
		if (SDLNet_SocketReady(udpSockets[nSock]))
		{
			// 3. Receive a packet from the specific socket
			if (SDLNet_UDP_Recv(udpSockets[nSock], udpPackets[nSock]))
			{
				

				sscanf(udpPackets[nSock]->data, "%f", &distance);
				//printf("Detected object %d at distance: %3.1f cm\n", nSock, distance); // test

				char host[16];
				int port;
				getIPfromNetwork(udpPackets[nSock]->address, &host, &port);

				// Test
				printf("UDP Packet incoming\n");
				printf("\tChan: %d\n", udpPackets[nSock]->channel);
				printf("\tData: %s\n", (char*)udpPackets[nSock]->data);
				printf("\tLen: %d\n", udpPackets[nSock]->len);
				printf("\tMaxlen: %d\n", udpPackets[nSock]->maxlen);
				printf("\tStatus: %d\n", udpPackets[nSock]->status);
				printf("\tAddress: %x:%x", udpPackets[nSock]->address.host, udpPackets[nSock]->address.port);
				printf(" (%s:%d)\n", host, port);

				port = DEFAULT_PORT + ACK_PORT_OFFSET + nSock;

				// Resolve ACK IP (TEST)
				if (SDLNet_ResolveHost(&ackAddress, host, port) == -1) {
					fprintf(stderr, "ERROR: SDLNet_ResolveHost: %s\n", SDLNet_GetError());
					exit(1);
				}
				udpPackets[nSock]->address = ackAddress;

				// Send ACK
				if (!SDLNet_UDP_Send(ackSocket, -1, udpPackets[nSock]))
				{
					printf("ERROR: SDLNet_UDP_Send: %s\n\n", SDLNet_GetError());
				}
			}
		}
	}

	return distance;
}

// Big Endian (network order)
void getIPfromNetwork(IPaddress address, char* ip, int* port)
{
	Uint8 addr[4];
	int val, i;
	char res[16];
	char tmp[4];

	res[0] = '\0';
	for (i = 0; i < 4; i++)
	{
		addr[i] = address.host >> i * 8;

		val = (int)addr[i];
		sprintf(tmp, "%d", val);
		strcat(res, tmp);
		i < 3 ? strcat(res, ".") : NULL;
	}

	ip != NULL ? strcpy(ip, res) : NULL;
	port != NULL ? *port = (int)address.port : NULL;
}

// Little Endian (host order)
void getIPfromHost(IPaddress address, char* ip, int* port)
{
	Uint8 addr[4];
	int val, i;
	char res[16];
	char tmp[4];

	res[0] = '\0';
	for (i = 0; i < 4; i++)
	{
		addr[i] = address.host >> 24 - (i * 8);

		val = (int)addr[i];
		sprintf(tmp, "%d", val);
		strcat(res, tmp);
		i < 3 ? strcat(res, ".") : NULL;
	}
	
	ip != NULL ? strcpy(ip, res) : NULL;
	port != NULL ? *port = (int)address.port : NULL;
}
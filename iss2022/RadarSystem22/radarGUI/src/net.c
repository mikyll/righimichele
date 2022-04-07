#include "net.h"

UDPsocket ackSocket;
IPaddress ackAddress;

const int ACK_PORT = 4200;

void initSDLNet(int port);
void initInteraction();
static void resetInteraction();

static void acceptConnection();
static void receiveDistance();
static void receiveDistanceMock();
static void receiveDistanceTCP();
static void sendACK();
static void sendACKMock();

void doReceive();

static void getIPfromNetwork(IPaddress address, char* ip, int* port);
static void getIPfromHost(IPaddress address, char* ip, int* port);


void initSDLNet()
{
	// Init SDL Net
	if (SDLNet_Init() < 0)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "Couldn't initialize SDLNet:  %s", SDLNet_GetError());
		exit(1);
	}
}

void initInteraction(int port)
{
	IPaddress* ipAddress;
	int i, numused;

	// 1. Set function pointers for receive and send
	interaction.receive = receiveDistanceTCP;
	interaction.send = sendACKMock;

	interaction.distanceTail = &interaction.distanceHead;

	// 2. Resolve Host (if IP is NULL then the address is broadcast: 0.0.0.0)
	if (SDLNet_ResolveHost(&interaction.ipAddress, NULL, 4123) == -1)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "SDLNet_ResolveHost failed: %s", SDLNet_GetError());
		exit(1);
	}

	// 3. Create a socket set
	if (!(interaction.socketSet = SDLNet_AllocSocketSet(MAX_SOCKET)))
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "SDLNet_AllocSocketSet failed: %s", SDLNet_GetError());
		exit(1); //most of the time this is a major error, but do what you want.
	}

	// to-do: add 1 udp socket and N (N = MAX_SOCKET - 1) tcp socket, to the socket set
	// 4. Open sockets and add them to the set
	// 4.1 Open UDP socket #1
	interaction.sockets[0] = SDLNet_UDP_Open(4000);
	if (!interaction.sockets[0])
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_Open[%d] failed: %s", 4000, SDLNet_GetError());
		exit(1);
	}
	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Listening on 0.0.0.0:%hd", 4000);

	// 4.2 Add UDP socket #1 to the socket set
	numused = SDLNet_UDP_AddSocket(interaction.socketSet, interaction.sockets[0]);
	if (numused == -1) {
		printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
		exit(1);
	}
	
	// test TCP
	interaction.serverSocket = SDLNet_TCP_Open(&interaction.ipAddress);
	if (!interaction.serverSocket) {
		printf("SDLNet_TCP_Open: %s\n", SDLNet_GetError());
		exit(1);
	}

	numused = SDLNet_TCP_AddSocket(interaction.socketSet, interaction.serverSocket);
	if (numused == -1) {
		printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
		exit(1);
	}

	for (i = 0; i < MAX_SOCKET; i++)
	{
		if (interaction.sockets[i] == NULL)
			printf("%d) NULL\n", i);
	}

	// 4.3 Open & Add TCP sockets to the socket set
	/*for (i = 1; i < MAX_SOCKET; i++)
	{
		interaction.sockets[i] = SDLNet_TCP_Open(&interaction.ipAddress);
		if (!interaction.sockets[i]) {
			printf("SDLNet_TCP_Open: %s\n", SDLNet_GetError());
			exit(2);
		}

		numused = SDLNet_TCP_AddSocket(interaction.socketset, interaction.sockets[i]);
		if (numused == -1) {
			printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
			exit(1);
		}
	}*/

	// 4.3 Open ACK socket on first available port
	/*if (!(ackSocket = SDLNet_UDP_Open(0)))
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_Open[ACK] failed: %s", SDLNet_GetError());
		exit(1);
	}
	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "ACK socket opened correcly");*/
}

static void resetInteraction()
{
	Distance* d;

	while (interaction.distanceHead.next)
	{
		d = interaction.distanceHead.next;
		interaction.distanceHead.next = d->next;
		free(d);
	}

	// reset socketset
}



static void receiveDistance()
{
	Distance* d;
	UDPpacket* p;
	char buffer[64];
	int numready, i;

	// 1. Check if there are sockets with activity (also includes disconnections and other errors, which can be determined by a failed write/read attempt)
	numready = SDLNet_CheckSockets(interaction.socketSet, 0);
	if (numready == -1)
	{
		printf("SDLNet_CheckSockets: %s\n", SDLNet_GetError());
		// NB: Most of the time this is a system error, where perror might help.
		perror("SDLNet_CheckSockets");
	}
	else if (numready)
	{
		// 2. There is at least 1 socket with activity => Allocate a packet
		if (!(p = SDLNet_AllocPacket(MAX_PACKET_SIZE)))
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "Could not allocate packet: %s", SDLNet_GetError());
			exit(1);
		}
		
		for (i = 0; i < MAX_SOCKET; i++)
		{
			// 3. Check if the specific socket has packets ready
			if (SDLNet_SocketReady(interaction.sockets[i]))
			{
				switch (interaction.socketType[i])
				{
				case SOCK_UDP:
					// receiveUDP()
					break;
				case SOCK_TCP:
					// receiveTCP()
					break;
				default:
					break;
				}

				// 4. Receive a packet from the socket #i
				if (SDLNet_UDP_Recv(interaction.sockets[i], p))
				{
					int distance, angle;
					sscanf(p->data, "{\"distance\": %d, \"angle\" : %d}", &distance, &angle);
					//printf("Detected object %d at distance: %3.1f cm\n", nSock, distance); // test
					printf("DISTANCE RECEIVED: %d, %d\n", distance, angle);

					char host[16];
					int port;
					getIPfromNetwork(p->address, &host, &port);

					// Test
					printf("UDP Packet incoming\n");
					printf("\tChan: %d\n", p->channel);
					printf("\tData: %s\n", (char*)p->data);
					printf("\tLen: %d\n", p->len);
					printf("\tMaxlen: %d\n", p->maxlen);
					printf("\tStatus: %d\n", p->status);
					printf("\tAddress: %x:%x", p->address.host, p->address.port);
					printf(" (%s:%d)\n", host, port);

					// 5. Add Distance to linked list
					d = malloc(sizeof(Distance));
					memset(d, 0, sizeof(Distance));

					d->distance = 90; // test
					d->angle = 3; // test

					interaction.distanceTail->next = d;
					interaction.distanceTail = d;

					

					// 6. Send back ACK
					/*port = DEFAULT_PORT + ACK_PORT_OFFSET + i;

					// Resolve ACK IP (TEST)
					if (SDLNet_ResolveHost(&ackAddress, host, port) == -1) {
						fprintf(stderr, "ERROR: SDLNet_ResolveHost: %s\n", SDLNet_GetError());
						exit(1);
					}
					p->address = ackAddress;

					// Send ACK
					if (!SDLNet_UDP_Send(ackSocket, -1, p))
					{
						printf("ERROR: SDLNet_UDP_Send: %s\n\n", SDLNet_GetError());
					}*/
				}
			}
		}
	}
}

static void sendACK()
{
	// to-do
}

static void receiveDistanceMock()
{
	Distance* d;

	int r = rand() % 1000;
	if (r < 200)
	{
		printf("Mock UDP Packet incoming\n");
		printf("\tData: {\"distance\" : %d, \"angle\" : %d}\n", r, 180);

		d = malloc(sizeof(Distance));
		memset(d, 0, sizeof(Distance));

		d->distance = r;
		d->angle = 180;

		interaction.distanceTail->next = d;
		interaction.distanceTail = d;
	}
}

// Checks if the server socket is ready: that means there is a connection ready to be accepted
static void acceptConnection()
{
	// if the server socket is ready, we accept the connection, and add it to the socket set
	if (SDLNet_SocketReady(interaction.serverSocket))
	{
		interaction.sockets[1] = SDLNet_TCP_Accept(interaction.serverSocket);
		if (!interaction.sockets[1]) {
			printf("SDLNet_TCP_Accept: %s\n", SDLNet_GetError());
		}
		else {
			printf("Accepted connection 1\n");

			if (!SDLNet_TCP_AddSocket(interaction.socketSet, interaction.sockets[1])) {
				printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
				exit(1);
			}
		}
	}
}

static void receiveUDP()
{

}

static void receiveTCP()
{

}

static void receiveDistanceTCP()
{
	Distance* d;
	int distance = 1, angle = 1;
	int result, numready;
	char msg[64];

	numready = SDLNet_CheckSockets(interaction.socketSet, 0);
	if (numready == -1) {
		printf("SDLNet_CheckSockets: %s\n", SDLNet_GetError());
		//most of the time this is a system error, where perror might help you.
		perror("SDLNet_CheckSockets");
	}
	else if (numready) {
		printf("There are %d sockets with activity!\n", numready);
		// check all sockets with SDLNet_SocketReady and handle the active ones.

		if (SDLNet_SocketReady(interaction.serverSocket))
		{
			interaction.sockets[1] = SDLNet_TCP_Accept(interaction.serverSocket);
			if (!interaction.sockets[1]) {
				printf("SDLNet_TCP_Accept: %s\n", SDLNet_GetError());
			}
			else {
				printf("Accepted connection 1\n");

				if (!SDLNet_TCP_AddSocket(interaction.socketSet, interaction.sockets[1])) {
					printf("SDLNet_AddSocket: %s\n", SDLNet_GetError());
					exit(1);
				}
			}
		}
		
		if (SDLNet_SocketReady(interaction.sockets[1]))
		{
			if (SDLNet_TCP_Recv(interaction.sockets[1], msg, 64) > 0)
			{
				printf("Received: \"%s\"\n", msg);

				sscanf(msg, "{\"distance\": %d, \"angle\" : %d}", &distance, &angle);

				d = malloc(sizeof(Distance));
				memset(d, 0, sizeof(Distance));

				d->distance = distance;
				d->angle = angle;

				printf("%d, %d\n", distance, angle);

				interaction.distanceTail->next = d;
				interaction.distanceTail = d;
			}
			// If recv fails it means that an error may have occurred. Usually we can ignore it, but we should disconnect the socket anyway since it's likely invalid now
			else {
				SDLNet_TCP_Close(interaction.sockets[1]);
				SDLNet_DelSocket(interaction.socketSet, interaction.sockets[1]);

				printf("Connection 1 closed\n");
				
				// An error may have occured, but sometimes you can just ignore it
				// It may be good to disconnect sock because it is likely invalid now.
			}
		}
	}
	
}

static void sendACKMock()
{
	//printf("Mock send ACK\n");
}

void doReceive()
{
	// accept TCP connections

	interaction.receive();

	interaction.send();
}

void doReceiveExample()
{
	Distance* d;

	// receive
	//interaction.receive();

	// create Obstacle
	int r = rand() % 200;
	if (r == 50)
	{
		d = malloc(sizeof(Distance));
		memset(d, 0, sizeof(Distance));

		d->distance = r;
		d->angle = 180;

		interaction.distanceTail->next = d;
		interaction.distanceTail = d;
	}

	// send ack
}

// initSDLnetTCP()
// initSDLnetUDP()

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
#include "net.h"

void initSDLnet();
void initInteraction();
static void addUDPsocket(Uint16 port);
static void addTCPserverSocket(Uint16 port);
static void acceptTCPconnection(int serverSocket);

static void receiveMessage();
static void receiveUDP(int nSock);
static void receiveTCP(int nSock);
static void receiveMock();

void doReceive();

static void addMessage(char* data);

static void getIPfromNetwork(IPaddress address, char* ip, int* port);
static void getIPfromHost(IPaddress address, char* ip, int* port);


void initSDLnet()
{
	// Init SDL Net subsystem
	if (SDLNet_Init() < 0)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "Couldn't initialize SDLNet:  %s", SDLNet_GetError());
		exit(1);
	}
}

void initInteraction()
{
	memset(&interaction, 0, sizeof(Interaction));
	interaction.used = 0;

	// 1.1 Init: set function pointers for receive and send
	interaction.receive = receiveMessage;

	// 1.2 Init: setup LinkedList
	interaction.messageTail = &interaction.messageHead;

	// 2. Create a socket set
	if ((interaction.socketSet = SDLNet_AllocSocketSet(MAX_SOCKET)) == NULL)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_AllocSocketSet failed: %s", SDLNet_GetError());
		exit(1); // most of the time this is a major error
	}

	// 3. Create and add sockets to the set
	addUDPsocket(app.args.UDPport);
	addTCPserverSocket(app.args.TCPport);

	// 4 Open ACK socket on first available port
	/*if (!(ackSocket = SDLNet_UDP_Open(0)))
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_Open[ACK] failed: %s", SDLNet_GetError());
		exit(1);
	}
	SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "ACK socket opened correcly");*/
}

static void receiveMessage()
{
	Message* m;
	int numReady, i;

	// 1. Check if there are sockets with activity (this includes disconnections and other errors, which can be determined by a failed write/read attempt)
	numReady = SDLNet_CheckSockets(interaction.socketSet, 0);
	if (numReady == -1) // Error with select system call
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_CheckSockets: %s", SDLNet_GetError());

		// NB: Most of the time this is a system error, where perror might help.
		perror("SDLNet_CheckSockets");
	}
	else if (numReady)
	{
		// 2. There is at least 1 socket with activity
		for (i = 0; i < interaction.used; i++)
		{
			// 3. Check if the specific socket has packets ready
			if (SDLNet_SocketReady(interaction.sockets[i]))
			{
				switch (interaction.socketType[i])
				{
				case SOCK_TCP_SERVER:
					acceptTCPconnection(i);
					break;

				case SOCK_UDP:
					receiveUDP(i);
					break;
				
				case SOCK_TCP:
					receiveTCP(i);
					break;
				
				default:
					break;
				}
			}
		}
	}
}

// Open a UDP socket and add it to the socket set
static void addUDPsocket(Uint16 port)
{
	// 1. Check for available indexes in socket array
	if (interaction.used == MAX_SOCKET)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "The socket set is full (%d/%d)", interaction.used, MAX_SOCKET);
		return;
	}
	else // There is at least an available index
	{
		// 2. Open UDP socket #i
		if ((interaction.sockets[interaction.used] = SDLNet_UDP_Open(port)) == NULL)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_Open[%d] on port %d failed: %s", interaction.used, port, SDLNet_GetError());
			exit(1);
		}
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "UDP socket created. Listening on 0.0.0.0:%hd...", port);

		// 3. Save the type of the socket
		interaction.socketType[interaction.used] = SOCK_UDP;

		// 4. Add UDP socket #i to the socket set
		interaction.used = SDLNet_UDP_AddSocket(interaction.socketSet, interaction.sockets[interaction.used]);
		if (interaction.used == -1)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_AddSocket failed: %s", SDLNet_GetError());
			exit(1);
		}
	}
}

// Open a TCP server socket and add it to the socket set
static void addTCPserverSocket(Uint16 port)
{
	// 1. Check for available indexes in socket array
	if (interaction.used == MAX_SOCKET)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "The socket set is full (%d/%d)", interaction.used, MAX_SOCKET);
		return;
	}
	else // There is at least an available index
	{
		// 2. Resolve Host (if IP is NULL || INADDR_ANY, the socket listens on all network interfaces: 0.0.0.0)
		if (SDLNet_ResolveHost(&interaction.ipAddress, NULL, port) == -1)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "SDLNet_ResolveHost failed: %s", SDLNet_GetError());
			exit(1);
		}

		// 3. Open TCP server socket #i
		if ((interaction.sockets[interaction.used] = SDLNet_TCP_Open(&interaction.ipAddress)) == NULL)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_CRITICAL, "SDLNet_TCP_Open[%d] on port %hd failed: %s", interaction.used, port, SDLNet_GetError());
			exit(1);
		}
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "TCP server socket created. Listening for connections on 0.0.0.0:%hd...", port);

		// 4. Save the type of the socket
		interaction.socketType[interaction.used] = SOCK_TCP_SERVER;

		// 5. Add TCP server socket #i to the socket set
		interaction.used = SDLNet_TCP_AddSocket(interaction.socketSet, interaction.sockets[interaction.used]);
		if (interaction.used == -1)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_TCP_AddSocket failed: %s", SDLNet_GetError());
			exit(1);
		}
	}
}

// Accept a TCP connection from the server socket
static void acceptTCPconnection(int serverSocket)
{
	// 1. Check for available indexes in socket array
	if (interaction.used == MAX_SOCKET)
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "The socket set is full (%d/%d)", interaction.used, MAX_SOCKET);
		// handle connection request
		return;
	}
	else // There is at least an available index
	{
		// 2. Accept the connection and add its socket to the array
		interaction.sockets[interaction.used] = SDLNet_TCP_Accept(interaction.sockets[serverSocket]);
		if (interaction.sockets[interaction.used] == NULL)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_WARN, "SDLNet_TCP_Accept: %s", SDL_GetError());
			return;
		}
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "TCP socket #%d connected. Listening...", interaction.used);

		// 3. Save the type of the socket
		interaction.socketType[interaction.used] = SOCK_TCP;

		// 4. Add the socket to the socket set
		interaction.used = SDLNet_TCP_AddSocket(interaction.socketSet, interaction.sockets[interaction.used]);
		if (interaction.used == -1)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_TCP_AddSocket: %s", SDLNet_GetError());
			exit(1);
		}
	}
}

// Receive a UDP packet
static void receiveUDP(int nSock)
{
	UDPpacket* p;

	// 1. Allocate a UDP Packet
	if (!(p = SDLNet_AllocPacket(MAX_PACKET_SIZE)))
	{
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "Could not allocate packet: %s", SDLNet_GetError());
		exit(1);
	}

	// 2. Receive a UDP packet from the socket #nSock
	if (SDLNet_UDP_Recv(interaction.sockets[nSock], p))
	{
		char host[16];
		int port;
		getIPfromNetwork(p->address, &host, &port);

		// Debug
		if (app.args.debug)
		{
			printf("UDP Packet incoming.\n\tChan: % d\n\tData: % s\n\tLen: % d\n\tMaxlen: % d\n\tStatus: % d\n\tAddress: % x : % x\n(% s: % d)\n", p->channel, (char*)p->data, p->len, p->maxlen, p->status, p->address.host, p->address.port, host, port);
		}

		// 3. Add Message to the linked list
		addMessage(p->data);
		
		// 3. Send back ACK
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

// Receive a TCP message
static void receiveTCP(int nSock)
{
	char msg[MAX_MSG_LENGTH];
	int read;

	// 1. Receive a TCP message from the connection #nSock
	read = SDLNet_TCP_Recv(interaction.sockets[nSock], msg, MAX_MSG_LENGTH);
	if (read > 0)
	{
		// Debug
		if (app.args.debug)
		{
			printf("TCP message incoming. Received %d bytes: \"%s\"\n", read, msg);
		}

		// 2. Add Message to the linked list
		addMessage(msg);
	}
	// If recv fails it means that an error may have occurred. Usually we can ignore it, but we should disconnect the socket anyway since it's likely invalid now
	else
	{
		interaction.used = SDLNet_DelSocket(interaction.socketSet, interaction.sockets[nSock]);
		if (interaction.used == -1)
		{
			SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_ERROR, "SDLNet_UDP_DelSocket failed: %s", SDLNet_GetError());
			exit(1);
		}

		SDLNet_TCP_Close(interaction.sockets[nSock]);
		
		SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Socket #%d closed", nSock);
	}
}

static void receiveMock()
{
	char msg[MAX_MSG_LENGTH];
	int r;

	r = rand() % 1000;
	if (r < 200)
	{
		sprintf(msg, "{\"distance\" : %d, \"angle\" : %d}", r, 180);

		// Debug
		if (app.args.debug)
		{
			printf("Mock TCP message incoming. Data: \"%s\"\n", msg);
		}

		addMessage(msg);
	}
}

void doReceive()
{
	interaction.receive();
}

static void addMessage(char* data)
{
	Message* m;

	m = malloc(sizeof(Message));
	if (m)
	{
		memset(m, 0, sizeof(Message));

		STRNCPY(m->data, data, MAX_MSG_LENGTH);

		interaction.messageTail->next = m;
		interaction.messageTail = m;
	}
}

// Big Endian (network order)
static void getIPfromNetwork(IPaddress address, char* ip, int* port)
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
static void getIPfromHost(IPaddress address, char* ip, int* port)
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
#include "stage.h"

static const DPC = SDL_ALPHA_OPAQUE / (FPS * SCP); // decrement to last for a complete cycle

static void logic();
static void draw();
// logic
void initStage();
static void resetStage();
static void initRadar();
static void doMessages();
static void doRadar();
static void doObstacles();
static void doObstacleCoordinates();
static Distance* parseDistance(Message m);
static void spawnObstacle(int distance, int angle);
static void spawnSus(int x, int y);
static void spawnObstacleCoordinatesText(int distance, int angle);
// draw
static void drawRadar();
static void drawRadarCoordinates();
static void drawRadarLine();
static void drawObstacles();
static void drawFPStext();
static void drawObstacleCoordinatesText();

void getCartesianFromPolarCoords(int radius, int angle, int* x, int* y);
void getPolarFromCartesianCoords(int x, int y, int* radius, int* angle);

static Entity* radar;
static Entity* radarCoordinates;
Entity* radarLine;
static SDL_Texture* radarTexture;
static SDL_Texture* radarCoordinateTexture;
static SDL_Texture* radarLineTexture;
static SDL_Texture* obstacleTexture;
static SDL_Texture* susTexture;

static SDL_Texture* textFPS;

static int prevMouseX, prevMouseY;

static void logic()
{
	doMessages();

	doRadar();

	doObstacles();

	doObstacleCoordinates();
}

static void initRadar()
{
	// Init radar
	radar = malloc(sizeof(Entity));
	memset(radar, 0, sizeof(Entity));
	radar->texture = radarTexture;
	SDL_QueryTexture(radar->texture, NULL, NULL, &radar->w, &radar->h);
	radar->x = WINDOW_WIDTH / 2;
	radar->y = WINDOW_HEIGHT - BOTTOM_OFFSET - radar->h / 2;

	// Init radarCoordinates
	radarCoordinates = malloc(sizeof(Entity));
	memset(radarCoordinates, 0, sizeof(Entity));
	radarCoordinates->texture = radarCoordinateTexture;
	SDL_QueryTexture(radarCoordinates->texture, NULL, NULL, &radarCoordinates->w, &radarCoordinates->h);
	radarCoordinates->x = WINDOW_WIDTH / 2;
	radarCoordinates->y = WINDOW_HEIGHT - BOTTOM_OFFSET - radar->h / 2;

	// Init radarLine
	radarLine = malloc(sizeof(Entity));
	memset(radarLine, 0, sizeof(Entity));
	radarLine->texture = radarLineTexture;
	SDL_QueryTexture(radarLine->texture, NULL, NULL, &radarLine->w, &radarLine->h);
	radarLine->x = WINDOW_WIDTH / 2;
	radarLine->y = WINDOW_HEIGHT - BOTTOM_OFFSET - radarLine->h / 2;
	radarLine->angle = 0;

	// Init ObsCoordinates Text queue
	stage.obsCoordQ = (ObstacleCoordinateQueue*)malloc(sizeof(ObstacleCoordinateQueue));
	stage.obsCoordQ->capacity = app.args.maxCoordinatesText;
	stage.obsCoordQ->length = 0;
	stage.obsCoordQ->first = NULL;
	stage.obsCoordQ->last = NULL;
}

void initStage()
{
	// Set the delegates
	app.delegate.logic = logic;
	app.delegate.draw = draw;

	memset(&stage, 0, sizeof(Stage));

	stage.obstacleTail = &stage.obstacleHead;
	stage.distanceTail = &stage.distanceHead;

	// Load textures
	radarTexture = loadTexture("gfx/radar.png");
	radarCoordinateTexture = loadTexture("gfx/radar_coordinates.png");
	radarLineTexture = loadTexture("gfx/radar_line.png");
	obstacleTexture = loadTexture("gfx/obstacle.png");
	SDL_SetTextureBlendMode(obstacleTexture, SDL_BLENDMODE_BLEND);
	susTexture = loadTexture("gfx/sus.png");
	SDL_SetTextureBlendMode(susTexture, SDL_BLENDMODE_BLEND);

	resetStage();
}

static void resetStage()
{
	Entity* e;
	Distance* d;
	ObstacleCoordinate* oc;

	while (stage.obstacleHead.next)
	{
		e = stage.obstacleHead.next;
		stage.obstacleHead.next = e->next;
		free(e);
	}
	while (stage.distanceHead.next)
	{
		d = stage.distanceHead.next;
		stage.distanceHead.next = d->next;
		free(d);
	}

	initRadar();

	stage.fps = 0;
}

// Scroll trough the Message list and parse a Distance from each element, then free it
static void doMessages()
{
	Distance* d;
	Message* m, * prev;

	prev = &interaction.messageHead;
	for (m = interaction.messageHead.next; m != NULL; m = m->next)
	{
		d = parseDistance(*m);

		stage.distanceTail->next = d;
		stage.distanceTail = d;

		if (m == interaction.messageTail)
			interaction.messageTail = prev;

		prev->next = m->next;
		free(m);
		m = prev;
	}
}

// Rotate the radar line and eventually add obstacles (if the distance is in range, and parse angles)
static void doRadar()
{
	Distance* d, * prev;
	int x, y, radius, angle;

	// Rotate the radar line
	if (radarLine->angle >= 360)
		radarLine->angle = 0;
	radarLine->angle += ANGLE_INCREMENT;

	prev = &stage.distanceHead;

	// Scroll the linked list
	for (d = stage.distanceHead.next; d != NULL; d = d->next)
	{
		// Check if the angle of the detected obstacle equals the current radarLine angle
		if (radarLine->angle <= d->angle && radarLine->angle >= d->angle - ANGLE_INCREMENT)
		{
			// Show (Spawn) the obstacle only if it's between the bounds
			if (d->distance >= MIN_D && d->distance <= MAX_D)
			{
				getCartesianFromPolarCoords(((radar->w / 2) / (double)MAX_D) * d->distance, d->angle - 90, &x, &y);

				spawnObstacle(x + radar->x, y + radar->y);
				spawnObstacleCoordinatesText(d->distance, d->angle);

				playSound(SND_OBJ_DETECTED, CH_OBJ);
			}

			if (d == stage.distanceTail)
				stage.distanceTail = prev;

			prev->next = d->next;
			free(d);
			d = prev;
		}

		prev = d;
	}

	if (app.susDetected && radarLine->angle == 90)
	{
		spawnSus(radar->x + radar->w / 4, radar->y);
		spawnObstacleCoordinatesText(100, 90);

		playSound(SND_SUS_DETECTED, CH_SUS);
	}

	if (app.mouse.button[SDL_BUTTON_LEFT] && app.mouse.prevX != app.mouse.x && app.mouse.prevY != app.mouse.y)
	{
		app.mouse.prevX = app.mouse.x;
		app.mouse.prevY = app.mouse.y;
		getPolarFromCartesianCoords(app.mouse.x - radar->x, -(app.mouse.y - radar->y), &radius, &angle);

		d = malloc(sizeof(Distance));
		memset(d, 0, sizeof(Distance));
		d->distance = radius / ((radar->w / 2) / (double)MAX_D);
		d->angle = app.mouse.x - radar->x >= 0 ? 90 - angle : 270 - angle;

		stage.distanceTail->next = d;
		stage.distanceTail = d;

		// Debug
		if (app.args.debug)
		{
			printf("Mouse click - distance: %d, angle: %d\n", d->distance, d->angle);
		}
	}
}

static void doObstacles()
{
	Entity* o, * prev;

	prev = &stage.obstacleHead;

	for (o = stage.obstacleHead.next; o != NULL; o = o->next)
	{
		o->alpha -= DPC * 1.5;
		if (o->alpha <= 0)
		{
			if (o == stage.obstacleTail)
				stage.obstacleTail = prev;

			prev->next = o->next;
			free(o); // Remove the obstacle from the linked list
			o = prev;
		}

		prev = o;
	}
}

static void doObstacleCoordinates()
{
	ObstacleCoordinate* oc;

	oc = stage.obsCoordQ->first;
	while (oc != NULL)
	{
		oc->health -= DPC * 1.5;
		if (oc->health <= 0)
		{
			// extract the first element of the queue
			oc = stage.obsCoordQ->first;
			stage.obsCoordQ->first = stage.obsCoordQ->first->next;
			stage.obsCoordQ->length--;

			if (stage.obsCoordQ->length == 0)
			{
				stage.obsCoordQ->first = NULL;
				stage.obsCoordQ->last = NULL;
			}
			
			free(oc);
			break;
		}

		oc = oc->next;
	}
}

Distance* parseDistance(Message m)
{
	Distance* d;

	d = malloc(sizeof(Distance));
	memset(d, 0, sizeof(Distance));

	sscanf(m.data, "{\"distance\": %d, \"angle\" : %d}", &(d->distance), &(d->angle));

	return d;
}

// Create the Obstacle
static void spawnObstacle(int x, int y)
{
	Entity* o;

	o = malloc(sizeof(Entity));
	memset(o, 0, sizeof(Entity));

	stage.obstacleTail->next = o;
	stage.obstacleTail = o;

	o->x = x;
	o->y = y;
	o->texture = obstacleTexture;

	o->alpha = SDL_ALPHA_OPAQUE;
}
/*static void spawnObstacle(int distance, int angle)
{
	Entity* o;
	int x, y;

	getCartesianFromPolarCoords(distance, angle - 90, &x, &y);

	o = malloc(sizeof(Entity));
	memset(o, 0, sizeof(Entity));

	stage.obstacleTail->next = o;
	stage.obstacleTail = o;

	o->x = x + radar->x;
	o->y = y + radar->y;
	o->texture = obstacleTexture;

	o->alpha = SDL_ALPHA_OPAQUE;
}*/

// Create the Sus
static void spawnSus(int x, int y)
{
	Entity* s;

	s = malloc(sizeof(Entity));
	memset(s, 0, sizeof(Entity));

	stage.obstacleTail->next = s;
	stage.obstacleTail = s;

	s->x = x;
	s->y = y;

	s->texture = susTexture;

	s->alpha = SDL_ALPHA_OPAQUE;
}

// Create the Obstacle Coordinates Text
static void spawnObstacleCoordinatesText(int distance, int angle)
{
	ObstacleCoordinate* oc, * newNode;
	char buffer[48];

	// Extract the first element if the queue is full
	if (stage.obsCoordQ->length == stage.obsCoordQ->capacity)
	{
		oc = stage.obsCoordQ->first;
		stage.obsCoordQ->first = stage.obsCoordQ->first->next;
		stage.obsCoordQ->length--;

		if (stage.obsCoordQ->length == 0)
			stage.obsCoordQ->last = NULL;

		free(oc);
	}

	// Append the new element at the end of the queue
	newNode = (ObstacleCoordinate*)malloc(sizeof(ObstacleCoordinate));
	if (newNode)
	{
		memset(newNode, 0, sizeof(ObstacleCoordinate));
		newNode->distance = distance;
		newNode->angle = angle;
		newNode->health = SDL_ALPHA_OPAQUE;

		snprintf(buffer, 48, "Obstacle { Distance: %d, Angle: %d }", distance, angle);
		newNode->text = getTextTexture(buffer);

		newNode->next = NULL;
		if (stage.obsCoordQ->length == 0)
			stage.obsCoordQ->first = newNode;
		else
			stage.obsCoordQ->last->next = newNode;

		stage.obsCoordQ->last = newNode;
		stage.obsCoordQ->length++;
	}
}

static void draw()
{
	drawRadar();

	drawRadarCoordinates();

	drawRadarLine();

	drawObstacles();

	drawObstacleCoordinatesText();

	if(app.args.fps)
		drawFPStext();
}

static void drawRadar()
{
	blit(radar->texture, radar->x, radar->y, 1);
}

static void drawRadarCoordinates()
{
	blit(radarCoordinates->texture, radarCoordinates->x, radarCoordinates->y, 1);
}

static void drawRadarLine()
{
	blitRotated(radarLine->texture, radarLine->x, radarLine->y, 1, radarLine->angle);
}

static void drawObstacles()
{
	Entity* o;

	for (o = stage.obstacleHead.next; o != NULL; o = o->next)
	{
		SDL_SetTextureAlphaMod(o->texture, o->alpha);

		blit(o->texture, o->x, o->y, 1);
	}
}

static void drawObstacleCoordinatesText()
{
	ObstacleCoordinate* oc;
	char buffer[32];
	int i;

	for (i = 0, oc = stage.obsCoordQ->first; oc != NULL; oc = oc->next, i += 20)
	{
		drawNormalText(oc->text, 20, i);
	}
}

static void drawFPStext()
{
	char buffer[32];

	// Draw FPS text
	snprintf(buffer, 32, "FPS: %d", stage.fps);
	textFPS = getTextTexture(buffer);
	drawNormalText(textFPS, WINDOW_WIDTH - 90, 0);
}

void getCartesianFromPolarCoords(int radius, int angle, int* x, int* y)
{
	double a;

	a = angle * (double)(M_PI / 180.0);
	*x = (cos(a) * radius);
	*y = (sin(a) * radius);
}

void getPolarFromCartesianCoords(int x, int y, int* radius, int* angle)
{
	double theta;

	*radius = sqrt(x * x + y * y);

	theta = atan((double) y / (double) (x != 0 ? x : 1)); // division by 0 is impossible
	theta *= (180.0 / M_PI);
	*angle = theta;
}
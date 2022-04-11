#include "stage.h"

static const DPC = SDL_ALPHA_OPAQUE / (FPS * CPS); // decrement to last for a complete cycle

static void logic();
static void draw();
// logic
void initStage();
static void resetStage();
static void initRadar();
static void doMessages();
static void doRadar();
static void doObstacles();
static Distance* parseDistance(Message m);
static void spawnObstacle(int x, int y);
static void spawnSus(int x, int y);
// draw
static void drawRadar();
static void drawRadarCoordinates();
static void drawRadarLine();
static void drawObstacles();
static void drawText();

void getCoordsFromAngle(int angle, int radius, int* x, int* y);


static Entity* radar;
Entity* radarLine;
static SDL_Texture* radarTexture;
static SDL_Texture* radarCoordinateTexture;
static SDL_Texture* radarLineTexture;
static SDL_Texture* obstacleTexture;
static SDL_Texture* susTexture;

static SDL_Texture* textFPS;


static void logic()
{
	doMessages();

	doRadar();

	doObstacles();
}

static void initRadar()
{
	// Init radar
	radar = malloc(sizeof(Entity));
	memset(radar, 0, sizeof(Entity));
	radar->texture = radarTexture;
	SDL_QueryTexture(radar->texture, NULL, NULL, &radar->w, &radar->h);
	radar->x = WINDOW_WIDTH / 2;
	radar->y = WINDOW_HEIGHT - radar->h / 2;

	// Init radarLine
	radarLine = malloc(sizeof(Entity));
	memset(radarLine, 0, sizeof(Entity));
	radarLine->texture = radarLineTexture;
	SDL_QueryTexture(radarLine->texture, NULL, NULL, &radarLine->w, &radarLine->h);
	radarLine->x = WINDOW_WIDTH / 2;
	radarLine->y = WINDOW_HEIGHT - radarLine->h / 2;
	radarLine->angle = -90;
}

void initStage()
{
	// Set the delegates
	app.delegate.logic = logic;
	app.delegate.draw = draw;

	memset(&stage, 0, sizeof(Stage));
	
	stage.obstacleTail = &stage.obstacleHead;
	stage.distanceTail = &stage.distanceHead;
	stage.obstacleCoordsTail = &stage.obstacleCoordsHead;

	// Load textures
	radarTexture = loadTexture("gfx/radar.png");
	radarCoordinateTexture = loadTexture("gfx/coordinates.png");
	radarLineTexture = loadTexture("gfx/radar_line.png");
	obstacleTexture = loadTexture("gfx/obstacle2.png");
	SDL_SetTextureBlendMode(obstacleTexture, SDL_BLENDMODE_BLEND);
	susTexture = loadTexture("gfx/sus.png");
	SDL_SetTextureBlendMode(susTexture, SDL_BLENDMODE_BLEND);

	resetStage();
}

static void resetStage()
{
	Entity* e;
	Distance* d;
	ObstacleCoord* oc;
	
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
	while (stage.obstacleCoordsHead.next)
	{
		oc = stage.obstacleCoordsHead.next;
		stage.obstacleCoordsHead.next = oc->next;
		free(oc);
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
	int x, y;

	// Rotate the radar line
	if (radarLine->angle >= 360)
		radarLine->angle = 0;
	radarLine->angle += ANGLE_INCREMENT;

	// scorre la lista di rilevazioni e se angle == radarLine->angle => chiama spawnObstacle()
	// calcola x & y, dunque spawnObstacle(x, y, ENTITY_LIFE) & spawnText(x, y, ENTITY_LIFE);

	prev = &stage.distanceHead;

	// Scroll the linked list
	for (d = stage.distanceHead.next; d != NULL; d = d->next)
	{
		// Check if the angle of the detected obstacle equals the current radarLine angle
		if (radarLine->angle == d->angle)
		{
			// show the obstacle only if it's between the bounds
			if (d->distance >= MIN_D && d->distance <= MAX_D)
			{
				int x2, y2, l;
				double a;
				l = radar->w / MAX_D;
				a = radarLine->angle * (double)(PI / 180.0);
				x2 = radar->x + (cos(a) * d->distance * l);
				y2 = radar->y + (sin(a) * d->distance * l);
				printf("l: %d, a: %f, x: %d, y: %d\n", l, a, x2, y2);

				getCoordsFromAngle(radarLine->angle, radar->w / MAX_D, &x, &y);

				spawnObstacle(radar->x + x * d->distance, radar->y + y * d->distance);
				printf("%d, %d\n", radar->x + x * d->distance, radar->y + y * d->distance); // Debug / Test
				// spawnText(?)
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
	
	
	if (app.susDetected && radarLine->angle == 360)
	{
		spawnSus(radar->x, radar->y);
		playSound(SND_SUS_DETECTED, CH_SUS);
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
			free(o); // remove the obstacle from the linked list
			o = prev;
		}

		prev = o;
	}
}

static void doObstacleCoordinates()
{
	// MAX_OBS_COORDS_NUM = 5

	// when the num is > MAX_OBS, start deleting the first element and scroll all the values up

	// if health <= 0 => free
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

// Create the Sus
static void spawnSus(int x, int y)
{
	Entity* s;

	s = malloc(sizeof(Entity));
	memset(s, 0, sizeof(Entity));

	stage.obstacleTail->next = s;
	stage.obstacleTail = s;

	s->x = x + radar->w / 4;
	s->y = y;

	s->texture = susTexture;

	s->alpha = SDL_ALPHA_OPAQUE;
}

static void spawnObsCoord(int x, int y)
{
	ObstacleCoord* oc;

	oc = malloc(sizeof(ObstacleCoord));
	memset(oc, 0, sizeof(ObstacleCoord));

	stage.obstacleCoordsTail->next = oc;
	stage.obstacleCoordsTail = oc;

	// to-do
	oc->health = SDL_ALPHA_OPAQUE;
	/*oc->x = x;
	oc->y = y;
	oc->texture = obstacleTexture;

	oc->alpha = SDL_ALPHA_OPAQUE;*/
}

static void draw()
{
	drawRadar();

	drawRadarCoordinates();

	drawRadarLine();

	drawObstacles();

	drawText();
}

static void drawRadar()
{
	blit(radarTexture, radar->x, radar->y, 1);
}

static void drawRadarCoordinates()
{
	blit(radarCoordinateTexture, radar->x, radar->y, 1);
}

static void drawRadarLine()
{
	blitRotated(radarLineTexture, radarLine->x, radarLine->y, 1, radarLine->angle);
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

static void drawText()
{
	Entity* o;
	SDL_Texture* coordsText;
	char buffer[32];
	int x, y;
	
	// Draw FPS text
	snprintf(buffer, 32, "FPS: %d", stage.fps);
	textFPS = getTextTexture(buffer);
	drawNormalText(textFPS, WINDOW_WIDTH - 90, 0);

	// Draw obstacle coordinates
	for (o = stage.obstacleHead.next; o != NULL; o = o->next)
	{
		x = (o->x - radar->x) / (radar->w / MAX_D);
		y = -(o->y - radar->y);
		snprintf(buffer, 32, "Obstacle { Distance: %d, Angle: %d }", x, y);
		coordsText = getTextTexture(buffer);
	}
}

void getCoordsFromAngle(int angle, int radius, int* x, int* y)
{
	double a;

	a = angle * (double)(PI / 180.0);
	*x = (int) (cos(a) * radius);
	*y = (int) (sin(a) * radius);
	
	printf("l: %d, a: %f, x: %d, y: %d - angle: %d\n", radius, a, (*x), (*y), angle); // Debug / Test
}
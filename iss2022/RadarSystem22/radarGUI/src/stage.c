#include "stage.h"

static void logic();
static void draw();
// logic
void initStage();
static void resetStage();
static void initRadar();
static void doRadar();
static void doObstacles();
static void spawnObstacle(int x, int y);
static void spawnSus(int x, int y);
// draw
static void drawRadar();
static void drawRadarLine();
static void drawObstacles();
static void drawText();

static const DPC = SDL_ALPHA_OPAQUE / (FPS * CPS); // decrement to lasts for a complete cycle

static Entity* radar;
Entity* radarLine;
static SDL_Texture* radarTexture;
static SDL_Texture* radarLineTexture;
static SDL_Texture* obstacleTexture;
static SDL_Texture* susTexture;

static SDL_Texture* textFPS;
static SDL_Texture* coordinates[DIR_NUM];

static void logic()
{
	doRadar();

	doObstacles();
}

static void initRadar()
{
	// radar
	radar = malloc(sizeof(Entity));
	memset(radar, 0, sizeof(Entity));
	radar->texture = radarTexture;
	SDL_QueryTexture(radar->texture, NULL, NULL, &radar->w, &radar->h);
	radar->x = WINDOW_WIDTH / 2;
	radar->y = WINDOW_HEIGHT - radar->h / 2;

	// radarLine
	radarLine = malloc(sizeof(Entity));
	memset(radarLine, 0, sizeof(Entity));
	radarLine->texture = radarLineTexture;
	SDL_QueryTexture(radarLine->texture, NULL, NULL, &radarLine->w, &radarLine->h);
	radarLine->x = WINDOW_WIDTH / 2;
	radarLine->y = WINDOW_HEIGHT - radarLine->h / 2;
	radarLine->angle = -90.0;
}

void initStage()
{
	// Set the function pointer "delegates"
	app.delegate.logic = logic;
	app.delegate.draw = draw;

	memset(&stage, 0, sizeof(Stage));
	
	stage.obstacleTail = &stage.obstacleHead;

	// load textures
	radarTexture = loadTexture("gfx/radar.png");
	radarLineTexture = loadTexture("gfx/radar_line.png");
	obstacleTexture = loadTexture("gfx/obstacle2.png");
	susTexture = loadTexture("gfx/sus.png");

	resetStage();
}

static void resetStage()
{
	Entity* e;
	
	while (stage.obstacleHead.next)
	{
		e = stage.obstacleHead.next;
		stage.obstacleHead.next = e->next;
		free(e);
	}
	
	initRadar();

	stage.fps = 0;
}



// moves the line, add obstacles & sus, ...
static void doRadar()
{
	Distance* d, * prev;

	// rotate the radar line
	if (radarLine->angle >= 360)
		radarLine->angle = 0;
	radarLine->angle += ANGLE_INCREMENT;

	// scorre la lista di rilevazioni e se angle == radarLine->angle => chiama spawnObstacle()
	// calcola x & y, dunque spawnObstacle(x, y, ENTITY_LIFE) & spawnText(x, y, ENTITY_LIFE);

	prev = &interaction.distanceHead;

	for (d = interaction.distanceHead.next; d != NULL; d = d->next)
	{
		if (radarLine->angle == d->angle)
		{
			// show the obstacle only if it's between the bounds
			if (d->distance >= MIN_D && d->distance <= MAX_D)
			{
				int x, y, l;
				double a;
				l = radar->w / MAX_D;
				a = radarLine->angle * (double)(PI / 180.0);
				x = radar->x + (cos(a) * d->distance * l);
				y = radar->y + (sin(a) * d->distance * l);

				spawnObstacle(x, y);
				// spawnText(?)
				app.soundEnabled ? playSound(SND_OBJ_DETECTED, CH_OBJ) : (void)0;
			}

			if (d == interaction.distanceTail)
				interaction.distanceTail = prev;

			prev->next = d->next;
			free(d);
			d = prev;
		}

		prev = d;
	}
	
	
	if (app.susDetected && radarLine->angle == 360)
	{
		spawnSus(radar->x, radar->y);
		app.soundEnabled ? playSound(SND_SUS_DETECTED, CH_SUS) : (void)0;
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
	SDL_SetTextureBlendMode(o->texture, SDL_BLENDMODE_BLEND);

	o->alpha = SDL_ALPHA_OPAQUE;
}

// creates the Sus
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
	SDL_SetTextureBlendMode(s->texture, SDL_BLENDMODE_BLEND);

	s->alpha = SDL_ALPHA_OPAQUE;
}

static void draw()
{
	drawRadar();

	drawRadarLine();

	drawObstacles();

	drawText();
}

static void drawRadar()
{
	blit(radarTexture, radar->x, radar->y, 1);
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
	char buffer[32];
	
	// draw FPS text
	snprintf(buffer, 32, "FPS: %d", stage.fps);
	SDL_Texture* textFPS = getTextTexture(buffer);
	drawNormalText(textFPS, WINDOW_WIDTH - 90, 0);

	// to-do: for ...
}

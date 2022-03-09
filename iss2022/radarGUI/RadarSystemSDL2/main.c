#include "main.h"

void doReceive();
void detectSus(int x, int y);
void detectObj(int nSock, float distance);

void drawRadar();
void drawRadarLine(float decrement);
void drawObject(int dir);
void drawSus();
void drawObjectText(int dir, int objN, int offset);
void drawSusText(int offset);
void drawFPStext(int fps);
void drawCoordinateSystemText();

static void capFrameRate(long* then, float* remainder);

int main(int argc, char** argv)
{
	long then;
	float remainder;
	int fps, i, j, k;
	SDL_Point l1, l2, l3;
	double a1, a2, a3;
	Uint64 start, end;

	memset(&app, 0, sizeof(App));

	if (argc == 2 && strcmp(argv[1], "-s") == 0)
		app.soundEnabled = 1;

	srand(time(NULL));

	// Init SDL2 libraries
	initSDL();
	initSDLNetServer(DEFAULT_PORT);
	initSDLMixer();
	initSDLTtf();

	atexit(cleanup);

	// Init elements
	initRadar();
	initObjects();
	initSus();

	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE);

	then = SDL_GetTicks();
	remainder = 0;
	fps = 0;

	while (1)
	{
		start = SDL_GetPerformanceCounter();

		// 1. Prepare scene
		prepareScene();

		// 2. Get input
		doInput();
		doReceive();

		// 3. Update
		// Update Radar Line
		a1 = radar.angle * (double)(PI / 180.0);
		a2 = (radar.angle - 3.0) * (double)(PI / 180.0);
		a3 = (radar.angle - 6.0) * (double)(PI / 180.0);
		l1.x = radar.x + (cos(a1) * radar.l);
		l1.y = radar.y + (sin(a1) * radar.l);
		l2.x = radar.x + (cos(a2) * radar.l);
		l2.y = radar.y + (sin(a2) * radar.l);
		l3.x = radar.x + (cos(a3) * radar.l);
		l3.y = radar.y + (sin(a3) * radar.l);

		detectSus(radar.x, radar.y);
		radar.angle += 1.0;

		// 4. Draw
		drawRadar();

		// Draw Line
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l1.x, l1.y);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l2.x, l2.y);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l3.x, l3.y);

		// draw Objects and Text
		for (i = 0, j = 0, k = 0; i < DIR_NUM; i++)
		{
			if (objects[i].detected)
			{
				j++;

				// Object on radar
				drawObject(i);

				// Object coordinates text
				drawObjectText(i, j, k);
				k += 20;
			}
		}
		if (sus.detected)
		{
			// Sus on radar
			drawSus();

			// Sus coordinates text
			drawSusText(k);
		}

		drawFPStext(fps);

		// 5. Present scene
		presentScene();

		// Cap framerate to get ~same time to complete a full radar cycle
		capFrameRate(&then, &remainder);

		// Get FPS
		end = SDL_GetPerformanceCounter();
		float elapsed = (end - start) / (float)SDL_GetPerformanceFrequency();
		((int)radar.angle % (360 / DIR_NUM)) < 2 ? fps = (int)1.0f / elapsed : fps;
	}

	return 0;
}

void doReceive()
{
	float distance;

	if ((int)radar.angle % 45 == 0)
	{
		int dir = (int)radar.angle % 8;
		dir == 5 || dir == 7 ? dir -= 4 : (dir == 1 || dir == 3 ? dir += 4 : 0);

		distance = receiveDistanceFromSocket(dir);
		detectObj(dir, distance);

		objects[dir].detected = 0;
		if (app.objDetected[dir])
		{
			objects[dir].detected = 1;
			app.soundEnabled ? playSound(SND_OBJ_DETECTED, CH_ANY) : (void)0;
		}
		if (radar.angle == 360.0)
		{
			sus.detected = 0;
			if (app.susDetected)
			{
				sus.detected = 1;
				app.soundEnabled ? playSound(SND_SUS_DETECTED, CH_SUS) : (void)0;
			}
			radar.angle = 0.0;
		}
	}
}

static void capFrameRate(long* then, float* remainder)
{
	long wait, frameTime;

	wait = *remainder;

	*remainder -= (int)*remainder;

	frameTime = SDL_GetTicks() - *then;

	wait -= frameTime;

	if (wait < 1)
	{
		wait = 1;
	}

	SDL_Delay(wait);

	//*remainder += 16.667; // caps FPS to ~60 (remainder: 1000 / 60 = 16.66667)
	*remainder += 5; // a complete radar cycle in ~2 sec
	//*remainder += 2.25; // a complete radar cycle in ~1 sec

	*then = SDL_GetTicks();
}

void detectSus(int x, int y)
{
	sus.x = x + radar.w / 4;
	sus.y = y;
}

// update the coordinates
void detectObj(int nSock, float distance)
{
	if (distance > MIN_D && distance <= MAX_D)
	{
		// Set that the object has been detected
		app.objDetected[nSock] = 1;
		float l = (float)radar.l / MAX_D;
		double a = radar.angle * (double)(PI / 180.0);

		objects[nSock].x = radar.x + (cos(a) * distance * l);
		objects[nSock].y = radar.y + (sin(a) * distance * l);
	}
	else {
		app.objDetected[nSock] = 0;
	}
}

void drawRadar()
{
	blit(radar.texture, radar.x, radar.y, 1);
}
void drawRadarLine(float decrement)
{
	int a1, a2, a3;
	SDL_Point l1, l2, l3;
	
	
}
void drawObject(int dir)
{
	blit(objects[dir].texture, objects[dir].x, objects[dir].y, 1);
}
void drawSus()
{
	blit(sus.texture, sus.x, sus.y, 1);
}
void drawObjectText(int dir, int objN, int offset)
{
	char buffer[32];
	int xcoord, ycoord;
	xcoord = ((long) objects[dir].x - radar.x) / (radar.l / MAX_D);
	ycoord = -(objects[dir].y - radar.y);
	snprintf(buffer, 32, "Obj %d (%d, %d)", objN, xcoord, ycoord);
	SDL_Texture* textCoords = getTextTexture(buffer);
	drawNormalText(textCoords, 10, 0 + offset);
}
void drawSusText(int offset)
{
	char buffer[32];
	snprintf(buffer, 32, "Sus (%d, %d)", sus.x - radar.x, -(sus.y - radar.y));
	SDL_Texture* textCoords = getTextTexture(buffer);
	drawNormalText(textCoords, 10, 0 + offset);
}
void drawFPStext(int fps)
{
	char buffer[32];
	snprintf(buffer, 32, "FPS: %d", fps);
	SDL_Texture* textFPS = getTextTexture(buffer);
	drawNormalText(textFPS, SCREEN_WIDTH - 90, 0);
}
void drawCoordinateSystemText()
{
	
}
#include "main.h"

/*void doReceive();
void detectSus(int x, int y);
void detectObj(int nSock, float distance);

void drawRadar();
void drawRadarLine();
void drawObject(int dir);
void drawSus();
void drawObjectText(int dir, int objN, int offset);
void drawSusText(int offset);
void drawFPStext(int fps);*/

static void capFrameRate(long* then, float* remainder);

static int t = 1000 / FPS;
static int r;

int main(int argc, char** argv)
{
	long then;
	float remainder;

	r = (1000 / FPS) - (float)t;

	int i, j, k;
	Uint64 start, end;
	float elapsed;

	memset(&app, 0, sizeof(App));

	if (argc == 2 && strcmp(argv[1], "-s") == 0)
		app.soundEnabled = 1;

	srand(time(NULL));

	// Init SDL2 libraries
	initSDL();
	initSDLNet(DEFAULT_PORT);
	initSDLMixer();
	initSDLTtf();

	atexit(cleanup);

	initStage();

	initInteraction();

	then = SDL_GetTicks();
	remainder = 0;

	while (1)
	{
		start = SDL_GetPerformanceCounter();

		prepareScene();

		doInput();

		doReceive();
		// NB: doReceive calls function pointer to Interaction (receive); that function is set by initNet();

		app.delegate.logic();

		app.delegate.draw();

		presentScene();

		capFrameRate(&then, &remainder);

		end = SDL_GetPerformanceCounter();
		elapsed = (end - start) / (float)SDL_GetPerformanceFrequency();
		((int)radarLine->angle % (360 / DIR_NUM)) == 0 ? stage.fps = (int)1.0f / elapsed : stage.fps;
		
		/*
		// end cycle


		// 2. Get input
		

		doReceive();

		// 3. Update
		// Update Radar Line
		if (radar.angle == 360.0)
		{
			radar.angle = 0.0;
			radarLine.angle = 0.0;
		}

		detectSus(radar.x, radar.y);
		radar.angle += 1.0;
		radarLine.angle += 1.0;

		// 4. Draw
		drawRadar();
		drawRadarLine();

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
		*/
	}

	return 0;
}

static void capFrameRate(long* then, float* remainder)
{
	long wait, frameTime;

	wait = t + *remainder;

	*remainder -= (int)*remainder;

	frameTime = SDL_GetTicks() - *then;

	wait -= frameTime;

	if (wait < 1)
	{
		wait = 1;
	}

	SDL_Delay(wait);

	*remainder += r;

	*then = SDL_GetTicks();
}

/*static void capFrameRate(long* then, float* remainder)
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

	*remainder += 16.667; // caps FPS to ~60 (remainder: 1000 / 60 = 16.66667)
	//*remainder += 5; // a complete radar cycle in ~2 sec
	//*remainder += 2.25; // a complete radar cycle in ~1 sec

	*then = SDL_GetTicks();
}*/

/*void doReceive()
{
	float distance;

	if ((int)radar.angle % 45 == 0)
	{
		//
		// 0 (360)	% 8 == 0 OK
		// 45		% 8 == 5 NO -> 1
		// 90		% 8 == 2 OK
		// 135		% 8 == 7 NO -> 3
		// 180		% 8 == 4 OK
		// 225		% 8 == 1 NO -> 5
		// 270		% 8 == 6 OK
		// 315		% 8 == 3 NO -> 7
		//
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
		}
	}
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

		// Set the object coordinates
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
void drawRadarLine()
{
	blitRotated(radarLine.texture, radarLine.x, radarLine.y, 1, radarLine.angle);
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
	xcoord = (objects[dir].x - radar.x) / (radar.l / MAX_D);
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
	drawNormalText(textFPS, WINDOW_WIDTH - 90, 0);
}
*/
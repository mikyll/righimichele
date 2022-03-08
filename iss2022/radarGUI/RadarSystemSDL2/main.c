#include "main.h"

void doReceive();
static void capFrameRate(long* then, float* remainder);
void detectSus(int x, int y);
void detectObj(int nSock, float distance);

int main(int argc, char** argv)
{
	long then;
	float remainder;
	int fps, i, j, k;
	SDL_Point l1, l2, l3;
	double a1, a2, a3;
	float heading;

	memset(&app, 0, sizeof(App));

	if (argc == 2 && strcmp(argv[1], "-s") == 0)
		app.soundEnabled = 1;

	srand(time(NULL));

	initSDL();
	initSDLNetServer(DEFAULT_PORT);
	initSDLMixer();
	initSDLTtf();

	atexit(cleanup);

	initRadar();
	initObjects();
	initSus();

	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE);

	/*
	// Test: elapsed time
	uint32_t startTime = SDL_GetTicks();
	uint32_t stopTime;
	double elapsedTime;
	printf("radar.x = %d | radar.y = %d\nradar.l = %d\nradar.w = %d\n", radar.x, radar.y, radar.l, radar.w); // test
	*/

	then = SDL_GetTicks();
	remainder = 0;
	fps = 0;

	while (1)
	{
		Uint64 start = SDL_GetPerformanceCounter();

		// 1. Prepare scene
		prepareScene();

		// 2. Get input
		doInput();
		doReceive();

		// 3. Update
		/*if (radar.angle == 360.0)
		{
			radar.angle = 0.0;
			
			objects[0].detected = 0;
			sus.detected = 0;
			if (app.objDetected[0])
			{
				objects[0].detected = 1;
				app.soundEnabled ? playSound(SND_OBJ_DETECTED, CH_ANY) : (void)0;
			}
			if (app.susDetected)
			{
				sus.detected = 1;
				app.soundEnabled ? playSound(SND_SUS_DETECTED, CH_SUS) : (void)0;
			}
		}*/

		// Radar Line
		a1 = radar.angle * (double)(PI / 180.0);
		a2 = (radar.angle - 3.0) * (double)(PI / 180.0);
		a3 = (radar.angle - 6.0) * (double)(PI / 180.0);

		l1.x = radar.x + (cos(a1) * radar.l);
		l1.y = radar.y + (sin(a1) * radar.l);
		l2.x = radar.x + (cos(a2) * radar.l);
		l2.y = radar.y + (sin(a2) * radar.l);
		l3.x = radar.x + (cos(a3) * radar.l);
		l3.y = radar.y + (sin(a3) * radar.l);
		//printf("x2=%d, y2=%d, angle=%3.2f, a=%3.2f, cos(a)=%f, sin(a)=%f\n", x2, y2, angle, a1, cos(a1), sin(a1)); // test

		radar.angle += 1.0;

		detectSus(radar.x, radar.y);

		// 4. Draw
		blit(radar.texture, radar.x, radar.y, 1);

		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l1.x, l1.y);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE / 2);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l2.x, l2.y);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE / 3);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l3.x, l3.y);

		for (i = 0, j = 0, k = 0; i < SOCKET_NUM; i++)
		{
			if (objects[i].detected)
			{
				j++;

				// Object on radar
				blit(objects[i].texture, objects[i].x, objects[i].y, 1);

				// Object coordinates text
				char buffer[32];
				snprintf(buffer, 32, "Obj %d (%d, %d)", j, objects[i].x, objects[i].y);
				SDL_Texture* textCoords = getTextTexture(buffer);
				drawScaledText(textCoords, 10, 0 + k, 0.6, 0.6);
				k += 20;
			}
		}
		if (sus.detected)
		{
			// Sus on radar
			blit(sus.texture, sus.x, sus.y, 1);

			// Sus coordinates text
			char buffer[32];
			snprintf(buffer, 32, "Sus (%d, %d)", sus.x, sus.y);
			SDL_Texture* textCoords = getTextTexture(buffer);
			drawScaledText(textCoords, 10, 0 + k, 0.6, 0.6);
		}

		// FPS text
		char buffer[32];
		snprintf(buffer, 32, "FPS: %d", fps);
		SDL_Texture* textFPS = getTextTexture(buffer);
		drawScaledText(textFPS, SCREEN_WIDTH - 90, 0, 0.6, 0.6);

		// 5. Present scene
		presentScene();

		capFrameRate(&then, &remainder);

		Uint64 end = SDL_GetPerformanceCounter();

		float elapsed = (end - start) / (float)SDL_GetPerformanceFrequency();
		((int)radar.angle % (360 / SOCKET_NUM)) < 2 ? fps = (int)1.0f / elapsed : fps;
		//printf("FPS: %f\n", (float)1.0f / elapsed);

		// 3. Update objects
		/*switch ((int)angle)
		{
		case 45: // South-East
			doReceiveFromSocket(1);
			break;
		case 90: // South
			doReceiveFromSocket(2);
			break;
		case 135: // South-West
			doReceiveFromSocket(3);
			break;
		case 180: // West
			doReceiveFromSocket(4);
			break;
		case 225: // North-West
			doReceiveFromSocket(5);
			break;
		case 270: // North
			doReceiveFromSocket(6);
			break;
		case 315: // North-East
			doReceiveFromSocket(7);
			break;
		case 360: // East
			doReceiveFromSocket(0);

			if (app.objDetected[0])
				objects[0].detected = 1;
			break;
		}*/
	}

	return 0;
}

void doReceive()
{
	float distance;

	// Test (Direction EST, port 4123)
	//int deg = (int)radar.angle % (360 / SOCKET_NUM);
	//deg == 0 ? distance = receiveDistanceFromSocket(0), printf("[MAIN] 360) distance: %3.1f cm\n", d) : (float)0;

	if ((int)radar.angle % 45 == 0)
	{
		int a = (int)radar.angle % 8;
		a == 5 || a == 7 ? a -= 4 : (a == 1 || a == 3 ? a += 4 : 0);

		distance = receiveDistanceFromSocket(a);
		detectObj(a, distance);

		objects[a].detected = 0;
		if (app.objDetected[a])
		{
			objects[a].detected = 1;
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
			/*
			// Test: elapsed time
			stopTime = SDL_GetTicks();
			elapsedTime = (stopTime - startTime) / 1000.0;
			printf("\nELAPSED TIME: %f\n\n", elapsedTime);
			startTime = SDL_GetTicks();
			*/
			//printf("app.objDetected[0]=%d\nobjects[0].detected=%d\nOBJECT (%d, %d)\n", app.objDetected[0], objects[0].detected, objects[0].x, objects[0].y); // test
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
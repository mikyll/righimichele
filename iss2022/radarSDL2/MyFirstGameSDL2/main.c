#include "main.h"

void detectObject(int x, int y);
void detectSus(int x, int y);
static void capFrameRate(long* then, float* remainder);

int main(int argc, char ** argv)
{
	long then;
	float remainder;
	int i;
	SDL_Point l1, l2, l3;
	double a1, a2, a3;

	memset(&app, 0, sizeof(App));

	initSDL();
	initSDLNet();
	initSDLMixer();

	atexit(cleanup);

	initRadar();
	initObjects();
	initSus();

	SDL_SetRenderDrawColor(app.renderer, 0, 0, 0, SDL_ALPHA_OPAQUE);

	// Test
	uint32_t startTime = SDL_GetTicks();
	uint32_t stopTime;
	double elapsedTime;
	//printf("radar.x = %d | radar.y = %d\nradar.l = %d\nradar.w = %d\n", radar.x, radar.y, radar.l, radar.w); // test

	then = SDL_GetTicks();
	remainder = 0;

	if (argc == 2 && strcmp(argv[1], "-s") == 0)
		app.soundEnabled = 1;

	while (1)
	{
		Uint64 start = SDL_GetPerformanceCounter();

		// 1. Prepare scene
		prepareScene();

		// 2. Get input
		doInput();
		//doReceive();
		if ((int)radar.angle % (360 /*/ SOCKET_NUM*/) == 0) // NB: receive only when inside one of the 8 directions
			doReceiveFromSocket(0);

		// 3. Update
		if (radar.angle == 360.0)
		{
			radar.angle = 0.0;

			/*stopTime = SDL_GetTicks();
			elapsedTime = (stopTime - startTime) / 1000.0;
			printf("\nELAPSED TIME: %f\n\n", elapsedTime);
			startTime = SDL_GetTicks();*/
			
			//printf("app.objDetected[0]=%d\nobjects[0].detected=%d\nOBJECT (%d, %d)\n", app.objDetected[0], objects[0].detected, objects[0].x, objects[0].y); // test

			objects[0].detected = 0;
			sus.detected = 0;
			if (app.objDetected[0])
				objects[0].detected = 1;
			if (app.susDetected)
			{
				sus.detected = 1;
				app.soundEnabled ? playSound(SND_SUS_DETECTED, CH_SUS) : (void)0;
			}
		}


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

		// 4. Draw
		blit(radar.texture, radar.x, radar.y);

		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l1.x, l1.y);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE / 2);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l2.x, l2.y);
		SDL_SetRenderDrawColor(app.renderer, 0, 154, 23, SDL_ALPHA_OPAQUE / 3);
		SDL_RenderDrawLine(app.renderer, radar.x, radar.y, l3.x, l3.y);

		for (i = 0; i < SOCKET_NUM; i++)
		{
			objects[i].detected ? blit(objects[0].texture, objects[0].x, objects[0].y) : NULL;
		}
		if (sus.detected)
		{
			detectSus(radar.x, radar.y);
			blit(sus.texture, sus.x, sus.y);
		}

		// 5. Present scene
		presentScene();

		capFrameRate(&then, &remainder);

		Uint64 end = SDL_GetPerformanceCounter();

		float elapsed = (end - start) / (float)SDL_GetPerformanceFrequency();
		printf("FPS: %f\n", (float)1.0f / elapsed);

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

static void capFrameRate(long* then, float* remainder)
{
	long wait, frameTime;

	//wait = 16 + *remainder; // Caps FPS to ~60
	//wait = 4.4 + *remainder; // a complete radar cycle in ~2 sec
	wait = 1.6 + *remainder; // a complete radar cycle in ~1 sec

	*remainder -= (int)*remainder;

	frameTime = SDL_GetTicks() - *then;

	wait -= frameTime;

	if (wait < 1)
	{
		wait = 1;
	}

	SDL_Delay(wait);

	*remainder += 0.667;

	*then = SDL_GetTicks();
}

void detectSus(int x, int y)
{
	sus.x = x + radar.w / 4;
	sus.y = y;
}
#include <wiringPi.h>
#include <stdlib.h>
#include <signal.h>
#include <stdio.h>
#include <unistd.h>

#define GPIO_TRIGGER 4  // GPIO 23
#define GPIO_ECHO 5     // GPIO 24
#define GPIO_LED 6      // GPIO 25

void cleanup();
float distance();
void turnOnLed();
void turnOffLed();

int main()
{
        float d;
        
        wiringPiSetup();
        pinMode(GPIO_TRIGGER, OUTPUT);
        pinMode(GPIO_ECHO, INPUT);
        pinMode(GPIO_LED, OUTPUT);
        
        // set handler for cleanup
        struct sigaction sigIntHandler;
        
        sigIntHandler.sa_handler = cleanup;
        sigemptyset(&sigIntHandler.sa_mask);
        sigIntHandler.sa_flags = 0;

        sigaction(SIGINT, &sigIntHandler, NULL);

        for(;;)
        {
                d = distance();
                printf("%3.1f cm\n", d);
                d < 10.0 ? turnOnLed() : turnOffLed();
                delay(100); // ms
        }
          
        return 0;
}


void cleanup()
{
        digitalWrite (GPIO_LED, LOW);
        digitalWrite (GPIO_TRIGGER, LOW);
        printf("\nCleanup...\n\n");
        exit(0);
}

// to calculate the distance:
float distance()
{
        float startTime, stopTime, timeElapsed;
        
        // Set TRIGGER to HIGH
        digitalWrite(GPIO_TRIGGER, HIGH);
        
        // Set TRIGGER to LOW (after 1 millisecond)
        delayMicroseconds(100);
        digitalWrite(GPIO_TRIGGER, LOW);
        
        // Save start time
        while(digitalRead(GPIO_ECHO) == 0)
                startTime = micros();
        
        // Save stop time
        while(digitalRead(GPIO_ECHO) == 1)
                stopTime = micros();
        
        //printf("start: %d\nstop: %d\n", startTime, stopTime); // test
        
        // time difference between start and arrival
        timeElapsed = (stopTime - startTime) / 1000000;
        
        // multiply with the sonic speed (34300 cm/s) and divide by 2
        return (timeElapsed * 34300) / 2;
}

void turnOnLed()
{
        digitalWrite(GPIO_LED, HIGH);
}

void turnOffLed()
{
        digitalWrite(GPIO_LED, LOW);
}





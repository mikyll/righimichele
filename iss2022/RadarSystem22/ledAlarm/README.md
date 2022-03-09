<h1 align="center">LedAlarm</h1>

### Description
Use the ultrasonic sensor (HC-SR04) to turn on a LED when it detects an object located at a distance less than a certain threshold.

<table>
  <tr align="center">
    <td width="49%"><img src="https://github.com/mikyll/righimichele/blob/master/iss2022/RadarSystem22/ledAlarm/gfx/ledAlarm%20usage.gif"/></td>
    <td width="49%"><img src="https://github.com/mikyll/righimichele/blob/master/iss2022/RadarSystem22/ledAlarm/gfx/ledAlarm%20circuit.png"/></td>
  </tr>
</table>
 

### How does it work
#### Sonar
The sonar works by sending sound waves from the transmitter, which then bounce off of an object and then return to the receiver. It has a range of approximatively 3m (300cm).

<img width="50%" src="https://github.com/mikyll/righimichele/blob/master/iss2022/RadarSystem22/ledAlarm/gfx/HC-SR04-Ultrasonic-Sensor-Working-Echo-reflected-from-Obstacle.gif"/>

### Implementation
We set the TRIGGER pin of the Sonar to HIGH to send a sound wave, and set it back to LOW immediately after; then we set the *startTime* as soon as we read the LOW value from ECHO pin, and set the *stopTime*.<br/>
In fact, the ECHO pin produces a pulse when the reflected signal is received, and the length of the pulse is proportional to the time it took for the transmitted signal to be detected.<br/>
So we just have to calculate the difference between *stopTime* and *startTime* to get the *timeElapsed* and use it to calculate the *distance*:
knowing the signal travels at sound speed (which is 34300 cm/s) we just multiply the *timeElapsed* by the sound speed (keeping the same unit) and then divide by 2, since the *timeElapsed* is the total time that took the wave to go and come back.

#### Python Code
We use the [RPi.GPIO package](https://pypi.org/project/RPi.GPIO/) to send and read signals from the GPIO pins.
<details>
  <summary>View Code</summary>
  
```Python
#Libraries
import RPi.GPIO as GPIO
import time
 
#GPIO Mode (BOARD / BCM)
GPIO.setmode(GPIO.BCM)
 
#set GPIO Pins
GPIO_TRIGGER = 23
GPIO_ECHO = 24
GPIO_LED = 25
 
#set GPIO direction (IN / OUT)
GPIO.setup(GPIO_TRIGGER, GPIO.OUT)
GPIO.setup(GPIO_ECHO, GPIO.IN)
GPIO.setup(GPIO_LED, GPIO.OUT)
 
def distance():
    # set Trigger to HIGH
    GPIO.output(GPIO_TRIGGER, True)
 
    # set Trigger after 0.01ms to LOW
    time.sleep(0.00001)
    GPIO.output(GPIO_TRIGGER, False)
 
    StartTime = time.time()
    StopTime = time.time()
 
    # save StartTime
    while GPIO.input(GPIO_ECHO) == 0:
        StartTime = time.time()
 
    # save time of arrival
    while GPIO.input(GPIO_ECHO) == 1:
        StopTime = time.time()
 
    # time difference between start and arrival
    TimeElapsed = StopTime - StartTime
    # multiply with the sonic speed (34300 cm/s)
    # and divide by 2, because there and back
    distance = (TimeElapsed * 34300) / 2
 
    return distance

def turn_on_led():
    GPIO.output(GPIO_LED, True)
    return

def turn_off_led():
    GPIO.output(GPIO_LED, False)
    return
 
if __name__ == '__main__':
    try:
        while True:
            dist = distance()
            print ("Measured Distance = %.1f cm" % dist)
            if dist <= 10.0:
                turn_on_led()
            else:
                turn_off_led()
            time.sleep(0.1)
 
        # Reset by pressing CTRL + C
    except KeyboardInterrupt:
        print("Measurement stopped by User")
        GPIO.cleanup()
```
</details>
  
To execute run ```python ledAlarm.py```.

#### C Code
We use the C library **Wiring Pi**, which is a PIN based GPIO access library for the RaspberryPi (with Raspberry OS 32-bit). We then use sigaction to catch the CTRL+C event and handle it with a cleanup function. 

<details>
  <summary>View Code</summary>
  
```C
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
```
  
</details>


To compile run ```gcc ledAlarm.c -o ledAlarm -lwiringPi```.<br/>
To execute run ```./ledAlarm```.

  
### References
- [How does the HC-SR04 work](https://lastminuteengineers.com/arduino-sr04-ultrasonic-sensor-tutorial/)
- [Sonar tutorial](https://tutorials-raspberrypi.com/raspberry-pi-ultrasonic-sensor-hc-sr04/)
- [Sonar YT tutorial](https://www.youtube.com/watch?v=JYnMRKVwBuQ)
- [WiringPi: Reference](http://wiringpi.com/reference/)
- [WiringPi: Pin numbering](http://wiringpi.com/pins/)
- [WiringPi: Timing](http://wiringpi.com/reference/timing/)
- [sigaction example](https://stackoverflow.com/a/1641223)
- [LED utilis](https://www.lifewire.com/light-an-led-with-the-raspberry-pis-gpio-4090226)

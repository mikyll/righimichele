# RadarSystem SDL2 GUI

### Setup Visual Studio Project
1. [...]

### To-do List
- [ ] Refactor VS Project
- [ ] Sync UDP packets (use timestamp OR use sync semantic of the receive: client waits for the server ACK)
- [ ] Print coordinates on screen (sdl2_ttf)
- [ ] Show FPS on screen
- [ ] Send & Receive a Struct instead of a char*
- [ ] Substitute the radar line with a rectangle/texture(?)

- [ ] create server to async read from UDP socket
- [ ] create client that gets input from raspi and sends over the socket
- [ ] aggiungere audio per il SUS
- [ ] aggiungere font e scrivere le coordinate dei punti detectati


- creare semplice programmino client che gira su raspi, poi combinarlo con radar
- refactor progetto Visual Studio 
	- ricrearlo chiamandolo "RadarSystem"
	- scrivere i passaggi per linkare le varie librerie su Windows
	- dividere il codice in file diversi

FARE CLIENT STUPIDO SU WINDOWS (copiare progetto radarSDL) IN LOCAL HOST poi provarlo su raspi

abilitare il ping a Windows(?)




per i vari file (.c e .h):
creare file multiuso, ovvero in net.c / net.h ad esempio mettere sia receive che send, poi nel client o server uso quello specifico

- aggiungere RadarPacket
- mettere tutte le definizioni in defs.h
	NB: #define NORD, EST, SUD, WEST con le differenti porte
	
fare makefile per server e client (linkano diversi header e source file)

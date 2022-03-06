# RadarSystem SDL2 GUI

### Setup Visual Studio Project
1. [...]

### To-do List
- [ ] Refactor VS Project (RadarSystemSDL2) + write setup
- [ ] Sync UDP packets (use timestamp OR use sync semantic of the receive: client waits for the server ACK)
- [ ] Print coordinates on screen (sdl2_ttf)
- [ ] Show FPS on screen
- [ ] Send & Receive a Struct (DetectPacket) instead of a char*
- [ ] Substitute the radar line with a rectangle/texture(?)
- [ ] Finish the client app


per i vari file (.c e .h):
creare file multiuso, ovvero in net.c / net.h ad esempio mettere sia receive che send, poi nel client o server uso quello specifico

- mettere tutte le definizioni in defs.h
	NB: #define NORD, EST, SUD, WEST con le differenti porte
	
fare makefile per server e client (linkano diversi header e source file)

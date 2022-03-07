# RadarSystem SDL2 GUI

### Setup Visual Studio Project
1. [...]

### Compile (Linux, Windows, ...)

### To-do List
- [ ] Refactor VS Project (RadarSystemSDL2) + write setup
- [ ] Sync UDP packets (use timestamp OR use sync semantic of the receive: client waits for the server ACK)
- [ ] Print coordinates on screen (sdl2_ttf)
- [ ] Show FPS on screen
- [ ] Send & Receive a Struct (DetectPacket) instead of a char*
- [ ] Substitute the radar line with a rectangle/texture(?)
- [ ] Finish the client app
- [ ] Docker(?)
- [ ] Object animation (transition)


per i vari file (.c e .h):
creare file multiuso, ovvero in net.c / net.h ad esempio mettere sia receive che send, poi nel client o server uso quello specifico

- mettere tutte le definizioni in defs.h
	NB: #define NORD, EST, SUD, WEST con le differenti porte
	
fare makefile per server e client (linkano diversi header e source file)


### References
- [SDL_net Homepage](https://www.libsdl.org/projects/SDL_net/)
- [Optimize C Code](http://icps.u-strasbg.fr/~bastoul/local_copies/lee.html)
- [SDL2 Tutorials](https://www.parallelrealities.co.uk/tutorials/) - consider buying them, they are stupidly cheap and useful af!
- [Free Windows Installer Maker](https://nsis.sourceforge.io/Main_Page)
- [SDL_net UDP Example](https://moddb.fandom.com/wiki/SDL:Tutorial:Using_SDL_net#Using_UDP)

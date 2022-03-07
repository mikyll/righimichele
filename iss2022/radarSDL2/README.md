# RadarSystem SDL2 GUI

### Setup Visual Studio Project
1. Create a new empty project
2. Download Simple Directmedia Layer (SDL) 2.0 <ins>development libraries</ins> for Visual C++: [SDL2](https://www.libsdl.org/release/SDL2-devel-2.0.20-VC.zip), [SDL_image 2.0](https://www.libsdl.org/projects/SDL_image/release/SDL2_image-devel-2.0.5-VC.zip), [SDL_net 2.0](https://www.libsdl.org/projects/SDL_net/release/SDL2_net-devel-2.0.1-VC.zip), [SDL_ttf 2.0](https://github.com/libsdl-org/SDL_ttf/releases/download/release-2.0.18/SDL2_ttf-devel-2.0.18-VC.zip)

### Setup Linux
1. ```sudo apt-get install libsdl2-*```

### To-do List
- [ ] Refactor VS Project (RadarSystemSDL2) + write setup
- [ ] Sync communication (use timestamp OR use sync semantic of the receive: client waits for the server ACK). NB: in this case TCP would be better, since with UDP tha packet could go lost
- [ ] Print coordinates on screen (sdl2_ttf)
- [ ] Show FPS on screen
- [ ] Send & Receive a Struct (DetectPacket) instead of a char*
- [ ] Substitute the radar line with a rectangle/texture(?)
- [ ] Finish the client app
- [ ] Docker(?)
- [ ] Object animation (transition)
- [ ] Use 2 sonar(?): one for the x axis, the other for the y one.

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

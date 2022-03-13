<h1 align="center">RadarSystem SDL2</h1>

Implementation of a Radar System, written in C using the SDL2 library. The system consists of a **client**, which runs on the Raspberry Pi and gets the input from SR-HC04 sonar, and a **server** which shows a radar on GUI ad gets the coordinates of the detected objects from the client.

### Demo
Radar simulation, using a test client, which sends random inputs to the server.
<table>
	<tr align="center">
		<td width="53%"><a href="https://github.com/mikyll/righimichele/tree/master/iss2022/RadarSystem22/radarClient"><img alt="Client" src="https://github.com/mikyll/righimichele/blob/master/iss2022/RadarSystem22/userDocs/img/radarTestClient.gif"/></a></td>
		<td width="46%"><a href="https://github.com/mikyll/righimichele/tree/master/iss2022/RadarSystem22/radarGUI"><img alt="Server (radarGUI)" src="https://github.com/mikyll/righimichele/blob/master/iss2022/RadarSystem22/userDocs/img/radarTestServer.gif"/></a></td>
	</tr>
	<tr align="center">
		<td>Client (Raspberry Pi)</td>
		<td>Server (PC - radarGUI)</td>
	</tr>
</table>

### Usage

#### radarClient
#### radarGUI

### Setup Visual Studio Project
1. Download **Simple Directmedia Layer** (**SDL 2.0**) <ins>development libraries</ins> for Visual C++:
	1. [SDL2](https://www.libsdl.org/release/SDL2-devel-2.0.20-VC.zip);
	2. [SDL_image 2.0](https://www.libsdl.org/projects/SDL_image/release/SDL2_image-devel-2.0.5-VC.zip);
	3. [SDL_net 2.0](https://www.libsdl.org/projects/SDL_net/release/SDL2_net-devel-2.0.1-VC.zip);
	4. [SDL mixer 2.0](https://www.libsdl.org/projects/SDL_mixer/release/SDL2_mixer-devel-2.0.4-VC.zip);
	5. [SDL_ttf 2.0](https://github.com/libsdl-org/SDL_ttf/releases/download/release-2.0.18/SDL2_ttf-devel-2.0.18-VC.zip).
2. Extract each zip and rename the rispective directories in: SDL2, SDL2_image, SDL2_net, SDL2_mixer, SDL2_ttf.
3. Move those directories to C:\ (e.g. C:\SDL2).
4. Create a new empty Visual Studio project.
5. Set the project architecture to be **x64** (rather than x86).
6. Create a new file "main.c" (Right click on "source files", then "add new element").
7. Edit the project properties (Project > Properties):
	1. Select "**Configuration properties > C/C++ > General > Additional Include Directories**", and add the path of each include directory (e.g. "C:\SDL2\include" for SDL2, "C:\SDL2_image\include" for SDL2_image, etc.); 
	2. Select "**Configuration properties > Linker > General > Additional Library Directories**", and add the path of each lib directory (e.g. "C:\SDL2\lib\x64" for SDL2, "C:\SDL2_image\lib\x64" for SDL2_image, etc.);
	3. Select "**Configuration properties > Linker > Input > Additional Dependencies**", and add the SDL2 lib files that can be found in ".\lib\x64" (e.g. "SDL2.lib" and "SDL2main.lib" for SDL2, etc.).
	4. Select "**Configuration properties > C/C++ > Preprocessor > Preprocessor definitions**" and add \_CRT_SECURE_NO_WARNINGS
8. Add all the **.dll** to the source files directory (they can be found in ".\lib\x64\").

### Compile with MinGW
```gcc -std=c17 ".\prova.c" -I<path\to\include> -L<path\to\lib> -Wall -lmingw32 -lSDL2main -lSDL2 -o main```

### Setup Linux
1. ```sudo apt-get install libsdl2-*```

### Roadmap (to-do list)
- [ ] add TCP communication
- [ ] Sync communication (use timestamp OR use sync semantic of the receive: client waits for the server ACK). NB: in this case TCP would be better, since with UDP tha packet could go lost
- [ ] Send & Receive a Struct (DetectPacket) instead of a char*
- [ ] Substitute the radar line with a rectangle/texture(?)
- [ ] Refactor client app (and add makefile)
- [ ] Docker(?)
- [ ] Object animation (transition/disapperance)
- [ ] Use 2 sonar(?): one for the x axis, the other for the y one.
- [ ] Make single modules more general purpose
- [ ] Client: add passing port as argument by specifying the direction (e.g. North/N -> DEFAULT_PORT + i)
- [ ] GitHub action to dynamically generate pdf from HTML userdocs and commit it

### References
- [SDL_net Homepage](https://www.libsdl.org/projects/SDL_net/)
- [Optimize C Code](http://icps.u-strasbg.fr/~bastoul/local_copies/lee.html)
- [SDL2 Tutorials](https://www.parallelrealities.co.uk/tutorials/) - consider buying them, they are stupidly cheap and useful af!
- [Free Windows Installer Maker](https://nsis.sourceforge.io/Main_Page)
- [SDL_net UDP Example](https://moddb.fandom.com/wiki/SDL:Tutorial:Using_SDL_net#Using_UDP)

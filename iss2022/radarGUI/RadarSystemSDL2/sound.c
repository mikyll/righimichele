#include "sound.h"

void initSounds();
static Mix_Chunk* loadSound(char* filename);

static Mix_Chunk* sounds[SND_MAX];

void initSDLMixer()
{
    if (Mix_OpenAudio(22050, MIX_DEFAULT_FORMAT, 2, 1024) == -1)
    {
        printf("Couldn't initialize SDL Mixer: %s\n", Mix_GetError());
        app.soundEnabled = 0;
        return;
    }

    Mix_AllocateChannels(MAX_SND_CHANNELS);

    initSounds();
}

void initSounds()
{
    memset(sounds, 0, sizeof(Mix_Chunk*) * SND_MAX);

    sounds[SND_OBJ_DETECTED] = loadSound("sound/sonar_ping.mp3");
    sounds[SND_SUS_DETECTED] = loadSound("sound/sus.mp3");
}

void playSound(int id, int channel)
{
    Mix_PlayChannel(channel, sounds[id], 0);
}

static Mix_Chunk* loadSound(char* filename)
{
    Mix_Chunk* sound;

    SDL_LogMessage(SDL_LOG_CATEGORY_APPLICATION, SDL_LOG_PRIORITY_INFO, "Loading %s", filename);

    sound = Mix_LoadWAV(filename);

    return sound;
}

#include "sound.h"

void initSounds();
static void loadSounds();

static Mix_Chunk* sounds[SND_MAX];
static Mix_Music* music;

void initSDLMixer()
{
    if (Mix_OpenAudio(22050, MIX_DEFAULT_FORMAT, 2, 1024) == -1)
    {
        printf("Couldn't initialize SDL Mixer\n");
        exit(1);
    }

    Mix_AllocateChannels(MAX_SND_CHANNELS);

    initSounds();
}

void initSounds()
{
    memset(sounds, 0, sizeof(Mix_Chunk*) * SND_MAX);

    music = NULL;

    loadSounds();
}

void playSound(int id, int channel)
{
    Mix_PlayChannel(channel, sounds[id], 0);
}

static void loadSounds(void)
{
    sounds[SND_SUS_DETECTED] = Mix_LoadWAV("sound/sus.mp3");
}

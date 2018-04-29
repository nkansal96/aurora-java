package com.auroraapi.models;

public class Speech {
    private Audio audio;

    public Speech(Audio audio) {
        setAudio(audio);
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public static Speech listen(int length, float silence_len) {
        return new Speech(Audio.record(length, silence_len));
    }
}

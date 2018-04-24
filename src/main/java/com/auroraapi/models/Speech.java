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

    private Text getText() {
        throw new RuntimeException("Not implemented yet!");
    }

    private Intention getIntention() {
        return getText().getIntention();
    }
}

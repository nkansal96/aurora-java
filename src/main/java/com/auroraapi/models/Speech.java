package com.auroraapi.models;

import com.auroraapi.Aurora;

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

    private Text getText() throws AuroraException {
        return Aurora.getText(this);
    }

    private Interpret getInterpretation() throws AuroraException {
        return getText().getInterpretation();
    }
}

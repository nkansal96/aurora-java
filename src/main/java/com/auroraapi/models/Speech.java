package com.auroraapi.models;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

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

    /**
     * Keeps recording until silence is encountered
     *
     * @param params The Audio recording parameters
     * @return A speech object containing the recorded audio
     * @throws LineUnavailableException If microphone was unavailable
     */
    public static Speech listen(Audio.Params params) throws IOException, LineUnavailableException {
        return new Speech(Audio.record(params));
    }
}

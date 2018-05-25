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
     * Records audio and returns a new Speech object containing the recorded audio
     * @param millis The millis of time, in milliseconds, to record
     * @return A speech object containing recorded audio
     * @throws LineUnavailableException If microphone was unavailable
     */
    public static Speech listenForTime(long millis) throws LineUnavailableException {
        return new Speech(Audio.timedRecord(millis));
    }

    /**
     * Keeps recording until silence is encountered
     * @param silenceLength the length of silence at which to stop stop recording in milliseconds
     * @return A speech object containing the recorded audio
     */
    public static Speech listenUntilSilence(long silenceLength) throws IOException, LineUnavailableException {
        return new Speech(Audio.silenceRecord(silenceLength));
    }
}

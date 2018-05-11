package com.auroraapi.models;

import javax.sound.sampled.LineUnavailableException;

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
     *
     * @param millis The millis of time, in milliseconds, to record
     * @param silenceLength TODO: describe what this is
     * @return A speech objected containing recorded audio
     * @throws LineUnavailableException If microphone was unavailable
     */
    public static Speech listen(long millis, float silenceLength) throws LineUnavailableException {
        return new Speech(Audio.record(millis, 0));
    }
}

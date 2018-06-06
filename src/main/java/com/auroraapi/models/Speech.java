package com.auroraapi.models;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
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
     * @throws IOException              If there was an error saving the recording
     */
    public static Speech listen(Audio.Params params) throws IOException, LineUnavailableException {
        return new Speech(Audio.record(params));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speech speech = (Speech) o;
        return audio != null ? audio.equals(speech.audio) : speech.audio == null;
    }

    @Override
    public int hashCode() {
        return audio != null ? audio.hashCode() : 0;
    }
}

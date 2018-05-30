package com.auroraapi.callbacks;

import com.auroraapi.models.Speech;

public interface SpeechCallback {
    /**
     * This function is called to obtain the results of a continuous listening operation.
     * @param speech The recorded speech
     * @return True to continue listening, False to stop listening
     */
    boolean onSpeech(Speech speech);

    /**
     * This function is called when there is some sort of error, either in listening or network, or otherwise
     * @param throwable The exception or throwable that was thrown
     * @return True to continue listening, False to stop listening
     */
    boolean onError(Throwable throwable);
}

package com.auroraapi.callbacks;

import com.auroraapi.models.Transcript;

public interface TranscriptCallback {
    /**
     * This function is called to obtain the results of a continuous listening operation.
     * @param transcript The transcript of the Audio that was just played
     * @return True to continue listening, False to stop listening
     */
    boolean onTranscript(Transcript transcript);

    /**
     * This function is called when there is some sort of error, either in listening or network, or otherwise
     * @param throwable The exception or throwable that was thrown
     * @return True to continue listening, False to stop listening
     */
    boolean onError(Throwable throwable);
}

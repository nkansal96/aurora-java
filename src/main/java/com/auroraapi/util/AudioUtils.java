package com.auroraapi.util;

public class AudioUtils {
    private static final int SILENT_THRESH = 1 << 11;
    /**
     * Checks if a chunk of data consists of silence
     * @param data The audio to check for silence
     * @param length The number of valid audio data bytes in data
     * @return Whether the audio consists of silence
     */
    public static boolean isSilent(byte[] data, int length) {
        // TODO: Implement
        return true;
    }
}

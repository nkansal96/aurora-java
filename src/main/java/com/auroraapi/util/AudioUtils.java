package com.auroraapi.util;

public class AudioUtils {
    private static final int SILENT_THRESH = 1 << 5;

    /**
     * Checks if a chunk of data consists of silence
     *
     * @param data   The audio to check for silence
     * @param length The number of valid audio data bytes in data
     * @return Whether the audio consists of silence
     */
    public static boolean isSilent(byte[] data, int length) {
        long sum = 0;
        for (byte datum : data) {
            sum += Math.abs(datum);
        }
        return (sum / length) < SILENT_THRESH;
    }
}

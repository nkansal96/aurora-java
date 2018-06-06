package com.auroraapi.util;

import java.util.Arrays;

public class AudioUtils {
    private static final int SILENT_THRESH = 1 << 5;
    private static final int MAX_SILENCE_LENGTH = 32; // In units of buffer size

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

    /**
     * Trims silence audio so that only the most recent silence bytes are recorded
     * @param silentAudioData The raw silent only audio
     * @param bufferSize The size of the recording buffer
     * @return A trimmed representation of the silence audio
     */
    public static byte[] trimSilence(byte[] silentAudioData, int bufferSize) {
        int maxLen = MAX_SILENCE_LENGTH * bufferSize;
        if (silentAudioData.length > maxLen) {
            return Arrays.copyOfRange(silentAudioData, silentAudioData.length - maxLen, silentAudioData.length);
        }
        return silentAudioData;
    }
}

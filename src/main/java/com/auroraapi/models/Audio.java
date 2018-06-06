package com.auroraapi.models;

import com.auroraapi.util.AudioUtils;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.auroraapi.util.AudioUtils.isSilent;
import static com.auroraapi.util.AudioUtils.trimSilence;

/**
 * Currently only supports WAVE filetype, but can be extended to support other filetypes
 */
public class Audio {

    private static final int NUM_CHANNELS = 1;
    private static final int SAMPLE_SIZE = 16;
    private static final int RATE = 16000;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;
    private static final AudioFormat format = new AudioFormat(RATE, SAMPLE_SIZE, NUM_CHANNELS, SIGNED, BIG_ENDIAN);
    private static final int BUF_SIZE = 1024;

    private byte[] data;

    public Audio() {
    }

    public Audio(byte[] data) {
        setData(data);
    }

    public static Audio record(Params params) throws IOException, LineUnavailableException {
        if (params.length != 0) {
            return timedRecord(params.length);
        } else if (params.silenceLength != 0) {
            return silenceRecord(params.silenceLength);
        } else {
            throw new IllegalArgumentException("Either length or silence length must be nonzero");
        }
    }

    /**
     * Records audio and returns a new Audio object containing the recorded audio
     *
     * @param millis The millis of time, in milliseconds, to record
     * @return A new Audio object containing the recorded audio
     * @throws LineUnavailableException If the mic is unavailble
     */
    public static Audio timedRecord(long millis) throws LineUnavailableException {
        ByteArrayOutputStream audioByteData = new ByteArrayOutputStream();
        TargetDataLine line = AudioSystem.getTargetDataLine(format);
        byte[] buffer = new byte[line.getBufferSize()];
        line.open(format);
        line.start();

        long stopTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < stopTime) {
            int numBytesRead = line.read(buffer, 0, buffer.length);
            audioByteData.write(buffer, 0, numBytesRead);
        }
        line.stop();
        line.close();
        return new Audio(audioByteData.toByteArray());
    }

    /**
     * Keeps recording until silence is encountered
     *
     * @param silenceLength length of silence to stop recording at in milliseconds (has granularity of about ±250 ms)
     * @return A new Audio object containing the recorded audio
     * @throws LineUnavailableException If there is a mic error
     * @throws IOException If there is an error saving the recording
     */
    public static Audio silenceRecord(final long silenceLength) throws LineUnavailableException, IOException {
        ByteArrayOutputStream audioByteData = new ByteArrayOutputStream();
        ByteArrayOutputStream silenceAudio = new ByteArrayOutputStream();
        TargetDataLine line = AudioSystem.getTargetDataLine(format);
        byte[] buffer = new byte[BUF_SIZE];
        line.open(format);
        line.start();
        boolean wasPreviouslySilent = false;
        long silenceStartTime = System.currentTimeMillis();
        long elapsedSilence = 0;
        boolean startedRecording = false;
        while (!startedRecording || elapsedSilence < silenceLength) {
            int numBytesRead = line.read(buffer, 0, buffer.length);
            if (isSilent(buffer, numBytesRead)) {
                if (!wasPreviouslySilent) {
                    wasPreviouslySilent = true;
                    silenceStartTime = System.currentTimeMillis();
                }
                elapsedSilence = System.currentTimeMillis() - silenceStartTime;
                silenceAudio.write(buffer, 0, numBytesRead);
            } else {
                wasPreviouslySilent = false;
                startedRecording = true;
                elapsedSilence = 0;
                if (silenceAudio.size() > 0) {
                    audioByteData.write(trimSilence(silenceAudio.toByteArray(), BUF_SIZE));
                    silenceAudio.reset();
                }
                audioByteData.write(buffer, 0, numBytesRead);

            }
        }
        line.stop();
        line.close();
        return new Audio(audioByteData.toByteArray());
    }

    /**
     * Create an Audio instance by copying data over from a file
     *
     * @param pathname the filename/path
     * @return A new Audio object containing the file data
     * @throws IOException If there is an error reading from file
     */
    public static Audio fromFile(String pathname) throws IOException {
        return new Audio(Files.readAllBytes(Paths.get(pathname)));
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return "audio/x-wav";
    }

    /**
     * Plays back the audio contained in this object
     *
     * @param shouldBlock Whether to make playing the audio a blocking operation
     * @throws LineUnavailableException If the speaker is not available
     * @throws InterruptedException     If the audio is unable finish playing
     */
    public void play(boolean shouldBlock) throws LineUnavailableException, InterruptedException {
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(info);
        clip.open(format, data, 0, data.length);
        clip.start();
        if (shouldBlock) {
            // wait for clip to start playing
            while (!clip.isRunning()) {
                Thread.sleep(10);
            }
            // wait for clip to finish playing
            while (clip.isRunning()) {
                Thread.sleep(10);
            }
        }
    }

    /**
     * Plays back the audio contained in this object (blocking)
     *
     * @throws LineUnavailableException If the speaker is not available
     * @throws InterruptedException     If the audio is unable finish playing
     */
    public void play() throws LineUnavailableException, InterruptedException {
        play(true);
    }

    /**
     * Write the audio data contained in this object to a wav file
     *
     * @param filename The name of the file to write to, should end in ".wav"
     * @throws IOException if there is an error writing the file
     */
    public void writeToFile(String filename) throws IOException {
        File wavFile = new File(filename);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format,
                data.length / format.getFrameSize());

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);

        audioInputStream.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audio audio = (Audio) o;
        return Arrays.equals(data, audio.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /**
     * This class is used to specify audio recording parameters
     */
    public static class Params {
        /**
         * The default silence length in milliseconds
         */
        public static final long DEFAULT_SILENCE_LENGTH = 500;
        /**
         * The default listen length in milliseconds
         */
        public static final long DEFAULT_LISTEN_LENGTH = 0;
        /**
         * The length of time to record audio for. If this is nonzero, it overrides silenceLength
         */
        public long length;

        /**
         * The amount of silence before the audio recording is stopped
         */
        public long silenceLength;

        /**
         * @param length        The length of time to record audio for. If this is nonzero, it overrides silenceLength
         * @param silenceLength The amount of silence before the audio recording is stopped
         */
        public Params(long length, long silenceLength) {
            this.length = length;
            this.silenceLength = silenceLength;
        }

        /**
         * The default/standard audio recording parameters
         *
         * @return A new Params instance
         */
        public static Params getDefaultParams() {
            return new Params(DEFAULT_LISTEN_LENGTH, DEFAULT_SILENCE_LENGTH);
        }
    }
}

package com.auroraapi.models;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.auroraapi.util.AudioUtils.isSilent;

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

    private byte[] data;

    public Audio() {
    }

    public Audio(byte[] data) {
        setData(data);
    }

    /**
     * Records audio and returns a new Audio object containing the recorded audio
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
     * @param silenceLength the length of silence at which to stop stop recording in milliseconds
     * @return A new Audio object containing the recorded audio
     */
    public static Audio silenceRecord(long silenceLength) throws LineUnavailableException, IOException {
        // TODO: This is completely untested and not sure if it works yet
        ByteArrayOutputStream audioByteData = new ByteArrayOutputStream();
        ByteArrayOutputStream silenceAudio = new ByteArrayOutputStream();
        TargetDataLine line = AudioSystem.getTargetDataLine(format);
        byte[] buffer = new byte[line.getBufferSize()];
        line.open(format);
        line.start();
        boolean wasPreviouslySilent = false;
        long silenceStartTime = System.currentTimeMillis();
        long elapsedSilence = 0;
        while (elapsedSilence < silenceLength) {
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
                elapsedSilence = 0;
                if (silenceAudio.size() > 0) {
                    audioByteData.write(silenceAudio.toByteArray());
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
        // this.data = Arrays.copyOf(data, data.length);
    }

    public String getContentType() {
        return "audio/x-wav";
    }

    /**
     * Plays back the audio contained in this object
     * @param shouldBlock Whether to make playing the audio a blocking operation
     * @throws LineUnavailableException If the speaker is not available
     * @throws InterruptedException If the audio is unable finish playing
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
     * @throws LineUnavailableException If the speaker is not available
     * @throws InterruptedException If the audio is unable finish playing
     */
    public void play() throws LineUnavailableException, InterruptedException {
        play(true);
    }

    /**
     * Write the audio data contained in this object to a wav file
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
}

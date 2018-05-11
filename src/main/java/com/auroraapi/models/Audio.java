package com.auroraapi.models;

import sun.audio.AudioPlayer;

import javax.sound.sampled.*;
import java.io.*;

public class Audio {
    static final int NUM_CHANNELS = 1;
    static final int SAMPLE_SIZE = 16;
    static final int RATE = 16000;
    static final boolean SIGNED = true;
    static final boolean BIG_ENDIAN = true;

    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    boolean isRecording = false;

    TargetDataLine line;

    AudioFormat format = new AudioFormat(RATE, SAMPLE_SIZE, NUM_CHANNELS, SIGNED, BIG_ENDIAN);

    private byte[] data;

    public Audio() {
    }

    public Audio(byte[] data) {
        setData(data);
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
     */
    public void play() {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream audioStream = new AudioInputStream(bais, format,
                    data.length / format.getFrameSize());
            AudioFormat audioFormat = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            clip.start();
            while (!clip.isRunning())
                Thread.sleep(10);
            while (clip.isRunning())
                Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        try {
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            byte[] bytes = new byte[line.getBufferSize() / 5];
            int numBytesRead;
            while (isRecording) {
                numBytesRead = line.read(bytes, 0, bytes.length);
                dataStream.write(bytes, 0, numBytesRead);
            }
            line.stop();
            line.close();

            byte[] dataBytes = dataStream.toByteArray();
            setData(dataBytes);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private void finishRecording() {
        isRecording = false;
    }

    /**
     * Records audio and returns a new Audio object containing the recorded audio
     * @param length The length of time, in seconds, to record
     * @param silenceLength TODO: describe what this is
     * @return A new Audio object containing the recorded audio
     */
    public static Audio record(int length, float silenceLength) {
        final Audio audio = new Audio();
        audio.isRecording = true;

        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(length * 1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                audio.finishRecording();
            }
        });

        stopper.start();
        audio.startRecording();

        return audio;
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

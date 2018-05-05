package com.auroraapi.models;

import javax.sound.sampled.*;
import java.io.*;

class Audio {
  static final int NUM_CHANNELS = 1;
  static final int SAMPLE_SIZE = 16;
  static final int RATE = 16000;
  static final boolean SIGNED = true;
  static final boolean BIG_ENDIAN = true;

  AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

  // the line from which audio data is captured
  TargetDataLine line;

  AudioFormat format = new AudioFormat(RATE, SAMPLE_SIZE, NUM_CHANNELS, SIGNED, BIG_ENDIAN);

  ByteArrayOutputStream data = new ByteArrayOutputStream();

  void start() {
    try {
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

      // checks if system supports the data line
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line not supported");
        System.exit(0);
      }
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();   // start capturing

      System.out.println("Start capturing...");

      AudioInputStream ais = new AudioInputStream(line);

      System.out.println("Start recording...");

      // start recording
      AudioSystem.write(ais, fileType, data);

    } catch (LineUnavailableException ex) {
      ex.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Closes the target data line to finish capturing and recording
   */
  void finish() {
    line.stop();
    line.close();
    System.out.println("Finished");
  }

  public static Audio record(int length, float silence_length) {
    final Audio audio = new Audio();

    Thread stopper = new Thread(new Runnable() {
      public void run() {
        try {
          Thread.sleep(length * 1000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
        audio.finish();
      }
    });

    stopper.start();

    audio.start();

    return audio;
  }

  private byte[] data;

  public Audio(byte[] data) {
      setData(data);
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
}

package com.auroraapi.models;

import org.jpab.PortAudio;
import org.jpab.StreamConfiguration.SampleFormat;

public class Audio {
  static final int BUF_SIZE = Math.pow(2, 10);
  static final int SILENT_THRESH = Math.pow(2, 11);
  static final int NUM_CHANNELS = 1;
  static final int FORMAT = SampleFormat.SIGNED_INTEGER_16;
  static final int RATE = 16000;

  public RecordResponse record(float length, float silence_len) {
    PortAudio.initialize();

    Device d = PortAudio.getDevices().get(1);

    return new RecordResponse();
  }
}

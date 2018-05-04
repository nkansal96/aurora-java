package com.auroraapi.models;

public class Audio {
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

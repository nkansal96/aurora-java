package com.auroraapi.models;

public class Transcript {
    private String transcript;

    public Transcript(String transcript) {
        setTranscript(transcript);
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transcript that = (Transcript) o;
        return transcript != null ? transcript.equals(that.transcript) : that.transcript == null;
    }

    @Override
    public int hashCode() {
        return transcript != null ? transcript.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Transcript{transcript='" + transcript + "'}";
    }
}

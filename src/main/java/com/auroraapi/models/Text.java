package com.auroraapi.models;

public class Text {
    private String text;

    public Text(String text) {
        setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Speech toSpeech() {
        throw new RuntimeException("Not implemented yet!");
    }

    public Intention getIntention() {
        throw new RuntimeException("Not implemented yet!");
    }
}

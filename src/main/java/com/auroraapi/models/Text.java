package com.auroraapi.models;

import com.auroraapi.Aurora;

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

    public Speech getSpeech() throws Exception {
        return Aurora.getSpeech(this);
    }

    public Interpret getInterpretation() throws Exception {
        return Aurora.getInterpretation(this);
    }

    @Override
    public String toString() {
        return text;
    }
}

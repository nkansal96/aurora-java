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

    public Speech getSpeech() throws AuroraException {
        return Aurora.getSpeech(this);
    }

    public Interpret getInterpretation() throws AuroraException {
        return Aurora.getInterpretation(this);
    }
}

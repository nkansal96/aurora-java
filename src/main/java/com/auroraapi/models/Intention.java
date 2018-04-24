package com.auroraapi.models;

import java.util.Map;

public class Intention {
    private Text text;
    private String intent;
    private Map<String, String> entities;

    public Intention(Text text, String intent, Map<String, String> entities) {
        setText(text);
        setIntent(intent);
        setEntities(entities);
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Map<String, String> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }
}

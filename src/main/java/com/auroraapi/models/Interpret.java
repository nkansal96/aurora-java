package com.auroraapi.models;

import java.util.Map;

public class Interpret {
    private Text text;
    private String intent;
    private Map<String, String> entities;

    public Interpret(Text text, String intent, Map<String, String> entities) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interpret interpret = (Interpret) o;
        if (text != null ? !text.equals(interpret.text) : interpret.text != null) return false;
        if (intent != null ? !intent.equals(interpret.intent) : interpret.intent != null) return false;
        return entities != null ? entities.equals(interpret.entities) : interpret.entities == null;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (intent != null ? intent.hashCode() : 0);
        result = 31 * result + (entities != null ? entities.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Interpret{" +
                "text=" + text +
                ", intent='" + intent + '\'' +
                ", entities=" + entities +
                '}';
    }
}

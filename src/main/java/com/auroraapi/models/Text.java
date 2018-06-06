package com.auroraapi.models;

@SuppressWarnings("WeakerAccess")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text1 = (Text) o;
        return text != null ? text.equals(text1.text) : text1.text == null;
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Text{text='" + text + "'}";
    }
}

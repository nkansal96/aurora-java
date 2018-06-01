package com.auroraapi.networking;

import com.auroraapi.models.Text;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

public class TextTypeAdapter {
    @FromJson
    public Text fromJson(String text) {
        return new Text(text);
    }

    @ToJson
    public String toJson(Text text) {
        return text.getText();
    }
}

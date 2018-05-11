package com.auroraapi.examples;

import com.auroraapi.Aurora;
import com.auroraapi.models.Audio;
import com.auroraapi.models.AuroraException;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;

import java.io.IOException;

public class TextToSpeech {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        Text text = new Text("Hello World!");
        System.out.println("Example tts usage for: " + text.toString());

        try {
            Speech speech = Aurora.getSpeech(text);
            Audio audio = speech.getAudio();

            // TODO: Uncomment when audio has play implemented
            // audio.play();
        } catch (AuroraException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

# Aurora Java SDK


## Overview

Aurora is the enterprise end-to-end speech solution. This Java SDK will allow you to quickly and easily use the Aurora service to integrate voice capabilities into your application.

The SDK is currently in a pre-alpha release phase. Bugs and limited functionality should be expected.

## Installation

**The Recommended Java version is 8+**

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.auroraapi</groupId>
  <artifactId>aurora-java</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

### Gradle users
Add this dependency to your project's build file:

```groovy
implementation 'com.auroraapi:aurora-java:1.0.0'
```

## Basic Usage

First, make sure you have an account with [Aurora](http://dashboard.auroraapi.com) and have created an Application.

### Text to Speech (TTS)

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.Audio;
import com.auroraapi.models.AuroraException;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;

import javax.sound.sampled.LineUnavailableException;
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

            audio.play();
        } catch (AuroraException e) {
            e.printStackTrace();
        } catch (LineUnavailableException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}

```

### Speech to Text (STT)

#### Convert a WAV file to Speech

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.*;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class SpeechToText {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        try {
            Audio audio = Audio.fromFile("text.wav");
            Speech speech = new Speech(audio);
            Transcript transcript = Aurora.getTranscript(speech);

            System.out.println("Transcript: " + transcript.toString());
        } catch (AuroraException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### Convert a previous Text API call to Speech
```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.AuroraException;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;
import com.auroraapi.models.Transcript;

import java.io.IOException;
public class TextAPICallToSpeech {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        Text text = new Text("Hello World!");
        System.out.println("Example tts usage for: " + text.toString());

        try {
            Speech speech = Aurora.getSpeech(text);
            Transcript transcript = Aurora.getTranscript(speech);

            System.out.println("Transcription: " + transcript.toString());
        } catch (AuroraException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### Listen for a specified amount of time
```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.*;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class ListenSpecifiedTime {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        Audio.Params params = new Audio.Params(3L, Audio.Params.DEFAULT_SILENCE_LENGTH);

        try {
            Speech speech = Speech.listen(params);
            Transcript transcript = Aurora.getTranscript(speech);

            System.out.println("Transcription: " + transcript.getTranscript());
        } catch (AuroraException e) {
            e.printStackTrace();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
```

#### Listen for an unspecified amount of time

Calling this API will start listening and will automatically stop listening after a certain amount of silence (default is 1.0 seconds).
```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.*;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class ListenUnspecifiedTime {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        Audio.Params params = new Audio.Params(Audio.Params.DEFAULT_LISTEN_LENGTH, 1L);

        try {
            Speech speech = Speech.listen(params);
            Transcript transcript = Aurora.getTranscript(speech);

            System.out.println("Transcription: " + transcript.getTranscript());
        } catch (AuroraException e) {
            e.printStackTrace();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}

```

#### Continuously listen

Continuously listen and retrieve speech segments. Note: you can do anything with these speech segments, but here we'll convert them to text. Just like the previous example, these segments are demarcated by silence (1.0 second by default) and can be changed by passing the `silenceLength` parameter. Additionally, you can make these segments fixed length (as in the example before the previous) by setting the `length` parameter.

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.callbacks.SpeechCallback;
import com.auroraapi.models.*;

public class ContinuouslyListen {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        SpeechCallback callback = new SpeechCallback() {
            @Override
            public boolean onSpeech(Speech speech) {
                System.out.println("Speech: " + speech.toString());
                return false;
            }

            @Override
            public boolean onError(Throwable throwable) {
                throwable.printStackTrace();
                return false;
            }
        };

        // Params are optional
        Aurora.continuouslyListen(callback);
    }
}
```

#### Listen and Transcribe

If you already know that you wanted the recorded speech to be converted to text, you can do it in one step, reducing the amount of code you need to write and also reducing latency. Using the `continuouslyListenAndTranscribe` method, the audio that is recorded automatically starts uploading as soon as you call the method and transcription begins. When the audio recording ends, you get back the final transcription.

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.callbacks.TranscriptCallback;
import com.auroraapi.models.*;

public class ListenAndTranscribe {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        TranscriptCallback callback = new TranscriptCallback() {
            @Override
            public boolean onTranscript(Transcript transcript) {
                System.out.println("Transcription: " + transcript.getTranscript());
                return true;
            }

            @Override
            public boolean onError(Throwable throwable) {
                throwable.printStackTrace();
                return false;
            }
        };

        // NOTE: Params are optional and the silence and listen lengths are customizable.
        Aurora.continuouslyListenAndTranscribe(callback);
    }
}
```

#### Listen and echo example

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.callbacks.TranscriptCallback;
import com.auroraapi.models.*;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class ListenAndTranscribe {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        TranscriptCallback callback = new TranscriptCallback() {
            @Override
            public boolean onTranscript(Transcript transcript) {
                Text text = new Text(transcript.getTranscript());

                try {
                    Speech speech = Aurora.getSpeech(text);
                    Audio audio = speech.getAudio();
                    audio.play();

                    return true;
                } catch (AuroraException e) {
                    e.printStackTrace();
                    return false;
                } catch (LineUnavailableException | InterruptedException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public boolean onError(Throwable throwable) {
                throwable.printStackTrace();
                return false;
            }
        };

        // NOTE: Params are optional and the silence and listen lengths are customizable.
        Aurora.continuouslyListenAndTranscribe(callback);
    }
}
```

### Interpret (Language Understanding)

The interpret service allows you to take any Aurora `Text` object and understand the user's intent and extract additional query information. Interpret can only be called on `Text` objects and return `Interpret` objects after completion. To convert a user's speech into and `Interpret` object, it must be converted to text first.

#### Basic example

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.*;
import java.io.IOException;
import java.util.Map;

public class TextToInterpret {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        try {
            String str = "what is the weather in los angeles";
            Text text = new Text(str);

            Interpret interpret = Aurora.getInterpretation(text);

            String interpretation = "Interpretation:\n";
            interpretation += String.format("Intent: %s\n", interpret.getIntent());

            Map<String, String> entities = interpret.getEntities();
            for (String key : entities.keySet()) {
                String value = entities.get(key);
                interpretation += String.format("Entity key: %s, value: %s", key, value);
            }

            System.out.println(interpretation);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuroraException e) {
            e.printStackTrace();
        }
    }
}

```

#### User query example

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.models.*;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class UserQuery {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);

                // Assume string input
                Text text = new Text(scanner.nextLine());

                Interpret interpret = Aurora.getInterpretation(text);

                String interpretation = "Interpretation:\n";
                interpretation += String.format("Intent: %s\n", interpret.getIntent());

                Map<String, String> entities = interpret.getEntities();
                for (String key : entities.keySet()) {
                    String value = entities.get(key);
                    interpretation += String.format("Entity key: %s, value: %s", key, value);
                }

                System.out.println(interpretation);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuroraException e) {
                e.printStackTrace();
            }
        }
    }
}
```

#### Smart Lamp

This example shows how easy it is to voice-enable a smart lamp. It responds to queries in the form of "turn on the lights" or "turn off the lamp". You define what `object` you're listening for (so that you can ignore queries like "turn on the music").

```Java
package main;

import com.auroraapi.Aurora;
import com.auroraapi.callbacks.TranscriptCallback;
import com.auroraapi.models.*;

import java.io.IOException;

public class LampExample {
    public static void main(String[] args) {
        String appId = "<put your appId here>";
        String appToken = "<put your appToken here>";

        Aurora.init(appId, appToken);

        TranscriptCallback callback = new TranscriptCallback() {
            @Override
            public boolean onTranscript(Transcript transcript) {
                Text text = new Text(transcript.getTranscript());
                String[] validWords = {"light", "lights", "lamp"};

                try {
                    Interpret interpret = Aurora.getInterpretation(text);
                    String intent = interpret.getIntent();
                    String object = interpret.getEntities().get("object");

                    for (String word : validWords) {
                        if (object.equals(word)) {
                            if (intent.equals("turn_on")) {
                                // turn on the lamp
                            } else if (intent.equals("turn_off")) {
                                // turn off the lamp
                            }

                            break;
                        }
                    }

                    return true;
                } catch (AuroraException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public boolean onError(Throwable throwable) {
                throwable.printStackTrace();
                return false;
            }
        };

        Audio.Params params = Audio.Params.getDefaultParams();
        Aurora.continuouslyListenAndTranscribe(callback, params);
    }
}
```

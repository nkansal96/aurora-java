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
  <groupId>com.aurora</groupId>
  <artifactId>sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle users
Add this dependency to your project's build file:

```groovy
implementation "com.aurora:sdk:1.0.0"
```

## Basic Usage

First, make sure you have an account with [Aurora](http://dashboard.auroraapi.com) and have created an Application.

### Text to Speech (TTS)

```Java
# Import the package
import auroraapi as aurora
from auroraapi.text import Text

# Set your application settings
aurora.config.app_id    = "YOUR_APP_ID"     # put your app ID here
aurora.config.app_token = "YOUR_APP_TOKEN"  # put your app token here

# query the TTS service
speech = Text("Hello world").speech()

# play the resulting audio
speech.audio.play()

# or save it to a file
speech.audio.write_to_file("test.wav")
```

### Speech to Text (STT)

#### Convert a WAV file to Speech

```Java
# Import the package
import auroraapi as aurora
from auroraapi.audio import AudioFile
from auroraapi.speech import Speech

# Set your application settings
aurora.config.app_id    = "YOUR_APP_ID"      # put your app ID here
aurora.config.app_token = "YOUR_APP_TOKEN"   # put your app token here

# open an existing WAV file (16-bit, mono, 16KHz WAV PCM)
with open("test.wav", "rb") as f:
  a = AudioFile(f.read())
  p = Speech(a).text()
  print(p.text) # 'hello world'
```

#### Convert a previous Text API call to Speech
```Java
from auroraapi.text import Text
from auroraapi.speech import Speech

# Call the TTS API to convert "Hello world" to speech
speech = Text("Hello world").speech()

# Previous API returned a Speech object, so we can just call
# the text() method to get a prediction
p = speech.text()
print(p.text) # 'hello world'
```

#### Listen for a specified amount of time
```Java
from auroraapi.speech import listen

# Listen for 3 seconds (silence is automatically trimmed)
speech = listen(length=3)

# Convert to text
p = speech.text()
print(p.text) # prints the prediction
```

#### Listen for an unspecified amount of time

Calling this API will start listening and will automatically stop listening after a certain amount of silence (default is 1.0 seconds).
```Java
from auroraapi.speech import listen

# Start listening until 1.0s of silence
speech = listen()
# Or specify your own silence timeout (0.5 seconds shown here)
speech = listen(silence_len=0.5)

# Convert to text
p = speech.text()
print(p.text) # prints the prediction
```

#### Continuously listen

Continuously listen and retrieve speech segments. Note: you can do anything with these speech segments, but here we'll convert them to text. Just like the previous example, these segments are demarcated by silence (1.0 second by default) and can be changed by passing the `silence_len` parameter. Additionally, you can make these segments fixed length (as in the example before the previous) by setting the `length` parameter.

```Java
from auroraapi.speech import continuously_listen

# Continuously listen and convert to speech (blocking example)
for speech in continuously_listen():
  p = speech.text()
  print(p.text)

# Reduce the amount of silence in between speech segments
for speech in continuously_listen(silence_len=0.5):
  p = speech.text()
  print(p.text)

# Fixed-length speech segments of 3 seconds
for speech in continuously_listen(length=3.0):
  p = speech.text()
  print(p.text)
```

#### Listen and Transcribe

If you already know that you wanted the recorded speech to be converted to text, you can do it in one step, reducing the amount of code you need to write and also reducing latency. Using the `listen_and_transcribe` method, the audio that is recorded automatically starts uploading as soon as you call the method and transcription begins. When the audio recording ends, you get back the final transcription.

```Java
from auroraapi.speech import listen_and_transcribe, continuously_listen_and_transcribe

text = listen_and_transcribe(silence_len=0.5):
print("You said: {}".format(text.text))

# You can also use this in the same way as `continuously_listen`
for text in continuously_listen_and_transcribe(silence_len=0.5):
  print("You said: {}".format(text.text))
```

#### Listen and echo example

```Java
from auroraapi.speech import continuously_listen_and_transcribe

for text in continuously_listen_and_transcribe():
  text.speech().audio.play()
```

### Interpret (Language Understanding)

The interpret service allows you to take any Aurora `Text` object and understand the user's intent and extract additional query information. Interpret can only be called on `Text` objects and return `Interpret` objects after completion. To convert a user's speech into and `Interpret` object, it must be converted to text first.

#### Basic example

```Java
from auroraapi.text import Text

# create a Text object
text = Text("what is the time in los angeles")

# call the interpret service. This returns an `Interpret` object.
i = text.interpret()

# get the user's intent
print(i.intent)   # time

# get any additional information
print(i.entities) # { "location": "los angeles" }
```

#### User query example

```Java
from auroraapi.text import Text

while True:
  # Repeatedly ask the user to enter a command
  user_text = raw_input("Enter a command:")
  if user_text == "quit":
    break
  
  # Interpret and print the results
  i = Text(user_text).interpret()
  print(i.intent, i.entities)
```

#### Smart Lamp

This example shows how easy it is to voice-enable a smart lamp. It responds to queries in the form of "turn on the lights" or "turn off the lamp". You define what `object` you're listening for (so that you can ignore queries like "turn on the music").

```Java
from auroraapi.speech import continuously_listen

valid_words = ["light", "lights", "lamp"]
valid_entities = lambda d: "object" in d and d["object"] in valid_words

for speech in continuously_listen(silence_len=0.5):
  i = speech.text().interpret()
  if i.intent == "turn_on" and valid_entities(i.entities):
    # do something to actually turn on the lamp
    print("Turning on the lamp")
  elif i.intent == "turn_off" and valid_entities(i.entities):
    # do something to actually turn off the lamp
    print("Turning off the lamp")
```

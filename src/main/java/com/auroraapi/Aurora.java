package com.auroraapi;

import com.auroraapi.callbacks.SpeechCallback;
import com.auroraapi.callbacks.TranscriptCallback;
import com.auroraapi.models.*;
import com.auroraapi.networking.AuroraService;
import com.auroraapi.networking.SpeechConverterFactory;
import com.auroraapi.networking.TextTypeAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.converter.moshi.MoshiConverterFactory;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class Aurora {

    private static final String BASE_URL_V1 = "https://api.auroraapi.com/v1/";
    private static Aurora instance;
    private static AuroraService service;
    private static Converter<ResponseBody, AuroraException> exceptionConverter;

    private Aurora(AuroraService auroraService) {
        service = auroraService;
    }

    /**
     * Initializes and authenticates an instance of Aurora
     *
     * @param appId    The App ID obtained from the Aurora Dashboard
     * @param appToken The App Token obtained from the Aurora Dashboard
     */
    public static void init(String appId, String appToken) {
        init(appId, appToken, null);
    }

    /**
     * Initializes and authenticates an instance of Aurora
     *
     * @param appId    The App ID obtained from the Aurora Dashboard
     * @param appToken The App Token obtained from the Aurora Dashboard
     * @param deviceId The Device ID for analytics on Aurora Dashboard
     */
    public static void init(String appId, String appToken, String deviceId) {
        if (instance == null) {
            Interceptor authInterceptor = chain -> {
                Request.Builder builder = chain.request().newBuilder()
                        .addHeader("X-Application-ID", appId)
                        .addHeader("X-Application-Token", appToken);
                if (deviceId != null && deviceId.length() > 0) {
                    builder.addHeader("X-Device-ID", deviceId);
                }
                return chain.proceed(builder.build());
            };

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(authInterceptor).build();

            Moshi moshi = new Moshi.Builder()
                    .add(new TextTypeAdapter()) // Converts type String to type Text
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(new SpeechConverterFactory())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .baseUrl(BASE_URL_V1)
                    .client(client)
                    .build();
            exceptionConverter = retrofit.responseBodyConverter(AuroraException.class, new Annotation[0]);
            instance = new Aurora(retrofit.create(AuroraService.class));
        }
    }

    /**
     * Initializes a mock instance of Aurora for testing purposes
     *
     * @param auroraService auroraService User's custom service
     */
    public static void init(AuroraService auroraService) {
        if (instance == null) {
            instance = new Aurora(auroraService);
        }
    }

    /**
     * Converts speech to text
     *
     * @param speech User speech
     * @return Transcript of provided speech
     * @throws IOException     if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Transcript getTranscript(Speech speech) throws AuroraException, IOException {
        checkInitialized();
        return returnOrThrow(service.getTranscript(speech).execute());
    }

    /**
     * Converts text to speech
     *
     * @param text User input text
     * @return Aurora transcribed speech from provided text
     * @throws IOException     if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Speech getSpeech(Text text) throws AuroraException, IOException {
        checkInitialized();
        return returnOrThrow(service.getSpeech(text).execute());
    }

    /**
     * Get the interpretation of some Text
     *
     * @param text    User input text
     * @param modelId The ID of the model to query
     * @return Aurora interpretation from provided text
     * @throws IOException     if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Interpret getInterpretation(Text text, String modelId) throws AuroraException, IOException {
        checkInitialized();
        return returnOrThrow(service.getInterpret(text, modelId).execute());
    }

    /**
     * Get the interpretation of some Text
     *
     * @param text User input text
     * @return Aurora interpretation from provided text
     * @throws IOException     if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Interpret getInterpretation(Text text) throws AuroraException, IOException {
        checkInitialized();
        return getInterpretation(text, null);
    }

    /**
     * Continuously listen for audio and return the Speech segments as they are recorded
     *
     * @param callback The callback that is invoked on every receipt of some Speech
     * @param params   The audio recording parameters
     */
    public static void continuouslyListen(SpeechCallback callback, Audio.Params params) {
        checkInitialized();
        new Thread(() -> {
            boolean shouldContinue = true;
            while (shouldContinue) {
                try {
                    shouldContinue = callback.onSpeech(Speech.listen(params));
                } catch (IOException | LineUnavailableException e) {
                    shouldContinue = callback.onError(e);
                }
            }
        }).start();
    }

    /**
     * Continuously listen for audio and return the Speech segments as they are recorded
     *
     * @param callback The callback that is invoked on every receipt of Transcript for some Speech
     */
    public static void continuouslyListen(SpeechCallback callback) {
        checkInitialized();
        continuouslyListen(callback, Audio.Params.getDefaultParams());
    }

    /**
     * Directly get the Transcript for one Speech segment
     *
     * @param params The audio recording parameters
     * @return A Transcript of the user's Speech
     * @throws LineUnavailableException if there is an error recording audio
     * @throws IOException              if there is an error parsing the response
     * @throws AuroraException          if there is an API-side error
     */
    public static Transcript listenAndTranscribe(Audio.Params params) throws IOException, LineUnavailableException, AuroraException {
        checkInitialized();
        return getTranscript(Speech.listen(params));
    }

    /**
     * Directly get the Transcript for one Speech segment
     *
     * @return A Transcript of the user's Speech
     * @throws LineUnavailableException if there is an error recording audio
     * @throws IOException              if there is an error parsing the response
     * @throws AuroraException          if there is an API-side error
     */
    public static Transcript listenAndTranscribe() throws AuroraException, IOException, LineUnavailableException {
        checkInitialized();
        return listenAndTranscribe(Audio.Params.getDefaultParams());
    }

    /**
     * Continuously listen and automatically get the Transcript for chunks of recorded speech
     *
     * @param callback The callback that is invoked on every receipt of Transcript for some Speech
     * @param params   The audio recording parameters
     */
    public static void continuouslyListenAndTranscribe(TranscriptCallback callback, Audio.Params params) {
        checkInitialized();
        continuouslyListen(new SpeechCallback() {
            @Override
            public boolean onSpeech(Speech speech) {
                try {
                    return callback.onTranscript(getTranscript(speech));
                } catch (AuroraException | IOException e) {
                    return callback.onError(e);
                }
            }

            @Override
            public boolean onError(Throwable throwable) {
                return callback.onError(throwable);
            }
        }, params);
    }

    /**
     * Continuously listen and automatically get the Transcript for chunks of recorded speech
     *
     * @param callback The callback that is invoked on every receipt of Transcript for some Speech
     */
    public static void continuouslyListenAndTranscribe(TranscriptCallback callback) {
        checkInitialized();
        continuouslyListenAndTranscribe(callback, Audio.Params.getDefaultParams());
    }

    /**
     * Checks if current instance of Aurora is initialized and ready for use
     *
     * @throws RuntimeException if instance has not been initialized
     */
    private static void checkInitialized() {
        if (instance == null) {
            throw new RuntimeException("Please call Aurora.init() first");
        }
    }

    /**
     * Will return response body if successful, otherwise will convert the API error to an AuroraException and throw
     *
     * @param response The Response obtained by executing a Request
     * @param <T>      The data type of the response body
     * @return The response body
     * @throws IOException     if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    private static <T> T returnOrThrow(Response<T> response) throws IOException, AuroraException {
        if (response.isSuccessful()) {
            return response.body();
        }
        throw getAuroraException(response);
    }

    /**
     * Converts a Response to an AuroraException
     *
     * @param response The Response obtained by executing a Request
     * @return An AuroraException, which represents the API side error that occured
     * @throws IOException if there was an error parsing the response as an AuroraException
     */
    private static AuroraException getAuroraException(Response response) throws IOException {
        return exceptionConverter.convert(response.errorBody());
    }
}

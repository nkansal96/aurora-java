package com.auroraapi;

import com.auroraapi.models.*;
import com.squareup.moshi.Moshi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;

public class Aurora {

    private static final String BASE_URL_V1 = "https://api.auroraapi.com/v1/";
    private static Aurora instance;
    private static AuroraService service;
    private static Converter<ResponseBody, AuroraException> exceptionConverter;
    private static String modelId;

    private Aurora(AuroraService auroraService) {
        service = auroraService;
    }

    /**
     * Initializes and authenticates an instance of Aurora
     * @param String appId User's Application ID
     * @param String appToken
     * @throws IOException if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static void init(String appId, String appToken) {
        if (instance == null) {
            Interceptor authInterceptor = chain -> chain.proceed(chain.request().newBuilder()
                    .addHeader("X-Application-ID", appId)
                    .addHeader("X-Application-Token", appToken)
                    .build());

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
     * @param AuroraService auroraService User's custom service
     */
    public static void init(AuroraService auroraService) {
        if (instance == null) {
            instance = new Aurora(auroraService);
        }
    }

    public static void setModel(String model) {
        modelId = model;
    }

    /**
     * Aurora speech to text
     * @param Text User input text
     * @return Aurora transcribed text from provided speech
     * @throws IOException if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Transcript getText(Speech speech) throws AuroraException, IOException {
        checkInitialized();
        return returnOrThrow(service.getText(speech).execute());
    }

    /**
     * Aurora text to speech
     * @param Text User input text
     * @return Aurora transcribed speech from provided text
     * @throws IOException if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Speech getSpeech(Text text) throws AuroraException, IOException {
        checkInitialized();
        return returnOrThrow(service.getSpeech(text).execute());
    }

    /**
     * Aurora text to interpretation
     * @param Text User input text
     * @return Aurora interpretation from provided text
     * @throws IOException if there is an error parsing the response
     * @throws AuroraException if there is an API-side error
     */
    public static Interpret getInterpretation(Text text) throws AuroraException, IOException {
        checkInitialized();
        return returnOrThrow(service.getInterpret(text, modelId).execute());
    }

    /**
     * Checks if current instance of Aurora is initialized and ready for use
     * @throws RuntimeException if instance has not been initialized
     */
    private static void checkInitialized() {
        if (instance == null) {
            throw new RuntimeException("Please call Aurora.init() first");
        }
    }

    /**
     * Will return response body if successful, otherwise will convert the API error to an AuroraException and throw
     * @param response The Response obtained by executing a Request
     * @param <T> The data type of the response body
     * @return The response body
     * @throws IOException if there is an error parsing the response
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
     * @param response The Response obtained by executing a Request
     * @return An AuroraException, which represents the API side error that occured
     * @throws IOException if there was an error parsing the response as an AuroraException
     */
    private static AuroraException getAuroraException(Response response) throws IOException {
        return exceptionConverter.convert(response.errorBody());
    }
}

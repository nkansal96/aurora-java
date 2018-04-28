package com.auroraapi;

import com.auroraapi.models.AuroraException;
import com.auroraapi.models.Interpret;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;
import com.squareup.moshi.Moshi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private static Retrofit retrofit;
    private static String modelId;

    private Aurora(AuroraService auroraService) {
        service = auroraService;
    }

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
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .baseUrl(BASE_URL_V1)
                    .client(client)
                    .build();

            instance = new Aurora(retrofit.create(AuroraService.class));
        }
    }

    public static void init(AuroraService auroraService) {
        if (instance == null) {
            instance = new Aurora(auroraService);
        }
    }

    public static void setModel(String model) {
        modelId = model;
    }

    public static Text getText(Speech speech) throws AuroraException, IOException {
        checkInitialized();
        Response<Text> response = service.getText(speech).execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            Converter<ResponseBody, AuroraException> converter = retrofit.responseBodyConverter(AuroraException.class, new Annotation[0]);
            throw converter.convert(response.errorBody());
        }
    }

    public static Speech getSpeech(Text text) throws AuroraException, IOException {
        checkInitialized();
        Response<Speech> response = service.getSpeech(text).execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            Converter<ResponseBody, AuroraException> converter = retrofit.responseBodyConverter(AuroraException.class, new Annotation[0]);
            throw converter.convert(response.errorBody());
        }
    }

    public static Interpret getInterpretation(Text text) throws AuroraException, IOException {
        checkInitialized();
        Response<Interpret> response = service.getInterpret(text, modelId).execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            Converter<ResponseBody, AuroraException> converter = retrofit.responseBodyConverter(AuroraException.class, new Annotation[0]);
            throw converter.convert(response.errorBody());
        }
    }

    private static void checkInitialized() {
        if (instance == null) {
            throw new RuntimeException("Please call Aurora.init() first");
        }
    }
}

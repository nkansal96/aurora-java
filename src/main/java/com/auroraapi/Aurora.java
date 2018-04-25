package com.auroraapi;

import com.auroraapi.models.AuroraException;
import com.auroraapi.models.Interpret;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class Aurora {

    private static final String BASE_URL_V1 = "https://api.auroraapi.com/v1";
    private static Aurora instance;
    private static AuroraService service;

    private Aurora(String appId, String appToken) {
        Interceptor interceptor = chain -> chain.proceed(chain.request().newBuilder()
                .addHeader("X-Application-ID", appId)
                .addHeader("X-Application-Token", appToken)
                .build());
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL_V1)
                .client(client)
                .build();

        service = retrofit.create(AuroraService.class);
    }

    public static Aurora getInstance(String appId, String appToken) {
        if (instance == null) {
            instance = new Aurora(appId, appToken);
        }

        return instance;
    }

    public static Text getText(Speech speech) throws AuroraException {
        throw new RuntimeException("Not implemented yet!");
    }

    public static Speech getSpeech(Text text) throws AuroraException {
        throw new RuntimeException("Not implemented yet!");
    }

    public static Interpret getInterpretation(Text text) throws AuroraException {
        throw new RuntimeException("Not implemented yet!");
    }
}

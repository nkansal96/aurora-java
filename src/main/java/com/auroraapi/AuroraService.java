package com.auroraapi;

import com.auroraapi.models.Interpret;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AuroraService {

    @GET("tts")
    Call<Speech> getSpeech(@Query("text") Text text);

    @GET("stt")
    Call<Text> getText(@Body Speech speech);

    @GET("interpret")
    Call<Interpret> getInterpret(@Query("text") Text text, @Query("model") String model);

}

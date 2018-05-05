package com.auroraapi;

import com.auroraapi.models.Interpret;
import com.auroraapi.models.Speech;
import com.auroraapi.models.Text;
import com.auroraapi.models.Transcript;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuroraService {

    @GET("tts")
    Call<Speech> getSpeech(@Query("text") Text text);

    @Multipart
    @POST("stt")
    Call<Transcript> getText(@Part("speech") Speech speech);

    @GET("interpret")
    Call<Interpret> getInterpret(@Query("text") Text text, @Query("model") String model);

}

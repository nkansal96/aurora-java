package com.auroraapi.networking;

import com.auroraapi.models.Audio;
import com.auroraapi.models.Speech;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class SpeechConverterFactory extends Converter.Factory {
    /**
     * Converts a ResponseBody if it is of Type Speech
     *
     * @return Speech or null
     */
    @Override
    public Converter<ResponseBody, Speech> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return type == Speech.class ? responseBody -> {
            Speech speech = new Speech(new Audio(responseBody.bytes()));
            responseBody.close();
            return speech;
        } : null;
    }

    /**
     * Converts a RequestBody if it is of Type Speech
     *
     * @return Speech or null
     */
    @Override
    public Converter<Speech, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return type == Speech.class ? speech -> RequestBody.create(MediaType.parse(speech.getAudio().getContentType()), speech.getAudio().getData()) : null;
    }
}

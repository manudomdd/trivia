package com.example.trivia.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    // IMPORTANTE: La URL base siempre debe terminar en "/"
    private static final String BASE_URL = "https://opentdb.com/";

    public static TriviaApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Aquí usamos Gson para traducir
                    .build();
        }
        return retrofit.create(TriviaApiService.class);
    }
}
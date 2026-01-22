package com.example.trivia.api;

import com.example.trivia.models.TriviaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaApiService {

    // La URL base será "https://opentdb.com/"
    // Aquí definimos el resto de la ruta: "api.php"

    @GET("api.php")
    Call<TriviaResponse> getQuestions(
            @Query("amount") int amount,       // Cuántas preguntas (ej: 10)
            @Query("category") String category, // ID de categoría (ej: 9)
            @Query("difficulty") String difficulty, // "easy", "medium", "hard"
            @Query("type") String type          // "multiple" o "boolean"
    );
}
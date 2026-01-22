package com.example.trivia.models;

import com.example.trivia.models.Question;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TriviaResponse {

    @SerializedName("results")
    private List<Question> results;

    public List<Question> getResults() {
        return results;
    }
}

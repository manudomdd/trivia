package com.example.trivia.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Question {

    @SerializedName("question")
    private String questionText;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("incorrect_answers")
    private List<String> incorrectAnswers;

    // Getters (Click derecho -> Generate -> Getters)
    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }
}
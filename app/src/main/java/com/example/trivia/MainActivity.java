package com.example.trivia;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;

import com.example.trivia.models.Question;
import com.example.trivia.api.WebSocketManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvQuestion, tvScore, tvOpponentScore, tvGameStatus;
    private TextView tvFinalScore, tvFinalOpponentScore, tvWinnerText;

    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext, btnRestart;
    private ProgressBar progressBar;
    private Group gameGroup, gameOverGroup;
    private MaterialCardView cardScoreHeader;
    private List<Button> optionButtons;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int opponentScore = 0;
    private boolean isAnswered = false;
    private boolean isWaitingForResults = false;
    private WebSocketManager socketManager;
    private String roomId;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        socketManager = WebSocketManager.getInstance();
        socketManager.connect();

        setupSocketListeners();

        btnNext.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                showQuestion();
            } else {
                goToWaitingScreen();
            }
        });

        btnRestart.setOnClickListener(v -> buscarPartida());
        buscarPartida();
    }

    private void buscarPartida() {
        score = 0;
        opponentScore = 0;
        isWaitingForResults = false;

        tvScore.setText("Yo: 0");
        tvOpponentScore.setText("Rival: 0");
        tvGameStatus.setText("Buscando oponente...");
        tvGameStatus.setTextColor(Color.WHITE);

        gameOverGroup.setVisibility(View.GONE);
        gameGroup.setVisibility(View.GONE);
        cardScoreHeader.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        socketManager.getSocket().emit("join_game");
        Toast.makeText(this, "Buscando oponente...", Toast.LENGTH_SHORT).show();
    }

    private void goToWaitingScreen() {
        isWaitingForResults = true;
        gameGroup.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvGameStatus.setText("¡Terminaste! Esperando al rival...");
        tvGameStatus.setTextColor(Color.YELLOW);

        try {
            JSONObject json = new JSONObject();
            json.put("roomId", roomId);
            socketManager.getSocket().emit("player_finished", json);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void setupSocketListeners() {
        socketManager.getSocket().on("game_start", args -> {
            runOnUiThread(() -> {
                try {
                    JSONObject data = (JSONObject) args[0];
                    roomId = data.getString("roomId");
                    JSONArray questionsArray = data.getJSONArray("questions");

                    questionList = new ArrayList<>();
                    for (int i = 0; i < questionsArray.length(); i++) {
                        String jsonString = questionsArray.getJSONObject(i).toString();
                        Question q = gson.fromJson(jsonString, Question.class);
                        questionList.add(q);
                    }

                    progressBar.setVisibility(View.GONE);
                    gameGroup.setVisibility(View.VISIBLE);
                    cardScoreHeader.setVisibility(View.VISIBLE);
                    tvGameStatus.setText("¡Partida en curso!");
                    currentQuestionIndex = 0;
                    showQuestion();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });

        // Evento cuando el rival responde
        socketManager.getSocket().on("opponent_answered", args -> {
            runOnUiThread(() -> {
                try {
                    JSONObject data = (args.length > 0) ? (JSONObject) args[0] : null;
                    boolean rivalAcerto = false;

                    if (data != null) rivalAcerto = data.optBoolean("isCorrect", false);

                    if (rivalAcerto) {
                        opponentScore += 10;
                        tvOpponentScore.setText("Rival: " + opponentScore);
                        tvGameStatus.setText("Rival acertó (+10)");
                        tvGameStatus.setTextColor(Color.GREEN);
                    } else {
                        tvGameStatus.setText("Rival falló");
                        tvGameStatus.setTextColor(Color.RED);
                    }

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (isWaitingForResults) {
                            tvGameStatus.setText("Esperando a que el rival termine...");
                            tvGameStatus.setTextColor(Color.YELLOW);
                        } else if (gameGroup.getVisibility() == View.VISIBLE) {
                            tvGameStatus.setText("Jugando...");
                            tvGameStatus.setTextColor(Color.parseColor("#EEFFFFFF"));
                        }
                    }, 1500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        socketManager.getSocket().on("force_game_over", args -> {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                showGameOverScreen();
            });
        });

        socketManager.getSocket().on("waiting_opponent", args -> {
            runOnUiThread(() -> tvGameStatus.setText("Esperando a otro jugador..."));
        });
    }

    private void initViews() {
        tvScore = findViewById(R.id.tvScore);
        cardScoreHeader = findViewById(R.id.cardScoreHeader);
        tvOpponentScore = findViewById(R.id.tvOpponentScore);
        tvGameStatus = findViewById(R.id.tvGameStatus);

        tvQuestion = findViewById(R.id.tvQuestion);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);
        gameGroup = findViewById(R.id.gameGroup);

        gameOverGroup = findViewById(R.id.gameOverGroup);
        tvFinalScore = findViewById(R.id.tvFinalScore);
        tvFinalOpponentScore = findViewById(R.id.tvFinalOpponentScore);
        tvWinnerText = findViewById(R.id.tvWinnerText);
        btnRestart = findViewById(R.id.btnRestart);

        optionButtons = new ArrayList<>();
        optionButtons.add(btnOption1);
        optionButtons.add(btnOption2);
        optionButtons.add(btnOption3);
        optionButtons.add(btnOption4);
    }

    private void showQuestion() {
        isAnswered = false;
        btnNext.setVisibility(View.INVISIBLE);
        resetButtonColors();

        Question q = questionList.get(currentQuestionIndex);
        tvQuestion.setText(Html.fromHtml(q.getQuestionText(), Html.FROM_HTML_MODE_LEGACY));

        List<String> answers = new ArrayList<>(q.getIncorrectAnswers());
        answers.add(q.getCorrectAnswer());
        Collections.shuffle(answers);

        for (int i = 0; i < optionButtons.size(); i++) {
            Button btn = optionButtons.get(i);
            if (i < answers.size()) {
                btn.setVisibility(View.VISIBLE);
                String answerText = answers.get(i);
                btn.setText(Html.fromHtml(answerText, Html.FROM_HTML_MODE_LEGACY));
                btn.setOnClickListener(v -> checkAnswer(btn, answerText, q.getCorrectAnswer()));
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    private void checkAnswer(Button selectedBtn, String selectedAnswer, String correctAnswer) {
        if (isAnswered) return;
        isAnswered = true;

        String decodedCorrect = Html.fromHtml(correctAnswer, Html.FROM_HTML_MODE_LEGACY).toString();
        String decodedSelected = Html.fromHtml(selectedAnswer, Html.FROM_HTML_MODE_LEGACY).toString();

        MaterialButton matBtn = (MaterialButton) selectedBtn;
        boolean isCorrect = decodedSelected.equals(decodedCorrect);

        if (isCorrect) {
            score += 10;
            tvScore.setText("Yo: " + score);
            matBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_correct)));
            matBtn.setTextColor(Color.WHITE);
            matBtn.setStrokeWidth(0);
        } else {
            matBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_wrong)));
            matBtn.setTextColor(Color.WHITE);
            matBtn.setStrokeWidth(0);
            showCorrectButton(decodedCorrect);
        }

        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("roomId", roomId);
            jsonData.put("isCorrect", isCorrect);
            socketManager.getSocket().emit("submit_answer", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnNext.setVisibility(View.VISIBLE);
    }

    private void showCorrectButton(String correctAnswer) {
        for (Button btn : optionButtons) {
            if (btn.getText().toString().equals(correctAnswer)) {
                MaterialButton matBtn = (MaterialButton) btn;
                matBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_correct)));
                matBtn.setTextColor(Color.WHITE);
                matBtn.setStrokeWidth(0);
            }
        }
    }

    private void resetButtonColors() {
        for (Button btn : optionButtons) {
            MaterialButton matBtn = (MaterialButton) btn;
            matBtn.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            matBtn.setTextColor(Color.WHITE);
            matBtn.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            matBtn.setStrokeWidth(5);
        }
    }

    private void showGameOverScreen() {
        gameGroup.setVisibility(View.GONE);
        cardScoreHeader.setVisibility(View.GONE);
        gameOverGroup.setVisibility(View.VISIBLE);

        updateWinnerDisplay();
    }

    private void updateWinnerDisplay() {
        tvFinalScore.setText(String.valueOf(score));
        tvFinalOpponentScore.setText(String.valueOf(opponentScore));

        if (score > opponentScore) {
            tvWinnerText.setText("¡HAS GANADO!");
            tvWinnerText.setTextColor(Color.GREEN);
        } else if (score < opponentScore) {
            tvWinnerText.setText("¡HAS PERDIDO!");
            tvWinnerText.setTextColor(Color.RED);
        } else {
            tvWinnerText.setText("¡EMPATE!");
            tvWinnerText.setTextColor(Color.YELLOW);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.disconnect();
    }
}
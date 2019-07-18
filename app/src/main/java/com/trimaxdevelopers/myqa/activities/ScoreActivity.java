package com.trimaxdevelopers.myqa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.trimaxdevelopers.myqa.R;
import com.trimaxdevelopers.myqa.models.QARow;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    private static final String TAG = ScoreActivity.class.getName();

    private ArrayList<QARow> allData = new ArrayList<>();

    private TextView textScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        allData = getIntent().getParcelableArrayListExtra("allData");

        textScore = findViewById(R.id.textScore);

        // Restart the task
        findViewById(R.id.buttonRestart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Restart the task
        findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        calculateScore();
    }

    private void calculateScore() {

        int totalQuestions = allData.size();
        int skippedQuestions = 0;
        int wrongAnswers = 0;
        int correctAnswers = 0;
        int totalScore = 0;

        for (QARow qaRow : allData) {
            if (qaRow.getSelectedAnswer() == null) {
                skippedQuestions++;
            } else if (qaRow.getSelectedAnswer().equals(qaRow.getAnswer())) {
                correctAnswers++;
                totalScore += 5;
            } else {
                wrongAnswers++;
                totalScore -= 5;
            }
        }

        String text = "RESULT"
                .concat("\n\nTotal Questions : ")
                .concat(String.valueOf(totalQuestions))
                .concat("\n\nSkipped Questions : ")
                .concat(String.valueOf(skippedQuestions))
                .concat("\nWrong Answers : ")
                .concat(String.valueOf(wrongAnswers))
                .concat("\nCorrect Answers : ")
                .concat(String.valueOf(correctAnswers))
                .concat("\n\nTOTAL SCORE : ")
                .concat(String.valueOf(totalScore));

        textScore.setText(text);
    }

}

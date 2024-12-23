package com.example.beatfit;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkoutActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        TextView timerTextView = findViewById(R.id.timer_text);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        long totalTime = 30 * 1000; // 30 שניות
        long interval = 1000;

        progressBar.setMax((int) (totalTime / interval));

        countDownTimer = new CountDownTimer(totalTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time remaining: " + millisUntilFinished / 1000 + " seconds");
                progressBar.setProgress((int) ((totalTime - millisUntilFinished) / interval));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Workout complete!");
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

package com.example.beatfit;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * WorkoutActivity
 * מסך זה מציג טיימר ואנימציות לאימון בפועל.
 */
public class WorkoutActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private ProgressBar progressBar;

    private long totalTime = 30 * 1000; // זמן האימון (30 שניות לדוגמה)
    private long interval = 1000;      // עדכון כל שנייה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        timerTextView = findViewById(R.id.timer_text);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setMax((int) (totalTime / interval));

        startWorkout();
    }

    private void startWorkout() {
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

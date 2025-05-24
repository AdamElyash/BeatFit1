package com.example.beatfit;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * מסך אימון — מציג ספירת זמן לאחור לפי הדקות שהוזנו, עם כפתורי Pause ו-Finish.
 */
public class WorkoutActivity extends AppCompatActivity {
    private TextView timerTextView;
    private Button pauseButton, finishButton;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private boolean isPaused = false;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        timerTextView = findViewById(R.id.timerTextView);
        pauseButton = findViewById(R.id.pauseButton);
        finishButton = findViewById(R.id.finishButton);

        int minutes = getIntent().getIntExtra("workoutMinutes", 1); // ברירת מחדל דקה
        timeLeftInMillis = minutes * 60 * 1000;

        startTimer();

        pauseButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        });

        finishButton.setOnClickListener(v -> finishWorkout());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
                isTimerRunning = true;
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timerTextView.setText("Workout Complete!");
                pauseButton.setEnabled(false);
                finishButton.setEnabled(false);
            }
        }.start();
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        isPaused = true;
        pauseButton.setText("Resume");
    }

    private void resumeTimer() {
        startTimer();
        isPaused = false;
        pauseButton.setText("Pause");
    }

    private void finishWorkout() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        timerTextView.setText("Workout Finished!");
        pauseButton.setEnabled(false);
        finishButton.setEnabled(false);
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

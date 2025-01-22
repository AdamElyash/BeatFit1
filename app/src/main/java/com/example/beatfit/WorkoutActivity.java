package com.example.beatfit;

import android.os.Bundle;
import android.os.CountDownTimer; // ייבוא מחלקת CountDownTimer לניהול טיימר
import android.widget.ProgressBar; // ייבוא מחלקת ProgressBar להצגת התקדמות האימון
import android.widget.TextView; // ייבוא מחלקת TextView להצגת טקסט על המסך

import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת AppCompatActivity לניהול מסכי האפליקציה

public class WorkoutActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer; // משתנה לנהל את הטיימר של האימון

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout); // הגדרת ממשק המשתמש למסך האימון

        // אתחול TextView להצגת הזמן הנותר
        TextView timerTextView = findViewById(R.id.timer_text);

        // אתחול ProgressBar להצגת ההתקדמות של האימון
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // קביעת הזמן הכולל של האימון (30 שניות)
        long totalTime = 30 * 1000; // 30 שניות בסך הכול, מומרות למילישניות

        // קביעת מרווח העדכון (כל שנייה)
        long interval = 1000; // עדכון כל 1 שניות

        // הגדרת הערך המקסימלי של ה-ProgressBar לפי הזמן הכולל חלקי המרווח
        progressBar.setMax((int) (totalTime / interval));

        // יצירת אובייקט של CountDownTimer לניהול הזמן של האימון
        countDownTimer = new CountDownTimer(totalTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // פעולה שמתבצעת בכל "טיק" של הטיימר
                // הצגת הזמן הנותר ב-TextView
                timerTextView.setText("Time remaining: " + millisUntilFinished / 1000 + " seconds");

                // עדכון ה-ProgressBar לפי הזמן שחלף
                progressBar.setProgress((int) ((totalTime - millisUntilFinished) / interval));
            }

            @Override
            public void onFinish() {
                // פעולה שמתבצעת כשהטיימר מסתיים
                timerTextView.setText("Workout complete!"); // הצגת הודעה שהאימון הסתיים
            }
        };

        // הפעלת הטיימר
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // בדיקה אם הטיימר פעיל, ובמקרה כזה עצירתו כדי למנוע זליגת זיכרון
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

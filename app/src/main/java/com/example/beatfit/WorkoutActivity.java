package com.example.beatfit;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * אקטיביטי זה אחראי על ניהול האימון בפועל.
 * מוצג טיימר שמתאר את הזמן שנותר לאימון, עם עדכון של ProgressBar.
 */
public class WorkoutActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer; // משתנה לניהול הטיימר של האימון
    private long totalTime; // משך הזמן הכולל של האימון (במילישניות)
    private long interval = 1000; // מרווח העדכון של הטיימר (במילישניות)

    @Override
    protected void onCreate(Bundle savedInstanceState) { // פונקציה שמופעלת בעת יצירת המסך
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout); // הגדרת ממשק המשתמש מהקובץ XML

        // אתחול TextView להצגת הזמן שנותר
        TextView timerTextView = findViewById(R.id.timer_text);

        // אתחול ProgressBar להצגת התקדמות האימון
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // קבלת הנתונים שהועברו מהמסך הקודם (משך האימון)
        String durationStr = getIntent().getStringExtra("duration");

        if (durationStr != null && !durationStr.isEmpty()) {
            totalTime = Long.parseLong(durationStr) * 1000; // המרה לשניות * 1000 למילישניות
        } else {
            totalTime = 30 * 1000; // ברירת מחדל: 30 שניות אם אין נתון
        }

        // הגדרת הערך המקסימלי של ה-ProgressBar לפי משך האימון
        progressBar.setMax((int) (totalTime / interval));

        // יצירת אובייקט של CountDownTimer לניהול הזמן של האימון
        countDownTimer = new CountDownTimer(totalTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                // עדכון הזמן הנותר בתצוגה
                timerTextView.setText("זמן שנותר: " + millisUntilFinished / 1000 + " שניות");

                // עדכון ה-ProgressBar לפי הזמן שחלף
                progressBar.setProgress((int) ((totalTime - millisUntilFinished) / interval));
            }

            @Override
            public void onFinish() {
                // כאשר הטיימר מסתיים, הצגת הודעה שהאימון הסתיים
                timerTextView.setText("האימון הסתיים!");
            }
        };

        // הפעלת הטיימר
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // אם הטיימר פועל, עצירה שלו למניעת זליגת זיכרון
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

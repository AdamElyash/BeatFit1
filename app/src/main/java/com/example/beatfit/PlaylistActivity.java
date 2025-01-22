package com.example.beatfit;

import android.content.Intent; // ייבוא מחלקת Intent לשימוש במעבר בין מסכים
import android.os.Bundle; // ייבוא מחלקת Bundle לניהול מידע שמועבר בין מסכים
import android.widget.Button; // ייבוא מחלקת Button לטיפול בכפתורים
import android.widget.TextView; // ייבוא מחלקת TextView להצגת טקסט על המסך

import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת AppCompatActivity לניהול מסכי האפליקציה

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist); // הגדרת ממשק המשתמש למסך רשימת ההשמעה

        // אתחול TextView להצגת הודעת רשימת ההשמעה
        TextView playlistMessage = findViewById(R.id.playlist_message);

        // אתחול כפתור מעבר למסך הבא
        Button nextButton = findViewById(R.id.next_button);

        // קבלת נתונים שהועברו ממסך קודם
        String music = getIntent().getStringExtra("music"); // סוג המוזיקה האהוב
        String sport = getIntent().getStringExtra("sport"); // סוג הספורט
        String username = getIntent().getStringExtra("USERNAME"); // שם המשתמש

        // הצגת הודעה מותאמת אישית למשתמש על בסיס ההעדפות שנבחרו
        playlistMessage.setText("We've created a playlist based on your preferences: " + music + " and " + sport);

        // הגדרת פעולה בלחיצה על כפתור Next
        nextButton.setOnClickListener(v -> {
            // יצירת Intent למעבר למסך הבא - מסך הגדרת אימון
            Intent intent = new Intent(PlaylistActivity.this, WorkoutSetupActivity.class);
            startActivity(intent); // הפעלת המסך הבא
        });
    }
}

package com.example.beatfit;

import android.content.Intent; // ייבוא מחלקת Intent לטיפול במעבר בין מסכים
import android.os.Bundle; // ייבוא מחלקת Bundle לניהול מצב נתונים בין מסכים
import android.widget.Button; // ייבוא מחלקת Button לניהול כפתורים
import android.widget.EditText; // ייבוא מחלקת EditText לטיפול בקלט טקסט של המשתמש
import android.widget.TextView; // ייבוא מחלקת TextView להצגת טקסט על המסך
import android.widget.Toast; // ייבוא מחלקת Toast להצגת הודעות קופצות למשתמש

import androidx.annotation.NonNull; // ייבוא מחלקה לסימון הערות שאינן יכולות להיות null
import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת AppCompatActivity לניהול מסך באפליקציה

import com.google.firebase.auth.FirebaseAuth; // ייבוא FirebaseAuth לניהול משתמשים
import com.google.firebase.database.DatabaseReference; // ייבוא DatabaseReference לניהול קישור למסד הנתונים
import com.google.firebase.database.FirebaseDatabase; // ייבוא FirebaseDatabase ליצירת חיבור למסד הנתונים

public class MusicSportActivity extends AppCompatActivity {

    // משתנים עבור קלט המשתמש ומסד הנתונים
    private EditText musicEditText, sportEditText; // שדות קלט עבור מוזיקה וספורט
    private DatabaseReference userDatabaseReference; // הפניה למסד הנתונים של Firebase
    private FirebaseAuth auth; // משתנה עבור FirebaseAuth לניהול אימות משתמשים

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_sport); // הגדרת ממשק המשתמש של המסך

        // אתחול רכיבי ממשק המשתמש
        TextView welcomeTextView = findViewById(R.id.welcome_text_view); // תיבת טקסט להצגת הודעת קבלת פנים
        musicEditText = findViewById(R.id.music_edit_text); // שדה קלט עבור סוג המוזיקה
        sportEditText = findViewById(R.id.sport_edit_text); // שדה קלט עבור סוג הספורט
        Button nextButton = findViewById(R.id.next_button); // כפתור למעבר למסך הבא

        // אתחול FirebaseAuth וקישור למסד הנתונים של Firebase
        auth = FirebaseAuth.getInstance(); // קבלת מופע של FirebaseAuth לניהול משתמשים
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users"); // קישור למסד הנתונים תחת ענף "Users"

        // קבלת שם המשתמש שהועבר מ-Intent במסך הקודם
        String username = getIntent().getStringExtra("USERNAME"); // שם המשתמש
        // הצגת הודעת קבלת פנים מותאמת אישית למשתמש
        welcomeTextView.setText("Hello, " + username + "! Select your favorite music and sports types.");

        // טיפול בלחיצה על כפתור Next
        nextButton.setOnClickListener(v -> {
            // קבלת נתוני המוזיקה והספורט שהוזנו על ידי המשתמש
            String music = musicEditText.getText().toString().trim(); // קלט סוג המוזיקה
            String sport = sportEditText.getText().toString().trim(); // קלט סוג הספורט

            // בדיקה אם שדה המוזיקה ריק
            if (music.isEmpty()) {
                musicEditText.setError("Please enter your favorite music genre"); // הצגת הודעת שגיאה
                musicEditText.requestFocus(); // הצבת הסמן בשדה הקלט
                return; // יציאה מוקדמת מהפעולה
            }

            // בדיקה אם שדה הספורט ריק
            if (sport.isEmpty()) {
                sportEditText.setError("Please enter your favorite sport type"); // הצגת הודעת שגיאה
                sportEditText.requestFocus(); // הצבת הסמן בשדה הקלט
                return; // יציאה מוקדמת מהפעולה
            }

            // שמירת הנתונים במסד הנתונים של Firebase תחת שם המשתמש הנוכחי
            String userId = auth.getCurrentUser().getUid(); // קבלת מזהה המשתמש הנוכחי
            userDatabaseReference.child(userId).child("favoriteMusic").setValue(music); // שמירת סוג המוזיקה
            userDatabaseReference.child(userId).child("favoriteSport").setValue(sport) // שמירת סוג הספורט
                    .addOnCompleteListener(task -> { // האזנה לתוצאת הפעולה
                        if (task.isSuccessful()) {
                            // אם הפעולה הצליחה - מעבר למסך הבא
                            Intent intent = new Intent(MusicSportActivity.this, PlaylistActivity.class); // יצירת Intent למעבר למסך רשימת ההשמעה
                            intent.putExtra("music", music); // העברת סוג המוזיקה
                            intent.putExtra("sport", sport); // העברת סוג הספורט
                            intent.putExtra("USERNAME", username); // העברת שם המשתמש
                            startActivity(intent); // הפעלת המסך הבא
                        } else {
                            // אם הפעולה נכשלה - הצגת הודעת שגיאה למשתמש
                            Toast.makeText(MusicSportActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}

package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MusicSportActivity extends AppCompatActivity {
    private EditText musicEditText, sportEditText; // שדות לקליטת העדפות המוזיקה והספורט של המשתמש
    private DatabaseReference userDatabaseReference; // משתנה להפניה למסד הנתונים של Firebase
    private FirebaseAuth auth; // משתנה לאימות המשתמש המחובר

    @Override
    protected void onCreate(Bundle savedInstanceState) { // פונקציה המופעלת בעת יצירת האקטיביטי
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_sport); // קביעת ממשק המשתמש מהקובץ XML

        TextView welcomeTextView = findViewById(R.id.welcome_text_view); // רכיב טקסט להצגת הודעת ברוכים הבאים
        musicEditText = findViewById(R.id.music_edit_text); // רכיב להזנת סגנון המוזיקה האהוב
        sportEditText = findViewById(R.id.sport_edit_text); // רכיב להזנת סוג הספורט המועדף
        Button nextButton = findViewById(R.id.next_button); // כפתור להמשך למסך הבא

        auth = FirebaseAuth.getInstance(); // אתחול Firebase Authentication
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users"); // קבלת הפניה למסד הנתונים בענף "Users"

        String userId = auth.getCurrentUser().getUid(); // קבלת מזהה ה-UID של המשתמש המחובר

        // שליפת הנתונים מהמסד של Firebase כדי לבדוק אם יש למשתמש העדפות מוזיקה וספורט שמורות
        userDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // כאשר הנתונים נטענים
                User user = dataSnapshot.getValue(User.class); // המרת הנתונים שהתקבלו לאובייקט מסוג User
                if (user != null) { // אם הנתונים קיימים
                    if (user.favoriteMusic != null) { // אם קיימת העדפת מוזיקה
                        musicEditText.setText(user.favoriteMusic); // הצגת ההעדפה בשדה
                    }
                    if (user.favoriteSport != null) { // אם קיימת העדפת ספורט
                        sportEditText.setText(user.favoriteSport); // הצגת ההעדפה בשדה
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { // במקרה של שגיאה בשליפת הנתונים
                Toast.makeText(MusicSportActivity.this, "טעינת הנתונים נכשלה", Toast.LENGTH_SHORT).show(); // הצגת הודעה למשתמש
            }
        });

        // כאשר המשתמש לוחץ על כפתור "הבא"
        nextButton.setOnClickListener(v -> {
            String music = musicEditText.getText().toString().trim(); // קריאת הנתון שהמשתמש הכניס למוזיקה
            String sport = sportEditText.getText().toString().trim(); // קריאת הנתון שהמשתמש הכניס לספורט

            if (music.isEmpty()) { // בדיקה אם המשתמש לא הכניס סגנון מוזיקה
                musicEditText.setError("נא להזין סגנון מוזיקה אהוב"); // הצגת שגיאה
                musicEditText.requestFocus(); // מיקוד בשדה כדי שהמשתמש יוכל לתקן
                return; // יציאה מהפעולה (לא להמשיך)
            }

            if (sport.isEmpty()) { // בדיקה אם המשתמש לא הכניס סוג ספורט
                sportEditText.setError("נא להזין סוג ספורט מועדף"); // הצגת שגיאה
                sportEditText.requestFocus(); // מיקוד בשדה כדי שהמשתמש יוכל לתקן
                return; // יציאה מהפעולה (לא להמשיך)
            }

            // שמירת הנתונים של המשתמש בפיירבייס
            userDatabaseReference.child(userId).child("favoriteMusic").setValue(music); // שמירת סגנון המוזיקה במסד הנתונים
            userDatabaseReference.child(userId).child("favoriteSport").setValue(sport); // שמירת סוג הספורט במסד הנתונים

            // שמירת הנתונים ב-SharedPreferences (אחסון מקומי במכשיר) כדי להשתמש בהם במסכים הבאים
            getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("FAVORITE_MUSIC", music) // שמירת העדפת המוזיקה
                    .putString("FAVORITE_SPORT", sport) // שמירת העדפת הספורט
                    .apply(); // החלת השינויים

            // מעבר למסך הבא - התחברות לספוטיפיי
            Intent intent = new Intent(MusicSportActivity.this, SpotifyLoginActivity.class); // יצירת Intent למסך ההתחברות לספוטיפיי
            intent.putExtra("music", music); // העברת סגנון המוזיקה לאקטיביטי הבא
            intent.putExtra("sport", sport); // העברת סוג הספורט לאקטיביטי הבא
            startActivity(intent); // פתיחת האקטיביטי של החיבור לספוטיפיי
        });
    }
}

package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // ייבוא Glide לניהול טעינת תמונות
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    // משתנה עבור Firebase Authentication
    private FirebaseAuth auth;

    // משתנה עבור Google Sign-In
    private GoogleSignInClient googleSignInClient;

    // Launcher לטיפול בתוצאות Google Sign-In
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), // חוזה פעילות לקבלת תוצאה
            new ActivityResultCallback<ActivityResult>() { // Callback שמטפל בתוצאה של הפעולה
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) { // אם הפעולה הסתיימה בהצלחה
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData()); // קבלת החשבון מ-Google
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class); // קבלת פרטי החשבון
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null); // יצירת אישור כניסה מ-Google

                            // התחברות ל-Firebase עם אישור Google
                            auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) { // אם ההתחברות הצליחה
                                    saveUserDataToDatabase(); // שמירת נתוני המשתמש במסד הנתונים

                                    // מעבר למסך MusicSportActivity
                                    Intent intent = new Intent(LoginActivity.this, MusicSportActivity.class);
                                    intent.putExtra("USERNAME", auth.getCurrentUser().getDisplayName()); // העברת שם המשתמש
                                    startActivity(intent); // הפעלת המסך הבא
                                } else { // אם ההתחברות נכשלה
                                    Toast.makeText(LoginActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show(); // הצגת הודעת שגיאה
                                }
                            });
                        } catch (ApiException e) { // טיפול בשגיאות בעת קבלת פרטי החשבון
                            e.printStackTrace(); // הדפסת השגיאה ליומן הבאגים
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // קביעת תצוגת המסך כ-layout של activity_login

        FirebaseApp.initializeApp(this); // אתחול Firebase

        // הגדרת אפשרויות Google Sign-In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // בקשה לקבלת מזהה ה-token
                .requestEmail() // בקשה לקבלת כתובת האימייל
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, options); // יצירת GoogleSignInClient עם האפשרויות שהוגדרו
        auth = FirebaseAuth.getInstance(); // אתחול FirebaseAuth לניהול משתמשים

        // טיפול בלחיצה על כפתור Google Sign-In
        SignInButton signInButton = findViewById(R.id.signIn); // קישור הכפתור לממשק המשתמש
        signInButton.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent(); // יצירת Intent לפתיחת Google Sign-In
            activityResultLauncher.launch(intent); // הפעלת הפעולה לקבלת התוצאה
        });
    }

    /**
     * שמירת נתוני המשתמש הבסיסיים במסד הנתונים של Firebase.
     */
    private void saveUserDataToDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(); // קבלת מופע של מסד הנתונים
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid(); // קבלת מזהה המשתמש הנוכחי

        // יצירת אובייקט משתמש בסיסי
        User user = new User(
                userId, // מזהה המשתמש
                auth.getCurrentUser().getDisplayName(), // שם המשתמש
                auth.getCurrentUser().getEmail(), // כתובת האימייל של המשתמש
                auth.getCurrentUser().getPhotoUrl() != null ? auth.getCurrentUser().getPhotoUrl().toString() : "" // כתובת התמונה או מחרוזת ריקה
        );

        // שמירת הנתונים במסד הנתונים תחת ענף "Users"
        database.getReference("Users").child(userId).setValue(user)
                .addOnCompleteListener(task -> { // האזנה לתוצאת השמירה
                    if (task.isSuccessful()) { // אם השמירה הצליחה
                        Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show(); // הצגת הודעת הצלחה
                    } else { // אם השמירה נכשלה
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show(); // הצגת הודעת שגיאה
                    }
                });
    }
}

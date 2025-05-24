package com.example.beatfit;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
    private FirebaseAuth auth; // משתנה לטיפול באימות המשתמש בפיירבייס
    private GoogleSignInClient googleSignInClient; // משתנה לניהול התחברות עם גוגל

    // מפעיל אקטיביטי שמחזיר תוצאה (Google Sign-In)
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), // מציין שהולכים להפעיל אקטיביטי עם תוצאה
            new ActivityResultCallback<ActivityResult>() { // פונקציה שתרוץ כשמתקבלת תוצאה מהאקטיביטי שהופעל
                @Override
                public void onActivityResult(ActivityResult result) { // פונקציה שמופעלת לאחר קבלת תוצאה
                    if (result.getResultCode() == RESULT_OK) {  // אם ההתחברות הצליחה
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData()); // ניסיון לקבל את החשבון שנכנס
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class); // קבלת חשבון המשתמש
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null); // יצירת אישור התחברות לפיירבייס

                            auth.signInWithCredential(authCredential).addOnCompleteListener(task -> { // ניסיון להתחבר עם האישור
                                if (task.isSuccessful()) { // אם ההתחברות הצליחה
                                    saveUserDataToDatabase(); // שמירת הנתונים במסד הנתונים
                                    Intent intent = new Intent(LoginActivity.this, MusicSportActivity.class); // הכנת מעבר למסך בחירת העדפות
                                    intent.putExtra("USERNAME", auth.getCurrentUser().getDisplayName()); // מעביר את שם המשתמש לאקטיביטי הבא
                                    startActivity(intent); // מעבר לאקטיביטי הבא
                                } else { // אם ההתחברות נכשלה
                                    Toast.makeText(LoginActivity.this, "ההתחברות נכשלה: " + task.getException(), Toast.LENGTH_SHORT).show(); // הצגת שגיאה למשתמש
                                }
                            });
                        } catch (ApiException e) { // טיפול בשגיאות במקרה של כשל
                            e.printStackTrace(); // הדפסת השגיאה בקונסול לצורכי ניתוח
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) { // פונקציה שנקראת כאשר האקטיביטי נטען
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // קביעת ממשק המשתמש מתוך XML

        FirebaseApp.initializeApp(this); // אתחול Firebase

        // הגדרת אפשרויות Google Sign-In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // בקשת מזהה משתמש ייחודי
                .requestEmail() // בקשת כתובת האימייל של המשתמש
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, options); // יצירת אובייקט להתחברות לגוגל
        auth = FirebaseAuth.getInstance(); // אתחול Firebase Authentication

        // אתחול כפתור ההתחברות של גוגל ולחיצה עליו
        SignInButton signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(view -> { // בעת לחיצה על הכפתור
            Intent intent = googleSignInClient.getSignInIntent(); // יצירת Intent לפתיחת מסך ההתחברות של גוגל
            activityResultLauncher.launch(intent); // הפעלת ה-Intent עם ציפייה לתוצאה
        });
    }

    /**
     * פונקציה לשמירת נתוני המשתמש במסד הנתונים Firebase Realtime Database.
     */
    private void saveUserDataToDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(); // יצירת חיבור למסד הנתונים
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid(); // קבלת ה-UID של המשתמש המחובר

        // יצירת אובייקט משתמש עם הנתונים הבסיסיים (ללא העדפות)
        User user = new User(
                userId,
                auth.getCurrentUser().getDisplayName(), // שם המשתמש
                auth.getCurrentUser().getEmail(), // כתובת המייל
                auth.getCurrentUser().getPhotoUrl() != null ? auth.getCurrentUser().getPhotoUrl().toString() : "" // תמונת פרופיל (אם קיימת)
        );

        // שמירת הנתונים במסד הנתונים תחת ענף "Users"
        database.getReference("Users").child(userId).setValue(user)
                .addOnCompleteListener(task -> { // מאזין לסיום הפעולה
                    if (task.isSuccessful())
                    { // אם השמירה הצליחה
                        Toast.makeText(this, "נתוני המשתמש נשמרו בהצלחה", Toast.LENGTH_SHORT).show(); // הודעה למשתמש
                    } else
                    { // אם השמירה נכשלה
                        Toast.makeText(this, "שמירת הנתונים נכשלה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // הצגת שגיאה
                    }
                });
    }
}

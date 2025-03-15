package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                            // התחברות ל-Firebase עם אישור Google
                            auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    saveUserDataToDatabase();

                                    // מעבר למסך MusicSportActivity
                                    Intent intent = new Intent(LoginActivity.this, MusicSportActivity.class);
                                    intent.putExtra("USERNAME", auth.getCurrentUser().getDisplayName());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        // הגדרת אפשרויות Google Sign-In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, options);
        auth = FirebaseAuth.getInstance();

        // טיפול בלחיצה על כפתור Google Sign-In
        SignInButton signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });
    }

    /**
     * שמירת נתוני המשתמש הבסיסיים במסד הנתונים של Firebase.
     */
    private void saveUserDataToDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // יצירת אובייקט משתמש בסיסי (ללא העדפות מוזיקה וספורט בשלב זה)
        User user = new User(
                userId,
                auth.getCurrentUser().getDisplayName(),
                auth.getCurrentUser().getEmail(),
                auth.getCurrentUser().getPhotoUrl() != null ? auth.getCurrentUser().getPhotoUrl().toString() : ""
        );

        // שמירת הנתונים במסד הנתונים תחת ענף "Users"
        database.getReference("Users").child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
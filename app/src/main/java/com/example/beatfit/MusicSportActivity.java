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

    private EditText musicEditText, sportEditText;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_sport);

        TextView welcomeTextView = findViewById(R.id.welcome_text_view);
        musicEditText = findViewById(R.id.music_edit_text);
        sportEditText = findViewById(R.id.sport_edit_text);
        Button nextButton = findViewById(R.id.next_button);

        auth = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        String userId = auth.getCurrentUser().getUid();

        // קריאת הנתונים מפיירבייס והצגתם בשדות הקלט
        userDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.favoriteMusic != null) {
                        musicEditText.setText(user.favoriteMusic);
                    }
                    if (user.favoriteSport != null) {
                        sportEditText.setText(user.favoriteSport);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MusicSportActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(v -> {
            String music = musicEditText.getText().toString().trim();
            String sport = sportEditText.getText().toString().trim();

            if (music.isEmpty()) {
                musicEditText.setError("Please enter your favorite music genre");
                musicEditText.requestFocus();
                return;
            }

            if (sport.isEmpty()) {
                sportEditText.setError("Please enter your favorite sport type");
                sportEditText.requestFocus();
                return;
            }

            // שמירת הנתונים בפיירבייס
            userDatabaseReference.child(userId).child("favoriteMusic").setValue(music);
            userDatabaseReference.child(userId).child("favoriteSport").setValue(sport);

            // מעבר למסך החיבור לספוטיפיי
            Intent intent = new Intent(MusicSportActivity.this, SpotifyLoginActivity.class);
            startActivity(intent);
        });
    }
}

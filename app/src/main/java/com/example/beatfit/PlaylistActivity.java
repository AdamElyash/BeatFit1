package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * PlaylistActivity
 * מסך זה מציג הודעה מותאמת אישית עם פלייליסט המבוסס על ההעדפות של המשתמש.
 */
public class PlaylistActivity extends AppCompatActivity {

    private TextView playlistMessage;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistMessage = findViewById(R.id.playlist_message);
        nextButton = findViewById(R.id.next_button);

        // קבלת הנתונים מהמסך הקודם
        String music = getIntent().getStringExtra("music");
        String sport = getIntent().getStringExtra("sport");
        String username = getIntent().getStringExtra("USERNAME");

        playlistMessage.setText("Hello " + username + ", we've created a playlist based on your preferences: " + music + " and " + sport);

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlaylistActivity.this, WorkoutSetupActivity.class);
            startActivity(intent);
        });
    }
}

package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        TextView playlistMessage = findViewById(R.id.playlist_message);
        Button nextButton = findViewById(R.id.next_button);

        String music = getIntent().getStringExtra("music");
        String sport = getIntent().getStringExtra("sport");
        String username = getIntent().getStringExtra("USERNAME");

        playlistMessage.setText("we've created a playlist based on your preferences: " + music + " and " + sport);

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlaylistActivity.this, WorkoutSetupActivity.class);
            startActivity(intent);
        });
    }
}

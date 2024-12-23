package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MusicSportActivity extends AppCompatActivity {

    private EditText musicEditText, sportEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_sport);

        TextView welcomeTextView = findViewById(R.id.welcome_text_view);
        musicEditText = findViewById(R.id.music_edit_text);
        sportEditText = findViewById(R.id.sport_edit_text);
        Button nextButton = findViewById(R.id.next_button);

        String username = getIntent().getStringExtra("USERNAME");
        welcomeTextView.setText("Hello, " + username + "! Select your favorite music and sports types.");

        nextButton.setOnClickListener(v -> {
            String music = musicEditText.getText().toString();
            String sport = sportEditText.getText().toString();

            Intent intent = new Intent(MusicSportActivity.this, PlaylistActivity.class);
            intent.putExtra("music", music);
            intent.putExtra("sport", sport);
            intent.putExtra("USERNAME", username);
            startActivity(intent);

        });
    }
}

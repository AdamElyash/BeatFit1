package com.example.beatfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SpotifyLoginActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "84cd06acfbd24f988275b174fdb68cb8";
    private static final String REDIRECT_URI = "connectspotify://callback";
    private static final String AUTH_URL = "https://accounts.spotify.com/authorize" +
            "?client_id=" + CLIENT_ID +
            "&response_type=token" +
            "&redirect_uri=" + REDIRECT_URI +
            "&scope=playlist-read-private";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);

        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL));
            startActivity(browserIntent);
        });
    }
}

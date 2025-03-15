package com.example.beatfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SpotifyRedirectActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyRedirectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Redirecting from Spotify...");

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith("connectspotify://callback")) {
            String fragment = uri.getFragment();
            if (fragment != null && fragment.contains("access_token=")) {
                String accessToken = fragment.split("access_token=")[1].split("&")[0];

                Log.d(TAG, "✅ Spotify Access Token Received: " + accessToken);

                // שמירת ה-Access Token
                SharedPreferences.Editor editor = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE).edit();
                editor.putString("ACCESS_TOKEN", accessToken);
                editor.apply();

                // טעינת המוזיקה והספורט ששמרנו קודם
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String music = prefs.getString("FAVORITE_MUSIC", "");
                String sport = prefs.getString("FAVORITE_SPORT", "");

                // אם המידע חסר → אל תמשיך
                if (music.isEmpty() || sport.isEmpty()) {
                    Log.e(TAG, "❌ Missing music or sport preference!");
                    Toast.makeText(this, "Missing preferences. Try again.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                Log.d(TAG, "Passing to PlaylistActivity - Music: " + music + ", Sport: " + sport);

                // מעבר למסך הפלייליסטים
                Intent intent = new Intent(this, PlaylistActivity.class);
                intent.putExtra("ACCESS_TOKEN", accessToken);
                intent.putExtra("music", music);
                intent.putExtra("sport", sport);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "❌ Failed to authenticate with Spotify", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error parsing Spotify response");
                finish();
            }
        } else {
            Log.e(TAG, "❌ No valid redirect URI detected");
            finish();
        }
    }
}

package com.example.beatfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

                Log.d(TAG, "Spotify Access Token Received: " + accessToken);

                // 🔹 שמירת ה-Access Token בפיירבייס
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
                userDatabaseReference.child(userId).child("spotifyAccessToken").setValue(accessToken);

                // מעבר למסך הפלייליסטים
                Intent intent = new Intent(this, PlaylistActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to authenticate with Spotify", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error parsing Spotify response");
                finish();
            }
        } else {
            Log.e(TAG, "No valid redirect URI detected");
            finish();
        }
    }
}

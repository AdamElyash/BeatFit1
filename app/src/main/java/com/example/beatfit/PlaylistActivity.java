package com.example.beatfit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * פעילות המציגה פלייליסטים מותאמים לפי העדפות המשתמש:
 * סוג מוזיקה + סוג ספורט, שנמשכים מ-Spotify API.
 */
public class PlaylistActivity extends AppCompatActivity {

    private String accessToken;
    private String music;
    private String sport;
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private List<PlaylistItem> playlists;
    private ProgressBar progressBar;
    private TextView errorText;
    private Button startWorkoutButton; // ← כפתור חדש

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);
        startWorkoutButton = findViewById(R.id.startWorkoutButton); // ← קישור לכפתור

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlists = new ArrayList<>();
        adapter = new PlaylistAdapter(this, playlists);
        recyclerView.setAdapter(adapter);

        // מאזין ללחיצה על כפתור
        startWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlaylistActivity.this, WorkoutSetupActivity.class);
            startActivity(intent);
        });

        // קבלת נתוני המשתמש מפיירבייס
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userDatabaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot dataSnapshot = task.getResult();
                accessToken = dataSnapshot.child("spotifyAccessToken").getValue(String.class);
                music = dataSnapshot.child("favoriteMusic").getValue(String.class);
                sport = dataSnapshot.child("favoriteSport").getValue(String.class);

                if (accessToken == null || accessToken.isEmpty()) {
                    showError("Missing Spotify access token.");
                    return;
                }

                if (music == null || music.isEmpty() || sport == null || sport.isEmpty()) {
                    showError("Missing music or sport preferences.");
                    return;
                }

                new FetchPlaylistsTask().execute(accessToken, music, sport);
            } else {
                showError("Failed to load user data.");
            }
        });
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private class FetchPlaylistsTask extends AsyncTask<String, Void, List<PlaylistItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        }

        @Override
        protected List<PlaylistItem> doInBackground(String... params) {
            String token = params[0];
            String musicGenre = params[1];
            String sportType = params[2];

            List<PlaylistItem> allPlaylists = new ArrayList<>();
            String[] searchQueries = createSearchQueries(musicGenre, sportType);

            for (String query : searchQueries) {
                List<PlaylistItem> queryResults = searchPlaylists(token, query, 10);
                allPlaylists.addAll(queryResults);

                if (allPlaylists.size() >= 20) break;
            }

            return filterAndRemoveDuplicates(allPlaylists, musicGenre, sportType);
        }

        private String[] createSearchQueries(String music, String sport) {
            String musicLower = music.toLowerCase();
            String sportLower = sport.toLowerCase();

            return new String[]{
                    musicLower + " " + sportLower,
                    musicLower + " workout",
                    musicLower + " gym",
                    musicLower + " training",
                    musicLower + " exercise",
                    sportLower + " music",
                    sportLower + " playlist",
                    sportLower + " workout",
                    "workout " + musicLower,
                    "gym " + musicLower,
                    musicLower + " motivation",
                    musicLower + " high energy"
            };
        }

        private List<PlaylistItem> searchPlaylists(String token, String query, int limit) {
            List<PlaylistItem> playlists = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                String urlStr = "https://api.spotify.com/v1/search?q=" + encodedQuery +
                        "&type=playlist&limit=" + limit + "&market=US";
                URL url = new URL(urlStr);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject playlistsObject = jsonResponse.getJSONObject("playlists");
                    JSONArray playlistsArray = playlistsObject.getJSONArray("items");

                    for (int i = 0; i < playlistsArray.length(); i++) {
                        JSONObject playlist = playlistsArray.getJSONObject(i);
                        String playlistName = playlist.getString("name");
                        String playlistUrl = playlist.getJSONObject("external_urls").getString("spotify");
                        String description = playlist.optString("description", "");

                        playlists.add(new PlaylistItem(playlistName, playlistUrl, description));
                    }
                }
            } catch (Exception e) {
                // התעלמות משגיאות בזמן ריצה כדי לא להפיל את האפליקציה
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    // התעלמות מסגירת משאבים
                }
            }
            return playlists;
        }

        private List<PlaylistItem> filterAndRemoveDuplicates(List<PlaylistItem> playlists, String music, String sport) {
            List<PlaylistItem> filteredPlaylists = new ArrayList<>();
            List<String> seenNames = new ArrayList<>();

            String musicLower = music.toLowerCase();
            String sportLower = sport.toLowerCase();

            String[] workoutKeywords = {"workout", "gym", "training", "exercise", "fitness",
                    "run", "cardio", "motivation", "energy", "power",
                    "beast", "strong", "pump", "intense", "focus"};

            for (PlaylistItem playlist : playlists) {
                String name = playlist.getName().toLowerCase();
                String description = playlist.getDescription().toLowerCase();
                String combined = name + " " + description;

                if (seenNames.contains(name)) continue;

                if (isRelevantPlaylist(combined, musicLower, sportLower, workoutKeywords)) {
                    filteredPlaylists.add(playlist);
                    seenNames.add(name);

                    if (filteredPlaylists.size() >= 15) break;
                }
            }

            return filteredPlaylists;
        }

        private boolean isRelevantPlaylist(String combined, String music, String sport, String[] workoutKeywords) {
            boolean hasMusic = combined.contains(music) || isMusicGenreMatch(combined, music);
            boolean hasSport = combined.contains(sport) || hasWorkoutKeywords(combined, workoutKeywords);

            boolean isNotRelevant = combined.contains("sleep") ||
                    combined.contains("relax") ||
                    combined.contains("chill") ||
                    combined.contains("meditation") ||
                    combined.contains("study");

            return (hasMusic || hasSport) && !isNotRelevant;
        }

        private boolean isMusicGenreMatch(String combined, String music) {
            switch (music) {
                case "rap":
                case "hip hop":
                    return combined.contains("hip hop") || combined.contains("hiphop") ||
                            combined.contains("rap") || combined.contains("urban");
                case "rock":
                    return combined.contains("rock") || combined.contains("metal") ||
                            combined.contains("alternative");
                case "pop":
                    return combined.contains("pop") || combined.contains("top 40") ||
                            combined.contains("hits");
                case "electronic":
                case "edm":
                    return combined.contains("electronic") || combined.contains("edm") ||
                            combined.contains("house") || combined.contains("techno") ||
                            combined.contains("dance");
                default:
                    return false;
            }
        }

        private boolean hasWorkoutKeywords(String combined, String[] keywords) {
            for (String keyword : keywords) {
                if (combined.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(List<PlaylistItem> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            if (result.isEmpty()) {
                errorText.setText("No relevant playlists found for " + music + " and " + sport +
                        ". Try different preferences.");
                errorText.setVisibility(View.VISIBLE);
            } else {
                playlists.clear();
                playlists.addAll(result);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                startWorkoutButton.setVisibility(View.VISIBLE); // ← מציג את הכפתור אחרי הטעינה
            }
        }
    }
}

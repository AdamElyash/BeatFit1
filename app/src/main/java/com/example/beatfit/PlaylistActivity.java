package com.example.beatfit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private static final String TAG = "PlaylistActivity";

    private String accessToken;
    private String music;
    private String sport;
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private List<PlaylistItem> playlists;
    private ProgressBar progressBar;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlists = new ArrayList<>();
        adapter = new PlaylistAdapter(this, playlists);
        recyclerView.setAdapter(adapter);

        accessToken = getIntent().getStringExtra("ACCESS_TOKEN");
        music = getIntent().getStringExtra("music");
        sport = getIntent().getStringExtra("sport");

        if (accessToken == null || accessToken.isEmpty()) {
            Toast.makeText(this, "Error: Missing access token.", Toast.LENGTH_LONG).show();
            errorText.setText("Error: Missing access token.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (music == null || music.isEmpty() || sport == null || sport.isEmpty()) {
            Toast.makeText(this, "Error: Missing music or sport preference.", Toast.LENGTH_LONG).show();
            errorText.setText("Error: Missing music or sport preference.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        Log.d(TAG, "Fetching playlists for: " + music + " and " + sport);
        new FetchPlaylistsTask().execute(accessToken, music + " " + sport);
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
            String query = params[1];
            List<PlaylistItem> playlists = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                String urlStr = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=playlist&limit=10";
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, " Response Code: " + responseCode);

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

                    Log.d(TAG, " Found " + playlistsArray.length() + " playlists.");

                    for (int i = 0; i < playlistsArray.length(); i++) {
                        JSONObject playlist = playlistsArray.getJSONObject(i);
                        String playlistName = playlist.getString("name");

                        String playlistUrl = "";
                        if (playlist.has("external_urls")) {
                            JSONObject externalUrls = playlist.getJSONObject("external_urls");
                            playlistUrl = externalUrls.getString("spotify");
                        }

                        Log.d(TAG, "🎵 Playlist #" + i + ": " + playlistName + " (" + playlistUrl + ")");
                        playlists.add(new PlaylistItem(playlistName, playlistUrl));
                    }
                } else {
                    Log.e(TAG, " Failed to fetch playlists: Response code " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, " Error fetching playlists", e);
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, " Error closing resources", e);
                }
            }
            return playlists;
        }

        @Override
        protected void onPostExecute(List<PlaylistItem> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            if (result.isEmpty()) {
                errorText.setText("No playlists found for '" + music + "' and '" + sport + "'.");
                errorText.setVisibility(View.VISIBLE);
            } else {
                playlists.clear();
                playlists.addAll(result);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG, " Playlists updated in adapter!");
            }
        }
    }
}

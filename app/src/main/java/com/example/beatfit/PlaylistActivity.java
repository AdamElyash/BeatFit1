package com.example.beatfit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * מסך להצגת רשימת פלייליסטים מומלצים מה-Spotify API בהתאם להעדפות המשתמש.
 */
public class PlaylistActivity extends AppCompatActivity {
    private static final String TAG = "PlaylistActivity"; // תווית ללוגים

    // משתנים שיכילו מידע שהתקבל מהמסך הקודם
    private String accessToken; // טוקן לגישה ל-Spotify API
    private String music; // ז'אנר מוזיקה מועדף
    private String sport; // סוג ספורט מועדף

    // רכיבים גרפיים מה-XML
    private RecyclerView recyclerView; // רשימת הפלייליסטים
    private PlaylistAdapter adapter; // מתאם לרשימת הפלייליסטים
    private List<PlaylistItem> playlists; // רשימת האובייקטים של פלייליסטים
    private ProgressBar progressBar; // מחוון טעינה
    private TextView errorText; // הודעת שגיאה במקרה של כישלון

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // אתחול רכיבי ה-UI
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);

        // קביעת פריסת התצוגה של רשימת הפלייליסטים
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // אתחול הרשימה והמתאם
        playlists = new ArrayList<>();
        adapter = new PlaylistAdapter(this, playlists);
        recyclerView.setAdapter(adapter);

        // קבלת הנתונים מה-Intent שנשלח ממסך קודם
        accessToken = getIntent().getStringExtra("ACCESS_TOKEN");
        music = getIntent().getStringExtra("music");
        sport = getIntent().getStringExtra("sport");

        // בדיקה אם חסרים נתונים קריטיים (טוקן, מוזיקה או ספורט)
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

        // הצגת הודעה על חיפוש פלייליסטים
        Log.d(TAG, "Fetching playlists for: " + music + " and " + sport);
        new FetchPlaylistsTask().execute(accessToken, music + " " + sport);
    }

    /**
     * AsyncTask שמבצע חיפוש פלייליסטים ב-Spotify API ברקע
     */
    private class FetchPlaylistsTask extends AsyncTask<String, Void, List<PlaylistItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE); // הצגת מחוון טעינה
            errorText.setVisibility(View.GONE); // הסתרת הודעת שגיאה (אם קיימת)
        }

        @Override
        protected List<PlaylistItem> doInBackground(String... params) {
            String token = params[0]; // קבלת טוקן גישה
            String query = params[1]; // שאילתת חיפוש

            List<PlaylistItem> playlists = new ArrayList<>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // שיפור החיפוש - הוספת מילות מפתח כדי להרחיב את התוצאות
                String enhancedQuery = query + " OR workout OR hype OR gym OR training OR motivation";
                String encodedQuery = URLEncoder.encode(enhancedQuery, "UTF-8");

                // יצירת ה-URL של השאילתה ל-Spotify API
                String urlStr = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=playlist&limit=15";
                URL url = new URL(urlStr);

                // 🔹 יצירת חיבור HTTP לשרת ה-API של Spotify
                connection = (HttpURLConnection) url.openConnection();
                 // 🔹 הגדרת סוג הבקשה – כאן זה GET כי אנו מבקשים לקבל נתונים
                connection.setRequestMethod("GET");
                // 🔹 הוספת כותרת (Header) עם טוקן ההרשאה כדי שהשרת יזהה שהבקשה מגיעה ממשתמש מורשה
                connection.setRequestProperty("Authorization", "Bearer " + token);
                  // 🔹 קביעת זמן התחברות מקסימלי (15 שניות) כדי למנוע שהאפליקציה תיתקע אם אין תגובה מהשרת
                connection.setConnectTimeout(15000);
                // 🔹 קביעת זמן קריאה מקסימלי (15 שניות) – אם הנתונים לא יתקבלו בזמן הזה, הבקשה תיכשל
                connection.setReadTimeout(15000);


                // קבלת קוד תגובה מהשרת
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "📡 Response Code: " + responseCode);

                // אם התגובה תקינה (קוד 200)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // קריאת הנתונים שהתקבלו מה-API
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // ניתוח הנתונים שהתקבלו לתוך אובייקטי JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject playlistsObject = jsonResponse.getJSONObject("playlists");
                    JSONArray playlistsArray = playlistsObject.getJSONArray("items");

                    Log.d(TAG, "📌 Found " + playlistsArray.length() + " playlists.");

                    // לולאה שעוברת על כל הפלייליסטים שהתקבלו
                    for (int i = 0; i < playlistsArray.length(); i++) {
                        JSONObject playlist = playlistsArray.getJSONObject(i);
                        String playlistName = playlist.getString("name");
                        String playlistUrl = playlist.getJSONObject("external_urls").getString("spotify");

                        // יצירת אובייקט פלייליסט חדש והוספתו לרשימה
                        playlists.add(new PlaylistItem(playlistName, playlistUrl));
                    }
                } else {
                    Log.e(TAG, "❌ Failed to fetch playlists: Response code " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "⚠️ Error fetching playlists", e);
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error closing resources", e);
                }
            }
            return playlists;
        }

        /**
         * פונקציה זו מופעלת לאחר השלמת המשימה - מציגה את הנתונים ב-RecyclerView.
         */
        @Override
        protected void onPostExecute(List<PlaylistItem> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE); // הסתרת מחוון הטעינה

            // בדיקה אם נמצאו פלייליסטים
            if (result.isEmpty()) {
                errorText.setText("No playlists found.");
                errorText.setVisibility(View.VISIBLE);
            } else {
                playlists.clear(); // ניקוי הרשימה הנוכחית
                playlists.addAll(result); // הוספת הנתונים החדשים
                adapter.notifyDataSetChanged(); // עדכון התצוגה
                recyclerView.setVisibility(View.VISIBLE); // הצגת ה-RecyclerView
                Log.d(TAG, "✅ Playlists updated in adapter!");
            }
        }
    }
}

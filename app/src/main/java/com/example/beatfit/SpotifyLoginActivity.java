package com.example.beatfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SpotifyLoginActivity extends AppCompatActivity {

    // מזהה הלקוח (Client ID) שקיבלת מספוטיפיי כדי לאמת את האפליקציה שלך
    private static final String CLIENT_ID = "84cd06acfbd24f988275b174fdb68cb8";

    // כתובת ההפניה חזרה (Redirect URI) - משמשת כדי לקבל את ה-Access Token מהדפדפן
    private static final String REDIRECT_URI = "connectspotify://callback";

    // כתובת ה-URL שמשמשת לאימות המשתמש מול ספוטיפיי
    private static final String AUTH_URL = "https://accounts.spotify.com/authorize"
            + "?client_id=" + CLIENT_ID // מזהה הלקוח
            + "&response_type=token" // מבקש לקבל Access Token ישירות
            + "&redirect_uri=" + REDIRECT_URI // כתובת להפניה חזרה לאחר התחברות
            + "&scope=playlist-read-private"; // בקשת הרשאה לקרוא פלייליסטים פרטיים

    @Override
    protected void onCreate(Bundle savedInstanceState) { // הפונקציה שמופעלת כאשר האקטיביטי נטען
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login); // קביעת ממשק המשתמש

        Button loginButton = findViewById(R.id.btnLogin); // כפתור להתחברות לספוטיפיי
        loginButton.setOnClickListener(v -> { // כאשר המשתמש לוחץ על הכפתור
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL)); // פתיחת הדפדפן עם כתובת האימות של ספוטיפיי
            startActivity(browserIntent); // הפעלת הדפדפן כדי שהמשתמש יוכל להתחבר
        });
    }
}

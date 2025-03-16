package com.example.beatfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SpotifyRedirectActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyRedirectActivity"; // תיוג ליומן הריצה (Log) כדי לעקוב אחר הביצוע

    @Override
    protected void onCreate(Bundle savedInstanceState) { // פונקציה שנקראת כאשר האקטיביטי נוצר
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Redirecting from Spotify..."); // רישום ביומן הריצה שהתחיל תהליך הקליטה

        Uri uri = getIntent().getData(); // קבלת ה-URI שהוחזר מספוטיפיי לאחר האימות
        if (uri != null && uri.toString().startsWith("connectspotify://callback")) { // בדיקה אם ההפניה תקינה
            String fragment = uri.getFragment(); // שליפת החלק שמכיל את ה-Access Token
            if (fragment != null && fragment.contains("access_token=")) { // בדיקה אם קיים Access Token
                String accessToken = fragment.split("access_token=")[1].split("&")[0]; // חילוץ ה-Access Token מתוך ה-URI

                Log.d(TAG, " Spotify Access Token Received: " + accessToken); // הדפסת ה-Access Token ליומן הריצה

                // שמירת ה-Access Token בזיכרון המכשיר (SharedPreferences) לשימוש עתידי
                SharedPreferences.Editor editor = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE).edit();
                editor.putString("ACCESS_TOKEN", accessToken);
                editor.apply(); // שמירת השינויים

                // שליפת ההעדפות של המשתמש מהזיכרון המקומי (SharedPreferences)
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String music = prefs.getString("FAVORITE_MUSIC", ""); // קבלת העדפת מוזיקה
                String sport = prefs.getString("FAVORITE_SPORT", ""); // קבלת העדפת ספורט

                // אם חסרים נתונים, להציג הודעה ולסיים את האקטיביטי
                if (music.isEmpty() || sport.isEmpty()) {
                    Log.e(TAG, " Missing music or sport preference!"); // רישום שגיאה
                    Toast.makeText(this, "Missing preferences. Try again.", Toast.LENGTH_SHORT).show(); // הודעה למשתמש
                    finish();
                    return;
                }

                Log.d(TAG, "Passing to PlaylistActivity - Music: " + music + ", Sport: " + sport); // רישום הנתונים ליומן הריצה

                // יצירת Intent למסך הבא - `PlaylistActivity`, עם נתוני המשתמש
                Intent intent = new Intent(this, PlaylistActivity.class);
                intent.putExtra("ACCESS_TOKEN", accessToken); // העברת ה-Access Token
                intent.putExtra("music", music); // העברת סוג המוזיקה
                intent.putExtra("sport", sport); // העברת סוג הספורט
                startActivity(intent); // מעבר למסך הפלייליסטים
                finish(); // סגירת האקטיביטי הנוכחי כדי שלא ניתן יהיה לחזור אליו בטעות
            } else
            {
                Toast.makeText(this, " Failed to authenticate with Spotify", Toast.LENGTH_SHORT).show(); // הודעה על כישלון באימות
                Log.e(TAG, "Error parsing Spotify response"); // רישום ביומן הריצה על שגיאה
                finish();
            }
        } else
        {
            Log.e(TAG, " No valid redirect URI detected"); // רישום שגיאה ביומן הריצה
            finish();
        }
    }
}

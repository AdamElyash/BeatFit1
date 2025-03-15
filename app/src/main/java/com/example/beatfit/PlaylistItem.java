package com.example.beatfit;

/**
 * מחלקת המודל המייצגת פלייליסט.
 * כל אובייקט מסוג זה מייצג פלייליסט יחיד שהתקבל מ-Spotify.
 */
public class PlaylistItem {
    private String name; // שם הפלייליסט
    private String url;  // קישור לפלייליסט

    // קונסטרקטור
    public PlaylistItem(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Getter עבור שם הפלייליסט
    public String getName() {
        return name;
    }

    // Getter עבור כתובת הפלייליסט
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "PlaylistItem{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
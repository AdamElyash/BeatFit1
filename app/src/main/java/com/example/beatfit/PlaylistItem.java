package com.example.beatfit;

/**
 * מחלקת המודל המייצגת פלייליסט.
 * כל אובייקט מסוג זה מייצג פלייליסט יחיד שהתקבל מ-Spotify.
 */
public class PlaylistItem
{
    private String name; // שם הפלייליסט
    private String url;  // קישור לפלייליסט
    private String description; // תיאור הפלייליסט

    // קונסטרקטור עם תיאור
    public PlaylistItem(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description != null ? description : "";
    }

    // קונסטרקטור ללא תיאור (לתאימות לאחור)
    public PlaylistItem(String name, String url) {
        this(name, url, "");
    }

    // Getter עבור שם הפלייליסט
    public String getName() {
        return name;
    }

    // Getter עבור כתובת הפלייליסט
    public String getUrl() {
        return url;
    }

    // Getter עבור תיאור הפלייליסט
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "PlaylistItem{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
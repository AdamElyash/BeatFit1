package com.example.beatfit;

public class User {
    public String userId, name, email, profileImage;
    public String favoriteMusic, favoriteSport;

    // Default constructor required for Firebase
    public User() {}

    // קונסטרקטור בסיסי ללא העדפות מוזיקה וספורט
    public User(String userId, String name, String email, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    // קונסטרקטור מלא עם העדפות מוזיקה וספורט
    public User(String userId, String name, String email, String profileImage,
                String favoriteMusic, String favoriteSport) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.favoriteMusic = favoriteMusic;
        this.favoriteSport = favoriteSport;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", favoriteMusic='" + favoriteMusic + '\'' +
                ", favoriteSport='" + favoriteSport + '\'' +
                '}';
    }
}
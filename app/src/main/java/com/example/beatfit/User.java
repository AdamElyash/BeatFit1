package com.example.beatfit;

public class User {
    public String userId, name, email, profileImage;
    public String favoriteMusic, favoriteSport; // שדות חדשים

    // Default constructor required for Firebase
    public User() {}

    public User(String userId, String name, String email, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.favoriteMusic = favoriteMusic;
        this.favoriteSport = favoriteSport;
    }
}

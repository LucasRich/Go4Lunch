package com.lucas.go4lunch.Models.ProfileFile;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String email;
    private String dayRestaurant;

    public User() { }

    public User(String uid, String username, String urlPicture, String email, String dayRestaurant) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
        this.dayRestaurant = dayRestaurant;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getEmail() { return email; }
    public String getDayRestaurant() { return dayRestaurant; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setEmail(String email) { this.email = email; }
    public void setDayRestaurant(String dayRestaurant) { this.dayRestaurant = dayRestaurant; }
}

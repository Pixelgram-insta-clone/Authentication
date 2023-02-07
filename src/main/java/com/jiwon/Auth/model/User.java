package com.cognizant.Auth.model;

public class User {
    int id;
    String username;
    String profileImg;

    public User() {
    }

    public User(int id, String username, String profileImg) {
        this.id = id;
        this.username = username;
        this.profileImg = profileImg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}

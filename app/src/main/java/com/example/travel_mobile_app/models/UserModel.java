package com.example.travel_mobile_app.models;

import java.util.List;

public class UserModel {
    private String id, fullName,username,email,address,password, avatarURL;
    private List<String> following, followers;

    public UserModel(String id, String fullName, String username, String email, String address, String password, String avatarURL, List<String> following, List<String> followers) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.address = address;
        this.password = password;
        this.avatarURL = avatarURL;
        this.following = following;
        this.followers = followers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}

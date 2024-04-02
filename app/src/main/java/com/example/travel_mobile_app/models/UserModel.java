package com.example.travel_mobile_app.models;

public class UserModel {
    private String id, name,username,email,address,password, avataURL;
    private int following, followers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public UserModel(String id, String name, String username, String email, String address, String password, String avataURL, int followers, int following) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.password = password;
        this.avataURL = avataURL;
        this.followers = followers;
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public String getAvataURL() {
        return avataURL;
    }

    public void setAvataURL(String avataURL) {
        this.avataURL = avataURL;
    }

    public void setName(String name) {
        this.name = name;
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
}

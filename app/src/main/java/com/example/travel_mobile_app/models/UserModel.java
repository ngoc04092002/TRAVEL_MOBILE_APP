package com.example.travel_mobile_app.models;

import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {
    private String id, fullName, username, email, address, password, avatarURL;
    private List<String> following, followers;
    private boolean enableNotification, enableUpdate;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public UserModel() {
        // Default constructor required for Firestore deserialization
    }
    public UserModel(String id, String fullName, String username, String email, String address, String password, String avatarURL, List<String> followers, List<String> following, boolean enableNotification, boolean enableUpdate) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.address = address;
        this.password = password;
        this.avatarURL = avatarURL;
        this.followers = followers;
        this.following = following;
        this.enableNotification = enableNotification;
        this.enableUpdate = enableUpdate;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", following=" + following +
                ", followers=" + followers +
                ", enableNotification=" + enableNotification +
                ", enableUpdate=" + enableUpdate +
                '}';
    }

    public boolean isEnableNotification() {
        return enableNotification;
    }

    public void setEnableNotification(boolean enableNotification) {
        this.enableNotification = enableNotification;
    }

    public boolean isEnableUpdate() {
        return enableUpdate;
    }

    public void setEnableUpdate(boolean enableUpdate) {
        this.enableUpdate = enableUpdate;
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

    public String getFullName() {
        return fullName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL= avatarURL;
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
}
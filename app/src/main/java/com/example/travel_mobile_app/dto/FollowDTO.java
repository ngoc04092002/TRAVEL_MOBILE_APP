package com.example.travel_mobile_app.dto;

public class FollowDTO {
    private String profileImage;
    private String userId;
    private String username;
    private int numberOfFollowers;

    public FollowDTO(String userId,String profileImage, String username, int numberOfFollowers) {
        this.userId = userId;
        this.profileImage = profileImage;
        this.username = username;
        this.numberOfFollowers = numberOfFollowers;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(int numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }
}

package com.example.travel_mobile_app.dto;

public class FollowDTO {
    private String profileImage;
    private String username;
    private int numberOfFollowers;

    public FollowDTO(String profileImage, String username, int numberOfFollowers) {
        this.profileImage = profileImage;
        this.username = username;
        this.numberOfFollowers = numberOfFollowers;
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

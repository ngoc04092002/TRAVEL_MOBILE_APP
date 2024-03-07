package com.example.travel_mobile_app.dto;

public class FollowDTO {
    private int profile;
    private String username;
    private Integer numberOfFollowers;

    public FollowDTO(int profile, String username, Integer numberOfFollowers) {
        this.profile = profile;
        this.username = username;
        this.numberOfFollowers = numberOfFollowers;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(Integer numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }
}

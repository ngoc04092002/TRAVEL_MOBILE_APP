package com.example.travel_mobile_app.models;


public class StoryModel {
    private int story,profile;
    private String name;

    public int getStory() {
        return story;
    }

    public void setStory(int story) {
        this.story = story;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StoryModel(int story, int profile, String name) {
        this.story = story;
        this.profile = profile;
        this.name = name;
    }
}

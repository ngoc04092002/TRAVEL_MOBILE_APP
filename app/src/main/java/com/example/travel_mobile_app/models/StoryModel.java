package com.example.travel_mobile_app.models;


import java.util.ArrayList;

public class StoryModel {
   private String storyId;
   private String storyBy;
   private String fullName;
   private long storyAt;
   private String uri;
   private ArrayList<UserStory> userStories;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public StoryModel() {
    }

    public StoryModel(String storyId, String storyBy, long storyAt, String uri) {
        this.storyId = storyId;
        this.storyBy = storyBy;
        this.storyAt = storyAt;
        this.uri = uri;
    }

    public ArrayList<UserStory> getUserStories() {
        return userStories;
    }

    public void setUserStories(ArrayList<UserStory> userStories) {
        this.userStories = userStories;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "StoryModel{" +
                "storyId='" + storyId + '\'' +
                ", storyBy='" + storyBy + '\'' +
                ", storyAt=" + storyAt +
                ", uri='" + uri + '\'' +
                ", userStories=" + userStories +
                '}';
    }
}

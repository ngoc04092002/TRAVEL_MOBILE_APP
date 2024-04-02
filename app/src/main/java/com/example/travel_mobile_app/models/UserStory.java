package com.example.travel_mobile_app.models;

public class UserStory {
    private String uri;
    private long storyAt;

    public UserStory(String uri, long storyAt) {
        this.uri = uri;
        this.storyAt = storyAt;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}

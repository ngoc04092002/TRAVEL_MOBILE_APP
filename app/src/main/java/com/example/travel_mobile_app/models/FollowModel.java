package com.example.travel_mobile_app.models;

public class FollowModel {
    private String followBy,followAt;

    public FollowModel(String followBy, String followAt) {
        this.followBy = followBy;
        this.followAt = followAt;
    }

    public String getFollowBy() {
        return followBy;
    }

    public void setFollowBy(String followBy) {
        this.followBy = followBy;
    }

    public String getFollowAt() {
        return followAt;
    }

    public void setFollowAt(String followAt) {
        this.followAt = followAt;
    }
}

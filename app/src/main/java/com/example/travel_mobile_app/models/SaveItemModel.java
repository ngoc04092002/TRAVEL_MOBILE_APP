package com.example.travel_mobile_app.models;

public class SaveItemModel {
    private Long id;
    private int profile;
    private String title,time;

    public SaveItemModel(Long id, int profile, String title, String time) {
        this.id = id;
        this.profile = profile;
        this.title = title;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

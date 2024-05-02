package com.example.travel_mobile_app.models;

public class SaveItemModel {
    private Long id;
    private String postID, savedBy;
    private String title,time, img;

    public SaveItemModel(Long id, String postID, String savedBy, String title, String time, String img) {
        this.id = id;
        this.postID = postID;
        this.savedBy = savedBy;
        this.title = title;
        this.time = time;
        this.img = img;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getSaveBy() {
        return savedBy;
    }

    public void setSaveBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

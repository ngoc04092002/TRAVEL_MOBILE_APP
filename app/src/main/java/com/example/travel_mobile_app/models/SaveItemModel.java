package com.example.travel_mobile_app.models;

public class SaveItemModel {
    private String id;
    private String postID, savedBy;
    private String title, img;
    private Long time;
    private int type; // 0: post, 1: address

    public SaveItemModel(String id, String postID, String savedBy, String title, Long time, String img, int type) {
        this.id = id;
        this.postID = postID;
        this.savedBy = savedBy;
        this.title = title;
        this.time = time;
        this.img = img;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SaveItemModel() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

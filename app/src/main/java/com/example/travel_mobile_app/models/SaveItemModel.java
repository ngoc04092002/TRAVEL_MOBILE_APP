package com.example.travel_mobile_app.models;

public class SaveItemModel {
    private String id;
    private String img;
    private String title;
    private Long time;
    private String savedBy;
    private String postId;

    public SaveItemModel(String id, String img, String title, Long time) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.time = time;
    }

    public SaveItemModel(String id, String img, String title, Long time, String savedBy, String postId) {
        this.id = id;
        this.img = img;
        this.title = title;
        this.time = time;
        this.savedBy = savedBy;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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
}

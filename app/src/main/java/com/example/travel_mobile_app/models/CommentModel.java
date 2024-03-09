package com.example.travel_mobile_app.models;

public class CommentModel {
    private int profile;
    private String commentBy;
    private String commentAt;
    private String content;
    private String createAt;

    public CommentModel(int profile, String commentBy, String commentAt, String content, String createAt) {
        this.profile = profile;
        this.commentBy = commentBy;
        this.commentAt = commentAt;
        this.content = content;
        this.createAt = createAt;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public String getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(String commentAt) {
        this.commentAt = commentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}

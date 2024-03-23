package com.example.travel_mobile_app.models;

public class CommentModel {
    private int profile;
    private String commentBy;
    private String content;
    private long createAt;
    private String commentId;

    public CommentModel(String commentId, int profile, String commentBy, String content, long createAt) {
        this.profile = profile;
        this.commentBy = commentBy;
        this.content = content;
        this.createAt = createAt;
        this.commentId = commentId;
    }

    public CommentModel(){

    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}

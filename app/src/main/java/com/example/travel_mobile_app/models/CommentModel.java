package com.example.travel_mobile_app.models;

public class CommentModel {
    private String image;
    private String commentBy;
    private String userId;
    private String content;
    private long createAt;
    private String commentId;

    public CommentModel(String userId,String commentId, String image, String commentBy, String content, long createAt) {
        this.userId = userId;
        this.image = image;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

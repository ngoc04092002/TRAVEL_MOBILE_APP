package com.example.travel_mobile_app.models;

import java.util.List;

public class PostModel {
    private String postId;
    private String postImage;
    private String postedBy;
    private String postDescription;
    private long postedAt;

    private List<String> likes, save;
    private String shareBy;
    private Boolean isShare;
    private long shareAt;
    private String originPostId;

    public String getOriginPostId() {
        return originPostId;
    }

    public void setOriginPostId(String originPostId) {
        this.originPostId = originPostId;
    }

    private List<CommentModel> comments;

    public PostModel() {
    }

    public PostModel(String postId, String postImage, String postedBy, String postDescription, long postedAt, List<String> likes, List<String> save, String shareBy, Boolean isShare, List<CommentModel> comments) {
        this.postId = postId;
        this.postImage = postImage;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.postedAt = postedAt;
        this.likes = likes;
        this.save = save;
        this.shareBy = shareBy;
        this.isShare = isShare;
        this.comments = comments;
    }

    public long getShareAt() {
        return shareAt;
    }

    public void setShareAt(long shareAt) {
        this.shareAt = shareAt;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getSave() {
        return save;
    }

    public void setSave(List<String> save) {
        this.save = save;
    }

    public String getShareBy() {
        return shareBy;
    }

    public void setShareBy(String shareBy) {
        this.shareBy = shareBy;
    }

    public Boolean getShare() {
        return isShare;
    }

    public void setShare(Boolean share) {
        isShare = share;
    }

    public List<CommentModel> getComments() {
        return comments;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "postId='" + postId + '\'' +
                ", postImage='" + postImage + '\'' +
                ", postedBy='" + postedBy + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", postedAt=" + postedAt +
                ", likes=" + likes +
                ", save=" + save +
                ", shareBy='" + shareBy + '\'' +
                ", isShare=" + isShare +
                ", comments=" + comments +
                '}';
    }
}

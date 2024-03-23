package com.example.travel_mobile_app.models;

import java.util.List;

public class PostModel {
    private String postId;
    private String postImage;
    private String postedBy;
    private String postDescription;
    private long postedAt;

    private List<String> likes, share, save;

    private List<CommentModel> comments;
    public PostModel() {
    }
    public PostModel(String postId, String postImage, String postedBy, String postDescription, long postedAt, List<String> likes, List<String> share, List<String> save) {
        this.postId = postId;
        this.postImage = postImage;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.postedAt = postedAt;
        this.likes = likes;
        this.share = share;
        this.save = save;
    }

    public List<CommentModel> getComments() {
        return comments;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
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

    public List<String> getShare() {
        return share;
    }

    public void setShare(List<String> share) {
        this.share = share;
    }

    public List<String> getSave() {
        return save;
    }

    public void setSave(List<String> save) {
        this.save = save;
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
                ", share=" + share +
                ", save=" + save +
                ", comments=" + comments +
                '}';
    }
}

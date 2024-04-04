package com.example.travel_mobile_app.dto;

public class UserToken {
    private String userId;
    private String deviceToken;
    private long deviceTokenAt;

    public UserToken() {
    }

    public UserToken(String userId, String deviceToken, long deviceTokenAt) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.deviceTokenAt = deviceTokenAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public long getDeviceTokenAt() {
        return deviceTokenAt;
    }

    public void setDeviceTokenAt(long deviceTokenAt) {
        this.deviceTokenAt = deviceTokenAt;
    }
}

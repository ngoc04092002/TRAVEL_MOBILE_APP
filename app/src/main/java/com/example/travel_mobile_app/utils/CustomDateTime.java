package com.example.travel_mobile_app.utils;

import java.time.Duration;
import java.time.Instant;

public class CustomDateTime {
    public CustomDateTime() {
    }

    static public String formatDate(long previousTimeMillis) {
        long currentTimeMillis = System.currentTimeMillis();

        Instant currentInstant = Instant.ofEpochMilli(currentTimeMillis);
        Instant previousInstant = Instant.ofEpochMilli(previousTimeMillis);

        Duration duration = Duration.between(previousInstant, currentInstant);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = days / 365;

        if (years > 0) {
            return years + " năm trước";
        } else if (months > 0) {
            return months + " tháng trước";
        } else if (days > 0) {
            return days + " ngày trước";
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else if (minutes > 0) {
            return minutes + " phút trước";
        } else {
            if (seconds <= 0) {
                return "Vừa mới";
            }
            return seconds + " giây trước";
        }
    }
}

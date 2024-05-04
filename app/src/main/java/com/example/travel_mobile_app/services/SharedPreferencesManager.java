package com.example.travel_mobile_app.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.travel_mobile_app.models.UserModel;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public class SharedPreferencesManager {
    private static SharedPreferences sharedPref;

    private SharedPreferencesManager() {
    }

    public static void init(Context context) {
        if (sharedPref == null)
            sharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static final String FCM_DEVICE_TOKEN = "FCM_DEVICE_TOKEN";

    public static @Nullable String readDeviceToken() {
        return sharedPref.getString(FCM_DEVICE_TOKEN, null);
    }

    public static void writeDeviceToken(String value) {
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putString(FCM_DEVICE_TOKEN, value);
        prefsEditor.apply();
    }

    public static final String USER_INFO = "USER_INFO";

    public static @Nullable UserModel readUserInfo() {
        String userLocal = sharedPref.getString(USER_INFO, null);
        Gson gson = new Gson();
        UserModel p = gson.fromJson(userLocal, UserModel.class);
        return p;
    }

    public static void writeUserInfo(UserModel userInfo) {
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String personString = gson.toJson(userInfo);
        prefsEditor.putString(USER_INFO, personString);
        prefsEditor.apply();
    }

    public static final String FCM_CHANNEL_LIST = "FCM_CHANNEL_LIST";

    public static Set<String> readPostSearchHistory() {
        Set<String> history = sharedPref.getStringSet(FCM_CHANNEL_LIST, null);
        if (history == null) {
            return new HashSet<>();
        } else {
            return history;
        }
    }

    public static void writePostSearchHistory(Set<String> value) {
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putStringSet(FCM_CHANNEL_LIST, value);
        prefsEditor.apply();
    }
}

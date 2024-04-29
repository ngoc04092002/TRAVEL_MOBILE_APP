package com.example.travel_mobile_app.services;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import javax.annotation.Nullable;

public class SharedPreferencesManager {
    private static SharedPreferences sharedPref;
    private SharedPreferencesManager() { }

    public static void init(Context context) {
        if(sharedPref == null)
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

    public static final String FCM_CHANNEL_LIST = "FCM_CHANNEL_LIST";
    public static @Nullable String[] readChannelList() {
        return (String[]) sharedPref.getStringSet(FCM_CHANNEL_LIST, null).toArray();
    }
    public static void writeChannelList(Set<String> value) {
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putStringSet(FCM_CHANNEL_LIST, value);
        prefsEditor.apply();
    }
}

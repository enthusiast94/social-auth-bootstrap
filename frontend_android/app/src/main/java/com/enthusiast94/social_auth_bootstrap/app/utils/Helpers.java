package com.enthusiast94.social_auth_bootstrap.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by manas on 23-08-2015.
 */
public class Helpers {

    public static void writeJsonToPrefs(Context context, String key, JSONObject json) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(key, json.toString()).apply();
    }

    public static JSONObject readJsonFromPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(key, null);
        if (value == null) {
            return null;
        } else {
            try {
                return new JSONObject(value);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package com.enthusiast94.social_auth_bootstrap.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.enthusiast94.social_auth_bootstrap.app.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    public static void clearPrefs(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }

    public static void hideSoftKeyboard(Context context, IBinder windowToken) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    public static String validateEmail(String email, Resources res) {
        if (email.length() == 0) {
            return res.getString(R.string.error_required_field);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return res.getString(R.string.error_invalid_email);
        } else {
            return null;
        }
    }

    public static String validatePassword(String password, Resources res) {
        if (password.length() == 0) {
            return  res.getString(R.string.error_required_field);
        } else if (password.length() < 8) {
            return res.getString(R.string.error_short_password);
        } else {
            return null;
        }
    }

    public static String validateName(String name, Resources res) {
        if (name.length() == 0) {
            return res.getString(R.string.error_required_field);
        } else {
            return null;
        }
    }

    public static Map<String, String> parseQueryParams(String url) {
        url = url.substring(url.indexOf("?") + 1, url.length());
        String[] split = url.split("&");

        Map<String, String> params = new HashMap<String, String>();
        for (String item : split) {
            String[] split2 = item.split("=");
            params.put(split2[0], split2[1]);
        }

        return params;
    }

    public static void showSnackbar(View view, String type, String message, Resources res) {
        Map<String, String> typesMap = new HashMap<String, String>();
        typesMap.put("success", res.getString(R.string.success_base));
        typesMap.put("error", res.getString(R.string.error_base));

        if (!typesMap.containsKey(type))
            throw new IllegalArgumentException("Allowed values for 'type' are: " + typesMap.keySet().toString());

        Snackbar.make(
                view,
                Html.fromHtml(typesMap.get(type) + message),
                Snackbar.LENGTH_SHORT
        ).show();
    }

    public static void showSnackbar(View view, String type, int resId, Resources res) {
        showSnackbar(view, type, res.getString(resId), res);
    }
}

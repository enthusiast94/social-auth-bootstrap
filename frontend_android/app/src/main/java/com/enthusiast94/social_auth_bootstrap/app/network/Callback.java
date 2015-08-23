package com.enthusiast94.social_auth_bootstrap.app.network;

import org.json.JSONObject;

/**
 * Created by manas on 22-08-2015.
 */
public interface Callback {
    void onSuccess(JSONObject data);
    void onFailure(int statusCode, String message);
}

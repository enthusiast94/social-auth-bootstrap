package com.enthusiast94.social_auth_starter.utils;

import com.google.gson.Gson;

/**
 * Created by ManasB on 7/28/2015.
 */

public class ApiResponse {

    private int status;
    private String error;
    private Object data;

    public ApiResponse(int status, String error, Object data) {
        this.status = status;
        this.error = error;
        this.data = data;
    }

    // only used when JsonTransformer cannot be used
    public String toJson() {
        return new Gson().toJson(this);
    }
}

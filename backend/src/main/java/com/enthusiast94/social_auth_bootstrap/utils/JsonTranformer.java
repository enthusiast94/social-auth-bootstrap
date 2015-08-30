package com.enthusiast94.social_auth_bootstrap.utils;

import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * Created by ManasB on 7/28/2015.
 */
public class JsonTranformer implements ResponseTransformer
{
    @Override
    public String render(Object model) throws Exception {
        return new Gson().toJson(model);
    }
}

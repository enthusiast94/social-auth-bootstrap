package com.enthusiast94.social_auth_starter.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ManasB on 7/28/2015.
 */
public class Helpers {

    public static String bodyParams(String body, String key) {
        Pattern pattern = Pattern.compile(key + "=" + "([^&]*)");
        Matcher m = pattern.matcher(body);
        if (m.find()) {
            return m.group(1).trim();
        }

        return null;
    }
}

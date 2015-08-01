package com.enthusiast94.social_auth_starter.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
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

    public static String stringifyParams(Map<String, String> queryParams) {
        int pos = 0;

        String paramsString = "";

        for (String key : queryParams.keySet()) {
            if (pos == 0) {
                paramsString  += key + "=" + queryParams.get(key);
            } else {
                paramsString  += "&" + key + "=" + queryParams.get(key);
            }

            pos++;
        }

        return paramsString;
    }

    public static String httpGet(String urlString, Map<String, String> queryParams) throws Exception {
        // add query params to url
        urlString += "?" + stringifyParams(queryParams);

        // setup url connection
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    public static String httpPost(String urlString, Map<String, String> postParams) throws Exception {
        // setup url connection
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // convert post params to string
        String postData = stringifyParams(postParams);

        // write post data to connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(postData);
        writer.close();

        // read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }
}

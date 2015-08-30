package com.enthusiast94.social_auth_starter.utils;

import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by manas on 30-08-2015.
 */
public class OauthCredentialsParser {

    private JSONObject fileJson;

    public OauthCredentialsParser(String path) {
        try {
            File credentialsFile = new File(path);
            FileInputStream fis = new FileInputStream(credentialsFile);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(fis));

            StringBuilder builder = new StringBuilder();
            String output = "";
            while ((output = bReader.readLine()) != null) {
                builder.append(output);
            }

            bReader.close();

            fileJson = new JSONObject(builder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerRedirectUriBase() {
        return fileJson.getString("server_redirect_uri_base");
    }

    public String getClientRedirectUri() {
        return fileJson.getString("client_redirect_uri");
    }

    public String getDefaultUserPassword() {
        return fileJson.getString("default_user_password");
    }

    public Map<String, String> getOauth2Credentials(String providerName) {
        JSONObject credentialsJson = fileJson.getJSONObject("oauth2_credentials").getJSONObject(providerName);

        Map<String, String> credentialsMap = new HashMap<>();
        credentialsMap.put("id", credentialsJson.getString("id"));
        credentialsMap.put("secret", credentialsJson.getString("secret"));
        credentialsMap.put("redirect_uri", getServerRedirectUriBase() + credentialsJson.getString("server_redirect_uri_path"));

        return credentialsMap;
    }
}

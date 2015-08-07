package com.enthusiast94.social_auth_starter.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by manas on 05-08-2015.
 */

@Entity("linked_accounts")
public class LinkedAccount {

    @Id
    private String id;
    private String userId;
    private String providerName;
    private String accessToken;
    private String userName;
    private String userEmail;

    public LinkedAccount() {
        // empty constructor required by Morphia for querying
    }

    public LinkedAccount(String userId, String providerName, String accessToken, String userName, String userEmail) {
        id = new ObjectId().toString();
        this.userId = userId;
        this.providerName = providerName;
        this.accessToken = accessToken;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}

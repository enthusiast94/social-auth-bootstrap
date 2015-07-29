package com.enthusiast94.social_auth_starter.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

/**
 * Created by ManasB on 7/28/2015.
 */

@Entity("access_tokens")
public class AccessToken {

    @Id
    private String id;
    private String userId;
    private String accessToken;
    private long createdAt;
    private int expiresIn;

    public AccessToken() {
        // empty constructor required by Morphia for querying
    }

    public AccessToken(String userId) {
        id = new ObjectId().toString();
        accessToken = UUID.randomUUID().toString();
        createdAt = System.currentTimeMillis() / 1000;
        expiresIn = 86400; // 1 day for now

        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public boolean hasExpired() {
        return ((System.currentTimeMillis() / 1000) - this.createdAt) > this.expiresIn;
    }
}


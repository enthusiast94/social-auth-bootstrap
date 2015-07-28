package com.enthusiast94.social_auth_starter.models;

import jdk.nashorn.internal.ir.annotations.Reference;
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
    @Reference
    private User user;
    private String accessToken;
    private long createdAt;
    private int expiresIn;

    public AccessToken() {
        // empty constructor required by Morphia for querying
    }

    public AccessToken(User user) {
        id = new ObjectId().toString();
        accessToken = UUID.randomUUID().toString();
        createdAt = System.currentTimeMillis() / 1000;
        expiresIn = 86400; // 1 day for now

        this.user = user;
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

    public User getUser() {
        return user;
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

    public void setUser(User user) {
        this.user = user;
    }
}


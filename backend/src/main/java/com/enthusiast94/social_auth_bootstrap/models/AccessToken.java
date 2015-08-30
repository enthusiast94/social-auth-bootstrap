package com.enthusiast94.social_auth_bootstrap.models;

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
    private String value;

    public AccessToken() {
        // empty constructor required by Morphia for querying
    }

    public AccessToken(String userId) {
        id = new ObjectId().toString();
        value = UUID.randomUUID().toString();

        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public String getUserId() {
        return userId;
    }
}


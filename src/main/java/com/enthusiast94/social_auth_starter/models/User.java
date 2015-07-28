package com.enthusiast94.social_auth_starter.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by ManasB on 7/28/2015.
 */

@Entity("users")
public class User {

    @Id
    private String id;
    private String username;
    private String passwordHash;
    private String email;

    public User() {
        // empty constructor required by Morphia for querying
    }

    public User(String username, String passwordHash, String email) {
        id = new ObjectId().toString();

        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

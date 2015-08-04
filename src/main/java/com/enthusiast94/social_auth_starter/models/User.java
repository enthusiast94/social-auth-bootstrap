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
    private String name;
    private String passwordHash;

    public User() {
        // empty constructor required by Morphia for querying
    }

    public User(String username, String name, String passwordHash) {
        id = new ObjectId().toString();

        this.username = username;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}

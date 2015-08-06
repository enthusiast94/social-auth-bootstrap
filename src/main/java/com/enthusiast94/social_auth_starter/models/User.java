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
    private String email;
    private String name;
    private String avatar;
    private String password;

    public User() {
        // empty constructor required by Morphia for querying
    }

    public User(String email, String name, String avatar, String password) {
        id = new ObjectId().toString();

        this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

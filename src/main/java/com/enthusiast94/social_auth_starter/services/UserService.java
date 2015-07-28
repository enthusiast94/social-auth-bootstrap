package com.enthusiast94.social_auth_starter.services;

import com.enthusiast94.social_auth_starter.models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.mongodb.morphia.Datastore;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ManasB on 7/28/2015.
 */

public class UserService {

    Datastore db;

    public UserService(Datastore db) {
        this.db = db;
    }

    public List<User> getAllUsers() {
        return  db.createQuery(User.class)
                .asList();
    }

    public User getUserById(String id) {
        return db.createQuery(User.class)
                .field("id").equal(id)
                .get();
    }

    public User getUserByUsername(String username) {
        return db.createQuery(User.class)
                .field("username").equal(username)
                .get();
    }

    public User createUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(username, hashedPassword, null);
        db.save(user);

        return user;
    }

    // returns error message if validation fails, else returns null
    public String validateUsername(String username) {
        if (username.length() < 6) {
            return "Username must be at least 6 characters long";
        }

        if (Pattern.compile("[^a-zA-Z0-9_]").matcher(username).find()) {
            return "Username cannot contain special characters other than underscore";
        }

        // check if another user already exists with same username
        if (getUserByUsername(username) != null) {
            return "Another user with the same username already exists";
        }

        return null;
    }

    // returns error message if validation fails, else returns null
    public String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }

        if (Pattern.compile("[^a-zA-Z0-9_]").matcher(password).find()) {
            return "Password cannot contain special characters other than underscore";
        }

        return null;
    }
}

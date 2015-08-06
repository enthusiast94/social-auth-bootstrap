package com.enthusiast94.social_auth_starter.services;

import com.enthusiast94.social_auth_starter.models.User;
import org.apache.commons.validator.routines.EmailValidator;
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

    public User getUserByEmail(String email) {
        return db.createQuery(User.class)
                .field("email").equal(email)
                .get();
    }

    public User createUser(String email, String name, String password) {
        String hashedPassword = hashPassword(password);

        User user = new User(email, name, hashedPassword);
        db.save(user);

        return user;
    }

    public void updateUser(User userToUpdate) {
        db.save(userToUpdate);
    }

    public void deleteUser(User userToDelete) {
        db.delete(userToDelete);
    }

    // returns error message if validation fails, else returns null
    public String validateEmail(String email) {
        if (email == null) {
            return "Email is required";
        }

        if (email.length() < 6) {
            return "Email must be at least 6 characters long";
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            return "Invalid email address format";
        }

        // check if another user already exists with same email
        if (getUserByEmail(email) != null) {
            return "Another user with the same email already exists";
        }

        return null;
    }

    // returns error message if validation fails, else returns null
    public String validateName(String name) {
        if (name == null) {
            return "Name is required";
        }

        if (name.length() == 0) {
            return "Name must be at least 1 character long";
        }

        return null;
    }

    // returns error message if validation fails, else returns null
    public String validatePassword(String password) {
        if (password == null) {
            return "Password is required";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }

        if (Pattern.compile("[^a-zA-Z0-9_]").matcher(password).find()) {
            return "Password cannot contain special characters other than underscore";
        }

        return null;
    }

    public boolean doesPasswordMatch(String password, String passwordHash) {
        return BCrypt.checkpw(password, passwordHash);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

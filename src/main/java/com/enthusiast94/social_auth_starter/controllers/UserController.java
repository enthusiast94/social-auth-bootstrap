package com.enthusiast94.social_auth_starter.controllers;

import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.ApiResponse;
import com.enthusiast94.social_auth_starter.utils.Helpers;
import com.google.gson.Gson;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by ManasB on 7/28/2015.
 */

public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void setupEndpoints() {
        /**
         * Create new user
         */
        post(
                "/users",
                (req, res) -> {
                    if (!req.contentType().equals("application/x-www-form-urlencoded"))
                        return new ApiResponse(500, "content-type must be 'application/x-www-form-urlencoded'", null);

                    String username = Helpers.bodyParams(req.body(), "username");
                    String password = Helpers.bodyParams(req.body(), "password");

                    if (username == null || password == null)
                        return new ApiResponse(500, "username and password are both required", null);

                    String usernameError = userService.validateUsername(username);
                    if (usernameError != null) {
                        return new ApiResponse(500, usernameError, null);
                    }

                    String passwordError = userService.validatePassword(password);
                    if (passwordError != null) {
                        return new ApiResponse(500, passwordError, null);
                    }

                    return new ApiResponse(200, null, userService.createUser(username, password).getId());
                },
                (object) -> new Gson().toJson(object));


        /**
         * Get all users
         */
        get(
                "/users",
                (req, res) -> {
                    return new ApiResponse(200, null, userService.getAllUsers());
                },
                (object) -> new Gson().toJson(object)
        );
    }

}

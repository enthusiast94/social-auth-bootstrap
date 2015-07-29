package com.enthusiast94.social_auth_starter.controllers;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.models.User;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.ApiResponse;
import com.enthusiast94.social_auth_starter.utils.Helpers;
import com.enthusiast94.social_auth_starter.utils.JsonTranformer;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by ManasB on 7/28/2015.
 */

public class UserController {

    UserService userService;
    AccessTokenService accessTokenService;

    public UserController(UserService userService, AccessTokenService accessTokenService) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;
    }

    public void setupEndpoints() {
        /**
         * Create new user
         */
        post(
                "/users",
                (req, res) -> {
                    if (req.contentType() == null || !req.contentType().equals("application/x-www-form-urlencoded"))
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

                    User user = userService.createUser(username, password);

                    AccessToken accessToken = accessTokenService.createAccessToken(user);

                    // set unneeded fields to null
                    accessToken.setId(null);
                    accessToken.setUser(null);

                    return new ApiResponse(200, null, accessToken);
                },
                new JsonTranformer()
        );

        /**
         * Returns currently authenticated user
         */
        get(
                "/me",
                (req, res) -> {
                    String accessTokenString = req.headers("Authorization").substring("Token".length()+1, req.headers("Authorization").length());
                    User user = accessTokenService.getAccessTokenUser(accessTokenString);

                    // set unneeded fields to null
                    user.setId(null);
                    user.setPasswordHash(null);

                    return new ApiResponse(200, null, user);
                },
                new JsonTranformer()
        );
    }

}

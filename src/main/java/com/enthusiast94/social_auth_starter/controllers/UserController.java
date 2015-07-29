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

                    AccessToken accessToken = accessTokenService.createAccessToken(user.getId());

                    // set unneeded fields to null
                    accessToken.setId(null);
                    accessToken.setUserId(null);

                    return new ApiResponse(200, null, accessToken);
                },
                new JsonTranformer()
        );

        /**
         * Returns currently authenticated user
         */
        get(
                "/me/",
                (req, res) -> {
                    String accessTokenString = (String) req.attribute("accessToken");
                    User user = userService.getUserById(accessTokenService.getAccessTokenByAccessTokenString(accessTokenString).getUserId());

                    // set unneeded fields to null
                    user.setId(null);
                    user.setPasswordHash(null);

                    return new ApiResponse(200, null, user);
                },
                new JsonTranformer()
        );

        /**
         * Authenticates an existing user
         */
        post(
                "/auth",
                (req, res) -> {
                    if (req.contentType() == null || !req.contentType().equals("application/x-www-form-urlencoded"))
                        return new ApiResponse(500, "content-type must be 'application/x-www-form-urlencoded'", null);

                    String username = Helpers.bodyParams(req.body(), "username");
                    String password = Helpers.bodyParams(req.body(), "password");

                    if (username == null || password == null)
                        return new ApiResponse(500, "username and password are both required", null);

                    // check if user who is trying to authenticate exists
                    User requestedUser = userService.getUserByUsername(username);
                    if (requestedUser == null) return new ApiResponse(401, "username is incorrect", null);

                    // check if the sent password matches the stored password hash
                    if (!userService.doesPasswordMatch(password, requestedUser.getPasswordHash()))
                        return new ApiResponse(401, "password is incorrect", null);

                    // if an access token already exists, delete it and create a new one, else simply create a new one
                    AccessToken existingAccessToken = accessTokenService.getAccessTokenByUserId(requestedUser.getId());
                    if (existingAccessToken != null) accessTokenService.deleteAccessToken(existingAccessToken);

                    AccessToken newAccessToken = accessTokenService.createAccessToken(requestedUser.getId());

                    // set unneeded fields to null
                    newAccessToken.setId(null);
                    newAccessToken.setUserId(null);

                    return new ApiResponse(200, null, newAccessToken);
                },
                new JsonTranformer()
        );

        /**
         * De-authenticates currently authenticated user
         */
        post(
                "/me/deauth",
                (req, res) -> {
                    // delete access token of currently authenticated user
                    String accessTokenString = (String) req.attribute("accessToken");
                    AccessToken accessTokenToDelete = accessTokenService.getAccessTokenByAccessTokenString(accessTokenString);
                    accessTokenService.deleteAccessToken(accessTokenToDelete);

                    return new ApiResponse(200, null, null);
                },
                new JsonTranformer()
        );
    }

}

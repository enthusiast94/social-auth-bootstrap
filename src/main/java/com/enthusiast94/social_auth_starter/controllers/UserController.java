package com.enthusiast94.social_auth_starter.controllers;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.models.User;
import com.enthusiast94.social_auth_starter.oauth_strategies.OAuthStrategy;
import com.enthusiast94.social_auth_starter.oauth_strategies.OAuthStrategyFactory;
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

    private UserService userService;
    private AccessTokenService accessTokenService;
    private OAuthStrategyFactory oAuthStrategyFactory;

    public UserController(UserService userService, AccessTokenService accessTokenService) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;

        oAuthStrategyFactory = new OAuthStrategyFactory(userService, accessTokenService);
    }

    public void setupEndpoints() {
        /**
         * Create new user
         */
        post(
                "/users",
                (req, res) -> {
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
                    AccessToken accessToken = (AccessToken) req.attribute("accessToken");
                    User user = userService.getUserById(accessToken.getUserId());

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

                    // if an access token already exists, simply return it, else create a new one
                    AccessToken accessToken = accessTokenService.getAccessTokenByUserId(requestedUser.getId());
                    if (accessToken == null) {
                        accessToken = accessTokenService.createAccessToken(requestedUser.getId());
                    }

                    // set unneeded fields to null
                    accessToken.setId(null);
                    accessToken.setUserId(null);

                    return new ApiResponse(200, null, accessToken);
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
                    AccessToken accessTokenToDelete = (AccessToken) req.attribute("accessToken");
                    accessTokenService.deleteAccessToken(accessTokenToDelete);

                    return new ApiResponse(200, null, null);
                },
                new JsonTranformer()
        );

        /**
         * Deletes currently authenticated user
         */
        post(
                "/me/delete",
                (req, res) -> {
                    // delete currently authenticated user's access token
                    AccessToken accessToken = (AccessToken) req.attribute("accessToken");
                    accessTokenService.deleteAccessToken(accessToken);

                    // delete currently authenticated user
                    User userToDelete = userService.getUserById(accessToken.getUserId());
                    userService.deleteUser(userToDelete);

                    return new ApiResponse(200, null, null);
                },
                new JsonTranformer()
        );

        /**
         * Creates new user or authenticates existing user using details fetched from the specified oauth provider
         */
        get(
                "/oauth2-callback/:provider",
                (req, res) -> {
                    OAuthStrategy oAuthStrategy = oAuthStrategyFactory.getStrategy(req.params("provider"));
                    String redirectUrl = oAuthStrategy.authorize(req.queryParams("code"), req.queryParams("error"));
                    res.redirect(redirectUrl);

                    return null;
                }
        );

        /**
         * Returns auth urls for all available providers
         */
        get(
                "/oauth2-urls",
                (req, res) -> {
                    return new ApiResponse(200, null, oAuthStrategyFactory.getAllAuthUrls());
                },
                new JsonTranformer()
        );
    }

}

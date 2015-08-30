package com.enthusiast94.social_auth_bootstrap.controllers;

import com.enthusiast94.social_auth_bootstrap.models.AccessToken;
import com.enthusiast94.social_auth_bootstrap.models.LinkedAccount;
import com.enthusiast94.social_auth_bootstrap.models.User;
import com.enthusiast94.social_auth_bootstrap.oauth_strategies.OAuthStrategy;
import com.enthusiast94.social_auth_bootstrap.oauth_strategies.OAuthStrategyFactory;
import com.enthusiast94.social_auth_bootstrap.services.AccessTokenService;
import com.enthusiast94.social_auth_bootstrap.services.LinkedAccountService;
import com.enthusiast94.social_auth_bootstrap.services.UserService;
import com.enthusiast94.social_auth_bootstrap.utils.ApiResponse;
import com.enthusiast94.social_auth_bootstrap.utils.Helpers;
import com.enthusiast94.social_auth_bootstrap.utils.JsonTranformer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by ManasB on 7/28/2015.
 */

public class UserController {

    private UserService userService;
    private AccessTokenService accessTokenService;
    private LinkedAccountService linkedAccountService;
    private OAuthStrategyFactory oAuthStrategyFactory;

    public UserController(UserService userService, AccessTokenService accessTokenService,
                          LinkedAccountService linkedAccountService, OAuthStrategyFactory oAuthStrategyFactory) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;
        this.linkedAccountService = linkedAccountService;
        this.oAuthStrategyFactory = oAuthStrategyFactory;
    }

    public void setupEndpoints() {

        /**
         * Create new user
         */
        post(
                "/users/create",
                (req, res) -> {
                    HashMap<String, String> bodyParams = Helpers.bodyParams(req.body());
                    String email = bodyParams.get("email");
                    String name = bodyParams.get("name");
                    String password = bodyParams.get("password");

                    String emailError = userService.validateEmail(email);
                    if (emailError != null) {
                        return new ApiResponse(500, emailError, null);
                    }

                    String nameError = userService.validateName(name);
                    if (nameError != null) {
                        return new ApiResponse(500, nameError, null);
                    }

                    String passwordError = userService.validatePassword(password);
                    if (passwordError != null) {
                        return new ApiResponse(500, passwordError, null);
                    }

                    User user = userService.createUser(email, name, Helpers.getGravatar(email), password);

                    AccessToken accessToken = accessTokenService.createAccessToken(user.getId());

                    // prepare response
                    HashMap<String, Object> responseMap = new HashMap<>();
                    responseMap.put("accessToken", accessToken.getValue());
                    responseMap.put("userId", accessToken.getUserId());

                    return new ApiResponse(200, null, responseMap);
                },
                new JsonTranformer()
        );


        /**
         * [REQUIRES AUTHENTICATION]
         *
         * Updates requested user with the provided details
         */
        post(
                "/users/update/:id",
                (req, res) -> {
                    Helpers.requireAuthentication(req, accessTokenService);

                    // check if user id mapped to the provided access token matches the requested user id
                    AccessToken accessToken = (AccessToken) req.attribute("accessToken");
                    User user = userService.getUserById(accessToken.getUserId());

                    if (!user.getId().equals(req.params("id"))) return new ApiResponse(401, "Invalid access token", null);

                    // validate provided details and update user
                    HashMap<String, String> bodyParams = Helpers.bodyParams(req.body());
                    String email = bodyParams.get("email");
                    String name = bodyParams.get("name");
                    String password = bodyParams.get("password");

                    // Only validate email if it not null AND not the same as the current email address.
                    // This makes sure that the check to ensure that no other users have the same email address
                    // is not performed
                    if (email != null && !email.equals(user.getEmail())) {
                        String emailError = userService.validateEmail(email);
                        if (emailError != null) {
                            return new ApiResponse(500, emailError, null);
                        }

                        user.setEmail(email);
                    }

                    if (name != null) {
                        String nameError = userService.validateName(name);
                        if (nameError != null) {
                            return new ApiResponse(500, nameError, null);
                        }

                        user.setName(name);
                    }

                    if (password != null) {
                        String passwordError = userService.validatePassword(password);
                        if (passwordError != null) {
                            return new ApiResponse(500, passwordError, null);
                        }

                        user.setPassword(userService.hashPassword(password));
                    }

                    userService.updateUser(user);

                    return new ApiResponse(200, null, null);
                },
                new JsonTranformer()
        );


        /**
         * [REQUIRES AUTHENTICATION]
         *
         * Returns requested user
         */
        get(
                "/users/:id",
                (req, res) -> {
                    Helpers.requireAuthentication(req, accessTokenService);

                    User requestedUser = userService.getUserById(req.params("id"));

                    if (requestedUser == null)
                        return new ApiResponse(404, "User with id '" + req.params("id") + "' not found", null);

                    // prepare response
                    Map<String, Object> responseMap = new LinkedHashMap<>();
                    responseMap.put("id", requestedUser.getId());
                    responseMap.put("email", requestedUser.getEmail());
                    responseMap.put("name", requestedUser.getName());
                    responseMap.put("avatar", requestedUser.getAvatar());

                    // if user id mapped to the provided access token matches the requested user id, then attach some
                    // extra information to the response
                    AccessToken accessToken = (AccessToken) req.attribute("accessToken");
                    User userMappedToAccessToken = userService.getUserById(accessToken.getUserId());
                    if (userMappedToAccessToken.getId().equals(req.params("id"))) {
                        List<LinkedAccount> linkedAccounts = linkedAccountService.getLinkedAccountsByUserId(requestedUser.getId());
                        linkedAccounts.forEach((linkedAccount -> {
                            linkedAccount.setUserId(null);
                            linkedAccount.setId(null);
                            linkedAccount.setAccessToken(null);
                        }));
                        responseMap.put("linkedAccounts", linkedAccounts);
                    }

                    return new ApiResponse(200, null, responseMap);
                },
                new JsonTranformer()
        );


        /**
         * [REQUIRES AUTHENTICATION]
         *
         * Deletes requested user
         */
        post(
                "/users/destroy/:id",
                (req, res) -> {
                    Helpers.requireAuthentication(req, accessTokenService);

                    // check if user id mapped to the provided access token matches the requested user id
                    AccessToken accessToken = (AccessToken) req.attribute("accessToken");
                    User user = userService.getUserById(accessToken.getUserId());

                    if (!user.getId().equals(req.params("id"))) return new ApiResponse(401, "Invalid access token", null);

                    // delete currently authenticated user's access token
                    accessTokenService.deleteAccessToken(accessToken);

                    // delete currently authenticated user's linked accounts
                    linkedAccountService.getLinkedAccountsByUserId(user.getId()).forEach((linkedAccount ->
                                    linkedAccountService.deleteLinkedAccount(linkedAccount))
                    );

                    // delete currently authenticated user
                    userService.deleteUser(user);

                    return new ApiResponse(200, null, null);
                },
                new JsonTranformer()
        );


        /**
         * Authenticates requested user
         */
        post(
                "/auth",
                (req, res) -> {
                    HashMap<String, String> bodyParams = Helpers.bodyParams(req.body());
                    String email = bodyParams.get("email");
                    String password = bodyParams.get("password");

                    if (email == null) {
                        return new ApiResponse(500, "Email is required", null);
                    }

                    if (password == null) {
                        return new ApiResponse(500, "Password is required", null);
                    }

                    // check if user who is trying to authenticate exists
                    User requestedUser = userService.getUserByEmail(email);
                    if (requestedUser == null) return new ApiResponse(401, "Incorrect credentials", null);

                    // check if the sent password matches the stored password hash
                    if (!userService.doesPasswordMatch(password, requestedUser.getPassword()))
                        return new ApiResponse(401, "Incorrect credentials", null);

                    // if an access token already exists, simply return it, else create a new one
                    AccessToken accessToken = accessTokenService.getAccessTokenByUserId(requestedUser.getId());
                    if (accessToken == null) {
                        accessToken = accessTokenService.createAccessToken(requestedUser.getId());
                    }

                    // prepare response
                    HashMap<String, Object> responseMap = new HashMap<>();
                    responseMap.put("accessToken", accessToken.getValue());
                    responseMap.put("userId", accessToken.getUserId());

                    return new ApiResponse(200, null, responseMap);
                },
                new JsonTranformer()
        );


        /**
         * [REQUIRES AUTHENTICATION]
         *
         * Deletes supplied access token
         */
        post(
                "/deauth",
                (req, res) -> {
                    Helpers.requireAuthentication(req, accessTokenService);

                    AccessToken accessTokenToDelete = (AccessToken) req.attribute("accessToken");
                    accessTokenService.deleteAccessToken(accessTokenToDelete);

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

        /**
         * [REQUIRES AUTHENTICATION]
         *
         * Unlinks the requested account for currently authenticated user
         */
        post(
                "linked-accounts/destroy/:providerName",
                (req, res) -> {
                    Helpers.requireAuthentication(req, accessTokenService);

                    String provderName = req.params("providerName");
                    if (provderName == null)
                        return new ApiResponse(500, "providerName is required", null);

                    AccessToken accessToken = (AccessToken) req.attribute("accessToken");
                    List<LinkedAccount> linkedAccounts = linkedAccountService.getLinkedAccountsByUserId(accessToken.getUserId());

                    boolean found = false;

                    for (LinkedAccount linkedAccount : linkedAccounts) {
                        if (linkedAccount.getProviderName().equals(provderName)) {
                            linkedAccountService.deleteLinkedAccount(linkedAccount);

                            found = true;
                        }
                    }

                    if (!found)
                        return new ApiResponse(500, "No linked account with provider name '" + provderName + "' found", null);

                    return new ApiResponse(200, null, null);
                },
                new JsonTranformer()
        );
    }
}

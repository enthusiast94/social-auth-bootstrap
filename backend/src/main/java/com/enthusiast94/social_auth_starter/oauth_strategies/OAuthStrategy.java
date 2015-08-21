package com.enthusiast94.social_auth_starter.oauth_strategies;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.models.LinkedAccount;
import com.enthusiast94.social_auth_starter.models.User;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.LinkedAccountService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.Helpers;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ManasB on 8/1/2015.
 */
public abstract class OAuthStrategy {

    protected static String SERVER_REDIRECT_URI_BASE = "http://localhost:3000/oauth2-callback";
    protected static final String CLIENT_REDIRECT_URI = "http://localhost:4000/#oauth2-callback";
    protected static final String USER_PASSWORD = "this is a secret";

    protected UserService userService;
    protected AccessTokenService accessTokenService;
    protected LinkedAccountService linkedAccountService;

    public OAuthStrategy(UserService userService, AccessTokenService accessTokenService, LinkedAccountService linkedAccountService) {
        this.userService = userService;
        this.accessTokenService = accessTokenService;
        this.linkedAccountService = linkedAccountService;
    }

    public abstract String authorize(String code, String error) throws Exception;

    public abstract String getAuthUrl();

    protected abstract HashMap<String, String> parseAccessToken(String response);

    protected abstract HashMap<String, String> parseUserInfo(String response);

    protected AccessToken generateAccessToken(String providerName, String providerAccessToken, String email, String name, String avatar) {
        AccessToken accessToken;

        List<LinkedAccount> linkedAccounts = linkedAccountService.getLinkedAccountsByEmail(email);

        if (linkedAccounts.size() > 0) {
            User user = userService.getUserById(linkedAccounts.get(0).getUserId());

            boolean alreadyLinkedWithSameProvider = false;

            for (int i=0; i<linkedAccounts.size(); i++) {
                if (linkedAccounts.get(i).getProviderName().equals(providerName)) {
                    alreadyLinkedWithSameProvider = true;
                    break;
                }
            }

            if (!alreadyLinkedWithSameProvider) {
                linkedAccountService.createLinkedAccount(user.getId(), providerName, providerAccessToken, name, email);

                // update user avatar
                if (avatar != null) {
                    user.setAvatar(avatar);
                    userService.updateUser(user);
                }
            }

            // if an access token already exists, simply return it, else create a new one
            accessToken = accessTokenService.getAccessTokenByUserId(user.getId());
            if (accessToken == null) {
                accessToken = accessTokenService.createAccessToken(user.getId());
            }
        } else {
            // Since there are no linked accounts, there are two possibilities: either the user doesn't exist, or
            // the user authenticated without using any oauth2 strategy.
            User user2 = userService.getUserByEmail(email);

            if (user2 != null) {
                // update user avatar
                if (avatar != null) {
                    user2.setAvatar(avatar);
                    userService.updateUser(user2);
                }

                // if an access token already exists, simply return it, else create a new one
                accessToken = accessTokenService.getAccessTokenByUserId(user2.getId());
                if (accessToken == null) {
                    accessToken = accessTokenService.createAccessToken(user2.getId());
                }
            } else {
                if (avatar != null) {
                    user2 = userService.createUser(email, name, avatar, USER_PASSWORD);
                } else {
                    user2 = userService.createUser(email, name, Helpers.getGravatar(email), USER_PASSWORD);
                }

                accessToken = accessTokenService.createAccessToken(user2.getId());
            }

            linkedAccountService.createLinkedAccount(user2.getId(), providerName, providerAccessToken, name, email);
        }

        return accessToken;
    }
}

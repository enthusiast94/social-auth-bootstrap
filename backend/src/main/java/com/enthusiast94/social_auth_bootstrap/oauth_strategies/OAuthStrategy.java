package com.enthusiast94.social_auth_bootstrap.oauth_strategies;

import com.enthusiast94.social_auth_bootstrap.models.AccessToken;
import com.enthusiast94.social_auth_bootstrap.models.LinkedAccount;
import com.enthusiast94.social_auth_bootstrap.models.User;
import com.enthusiast94.social_auth_bootstrap.services.AccessTokenService;
import com.enthusiast94.social_auth_bootstrap.services.LinkedAccountService;
import com.enthusiast94.social_auth_bootstrap.services.UserService;
import com.enthusiast94.social_auth_bootstrap.utils.Helpers;
import com.enthusiast94.social_auth_bootstrap.utils.OauthCredentialsParser;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ManasB on 8/1/2015.
 */
public abstract class OAuthStrategy {

    protected OauthCredentialsParser oauthCredentialsParser;
    protected UserService userService;
    protected AccessTokenService accessTokenService;
    protected LinkedAccountService linkedAccountService;

    public OAuthStrategy(OauthCredentialsParser oauthCredentialsParser, UserService userService,
                         AccessTokenService accessTokenService, LinkedAccountService linkedAccountService) {
        this.oauthCredentialsParser = oauthCredentialsParser;
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
        User user;

        List<LinkedAccount> linkedAccounts = linkedAccountService.getLinkedAccountsByEmail(email);

        if (linkedAccounts.size() > 0) {
            user = userService.getUserById(linkedAccounts.get(0).getUserId());

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
        } else {
            // Since there are no linked accounts, there are two possibilities: either the user doesn't exist, or
            // the user authenticated without using any oauth2 strategy.
            user = userService.getUserByEmail(email);

            if (user != null) {
                // update user avatar
                if (avatar != null) {
                    user.setAvatar(avatar);
                    userService.updateUser(user);
                }

            } else {
                if (avatar != null) {
                    user = userService.createUser(email, name, avatar, oauthCredentialsParser.getDefaultUserPassword());
                } else {
                    user = userService.createUser(email, name, Helpers.getGravatar(email), oauthCredentialsParser.getDefaultUserPassword());
                }
            }

            linkedAccountService.createLinkedAccount(user.getId(), providerName, providerAccessToken, name, email);
        }

        // create new access token
        accessToken = accessTokenService.createAccessToken(user.getId());

        return accessToken;
    }
}

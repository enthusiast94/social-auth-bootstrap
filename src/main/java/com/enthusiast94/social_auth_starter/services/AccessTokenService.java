package com.enthusiast94.social_auth_starter.services;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.models.User;
import org.mongodb.morphia.Datastore;

/**
 * Created by ManasB on 7/28/2015.
 */

public class AccessTokenService {

    Datastore db;

    public AccessTokenService(Datastore db) {
        this.db = db;
    }

    public AccessToken createAccessToken(User user) {
        AccessToken accessToken = new AccessToken(user);
        db.save(accessToken);

        return accessToken;
    }

    public User getAccessTokenUser(String accessTokenString) {
        return db.createQuery(AccessToken.class)
                .field("accessToken").equal(accessTokenString)
                .retrievedFields(true, "user")
                .get()
                .getUser();
    }

    public boolean isAccessTokenValid(String accessTokenString) {
        AccessToken accessToken = db.createQuery(AccessToken.class)
                .field("accessToken").equal(accessTokenString)
                .retrievedFields(true, "createdAt", "expiresIn")
                .get();

        if (accessToken != null) {
            if (((System.currentTimeMillis() / 1000) - accessToken.getCreatedAt()) <= accessToken.getExpiresIn()) {
                return true;
            }
        }

        return false;
    }
}

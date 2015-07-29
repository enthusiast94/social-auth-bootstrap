package com.enthusiast94.social_auth_starter.services;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import org.mongodb.morphia.Datastore;

/**
 * Created by ManasB on 7/28/2015.
 */

public class AccessTokenService {

    Datastore db;

    public AccessTokenService(Datastore db) {
        this.db = db;
    }

    public AccessToken createAccessToken(String userId) {
        AccessToken accessToken = new AccessToken(userId);
        db.save(accessToken);

        return accessToken;
    }

    public String getAccessTokenUserId(String accessTokenString) {
        return db.createQuery(AccessToken.class)
                .field("accessToken").equal(accessTokenString)
                .retrievedFields(true, "userId")
                .get()
                .getUserId();
    }

    public AccessToken getAccessTokenByUserId(String userId) {
        return db.createQuery(AccessToken.class)
                .field("userId").equal(userId)
                .get();
    }

    public void deleteAccessToken(AccessToken accessTokenToDelete) {
        db.delete(accessTokenToDelete);
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

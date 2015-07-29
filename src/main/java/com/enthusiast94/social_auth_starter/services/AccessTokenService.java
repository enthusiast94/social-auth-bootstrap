package com.enthusiast94.social_auth_starter.services;

import com.enthusiast94.social_auth_starter.models.AccessToken;
import org.mongodb.morphia.Datastore;
import spark.Access;

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

    public AccessToken getAccessTokenByAccessTokenString(String accessTokenString) {
        return db.createQuery(AccessToken.class)
                .field("accessToken").equal(accessTokenString)
                .get();
    }

    public AccessToken getAccessTokenByUserId(String userId) {
        return db.createQuery(AccessToken.class)
                .field("userId").equal(userId)
                .get();
    }

    public void deleteAccessToken(AccessToken accessTokenToDelete) {
        db.delete(accessTokenToDelete);
    }
}

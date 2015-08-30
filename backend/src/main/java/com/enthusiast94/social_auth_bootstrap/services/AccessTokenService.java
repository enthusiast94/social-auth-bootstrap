package com.enthusiast94.social_auth_bootstrap.services;

import com.enthusiast94.social_auth_bootstrap.models.AccessToken;
import org.mongodb.morphia.Datastore;

import java.util.List;

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

    public AccessToken getAccessTokenByValue(String value) {
        return db.createQuery(AccessToken.class)
                .field("value").equal(value)
                .get();
    }

    public List<AccessToken> getAccessTokensByUserId(String userId) {
        return db.createQuery(AccessToken.class)
                .field("userId").equal(userId)
                .asList();
    }

    public void deleteAccessToken(AccessToken accessTokenToDelete) {
        db.delete(accessTokenToDelete);
    }
}

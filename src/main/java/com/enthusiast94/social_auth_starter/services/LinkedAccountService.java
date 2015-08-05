package com.enthusiast94.social_auth_starter.services;

import com.enthusiast94.social_auth_starter.models.LinkedAccount;
import org.mongodb.morphia.Datastore;

import java.util.List;

/**
 * Created by manas on 05-08-2015.
 */
public class LinkedAccountService {

    private Datastore db;

    public LinkedAccountService(Datastore db) {
        this.db = db;
    }

    public LinkedAccount createLinkedAccount(String userId, String providerName, String accessToken, String userName, String userEmail) {
        LinkedAccount linkedAccount = new LinkedAccount(userId, providerName, accessToken, userName, userEmail);
        db.save(linkedAccount);

        return linkedAccount;
    }

    public List<LinkedAccount> getLinkedAccountsByEmail(String email) {
        return db.createQuery(LinkedAccount.class)
                .field("userEmail").equal(email)
                .asList();
    }

    public List<LinkedAccount> getLinkedAccountsByUserId(String userId) {
        return db.createQuery(LinkedAccount.class)
                .field("userId").equal(userId)
                .asList();
    }


    public void deleteLinkedAccount(LinkedAccount linkedAccount) {
        db.delete(linkedAccount);
    }
}

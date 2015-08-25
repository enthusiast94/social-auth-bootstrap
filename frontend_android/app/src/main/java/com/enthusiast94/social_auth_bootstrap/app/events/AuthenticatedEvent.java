package com.enthusiast94.social_auth_bootstrap.app.events;

/**
 * Created by manas on 25-08-2015.
 */
public class AuthenticatedEvent {
    private String userName;

    public AuthenticatedEvent(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}

package com.enthusiast94.social_auth_starter;

/**
 * Created by ManasB on 7/28/2015.
 */

import com.enthusiast94.social_auth_starter.controllers.UserController;
import com.enthusiast94.social_auth_starter.models.AccessToken;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.enthusiast94.social_auth_starter.utils.ApiResponse;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import spark.Request;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.SparkBase.port;

public class Main {

    public static void main (String[] args) {

        /**
         * Configure spark
         */

        port(3000);


        /**
         * Configure db
         */

        Morphia morphia = new Morphia();

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.mapPackage("com.enthusiast94.social_auth_starter.models");

        // create the Datastore connecting to the default port on the local host
        Datastore db = morphia.createDatastore(new MongoClient("localhost"), "social_auth_starter_db");
        db.ensureIndexes();


        /**
         * Setup endpoints
         */

        AccessTokenService accessTokenService = new AccessTokenService(db);
        UserService userService = new UserService(db);

        // set response type for all requests to json and enable CORS
        before((req, res) -> {
            res.type("application/json");

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Authorization");
            res.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        });

        // require authentication for specific requests
        before("/users/destroy/*", (req, res) -> requireAuthentication(req, accessTokenService));
        before("/deauth", (req, res) -> requireAuthentication(req, accessTokenService));

        new UserController(userService, accessTokenService).setupEndpoints();
    }


    /**
     * Helper method that checks if a valid access token is provided. Also appends the access token object to Request
     * attributes.
     */
    private static void requireAuthentication(Request req, AccessTokenService accessTokenService) {
        String authHeader = req.headers("Authorization");

        if (authHeader == null) {
            halt(new ApiResponse(401, "authorization header not found", null).toJson());
            return;
        }

        if (authHeader.length() < (36 /* Access Token is 36 characters long */ + "Token".length() + 1 /* SPACE after 'Token' */)) {
            halt(new ApiResponse(401, "invalid access token", null).toJson());
            return;
        }

        String accessTokenString = authHeader.substring("Token".length()+1, authHeader.length());
        AccessToken accessToken = accessTokenService.getAccessTokenByAccessTokenString(accessTokenString);

        if (accessToken == null || accessToken.hasExpired()) {
            halt(new ApiResponse(401, "invalid access token", null).toJson());
            return;
        }

        // add access token to attributes list so that it can be reused by other routes
        req.attribute("accessToken", accessToken);
    }
}

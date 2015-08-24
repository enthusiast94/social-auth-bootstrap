package com.enthusiast94.social_auth_starter;

/**
 * Created by ManasB on 7/28/2015.
 */

import com.enthusiast94.social_auth_starter.controllers.UserController;
import com.enthusiast94.social_auth_starter.services.AccessTokenService;
import com.enthusiast94.social_auth_starter.services.LinkedAccountService;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import static spark.Spark.before;
import static spark.Spark.options;
import static spark.SparkBase.port;

public class Main {

    public static void main (String[] args) {

        /**
         * Configure spark
         */

        port(3005);


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
         * Enable cross-origin resource sharing (CORS) and set content type for all responses to json
         */

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Authorization");
            res.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");

            res.type("application/json");
        });

        options("*", (req, res) -> {
            res.status(200);
            return "";
        });


        /**
         * Setup endpoints
         */

        AccessTokenService accessTokenService = new AccessTokenService(db);
        UserService userService = new UserService(db);
        LinkedAccountService linkedAccountService = new LinkedAccountService(db);

        new UserController(userService, accessTokenService, linkedAccountService).setupEndpoints();
    }
}

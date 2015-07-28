package com.enthusiast94.social_auth_starter;

/**
 * Created by ManasB on 7/28/2015.
 */

import com.enthusiast94.social_auth_starter.controllers.UserController;
import com.enthusiast94.social_auth_starter.services.UserService;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import spark.Spark;

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
         * Setup controllers
         */

        // set response type for all requests to json
        Spark.before((req, res) -> res.type("application/json"));

        new UserController(new UserService(db)).setupEndpoints();

    }
}

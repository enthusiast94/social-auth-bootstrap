/**
 * Created by ManasB on 7/28/2015.
 */

package com.enthusiast94.social_auth_starter;

import static spark.Spark.get;

public class Main {

    public static void main(String[] args) {

        get("/", ((req, res) -> "Hello World!"));
    }
}

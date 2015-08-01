/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var loginView = require("./views/login");
var meView = require("./views/me");
var authController = require("./auth_controller");

var AppRouter = Backbone.Router.extend({
    routes: {
        "me": "showMe",
        "login": "showLogin",
        "*any": "defaultAction"
    },
    showMe: function () {
        if (authController.getUserFromCache()) {
            meView.render();
        } else {
            this.navigate("login", true);
        }
    },
    showLogin: function () {
        if (!authController.getUserFromCache()) {
            loginView.render();
        } else {
            this.navigate("me", true);
        }
    },
    defaultAction: function () {
        this.navigate("me", true);
    }
});

module.exports = {
    init: function () {
        new AppRouter();

        Backbone.history.start();
    }
};

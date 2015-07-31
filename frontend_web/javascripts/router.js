/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var loginView = require("./views/login");
var meView = require("./views/me");

var AppRouter = Backbone.Router.extend({
    routes: {
        "me": "showMe",
        "login": "showLogin",
        "*any": "defaultAction"
    },
    showMe: function () {
        meView.render();
    },
    showLogin: function () {
        loginView.render();
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

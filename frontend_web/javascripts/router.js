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
        "oauth2-callback": "oauth2Callback",
        "*any": "defaultAction"
    },
    showMe: function () {
        if (authController.isAuthenticated()) {
            meView.render();
        } else {
            this.navigate("login", true);
        }
    },
    showLogin: function () {
        if (authController.isAuthenticated()) {
            this.navigate("me", true);
        } else {
            loginView.render();
        }
    },
    oauth2Callback: function () {
        var params = this.parseQueryParams(window.location.href);

        var self = this;

        if (!params.error) {
            authController.oauth({
                userId: params.userId,
                accessToken: params.accessToken,
                success: function () {
                    self.navigate("me", true);
                }
            });
        } else {
            alert(params.error);
        }

    },
    defaultAction: function () {
        this.navigate("me", true);
    },
    parseQueryParams: function (url) {
        url = url.substring(url.indexOf("?") + 1, url.length);
        var split = url.split("&");

        var params = {};
        split.forEach(function (param) {
            var split2 = param.split("=");
            params[split2[0]] = split2[1];
        });

        return params;
    }
});

module.exports = {
    init: function () {
        new AppRouter();

        Backbone.history.start();
    }
};

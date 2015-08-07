/**
 * Created by ManasB on 7/31/2015.
 */

window.$ = window.jQuery = require("jquery"); // needed in order to make bootstrap's javascript work

var Backbone = require("Backbone");
var $ = window.$;
var loginView = require("./views/login");
var meView = require("./views/me");
var navView = require("./views/nav");
var homeView = require("./views/home");
var createAccountView = require("./views/create_account");
var authController = require("./auth_controller");
var Notification = require("./views/notification");

var AppRouter = Backbone.Router.extend({
    routes: {
        "me": "showMe",
        "login": "showLogin",
        "create-account": "showCreateAccount",
        "home": "showHome",
        "logout": "logout",
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
            this.navigate("home", true);
        } else {
            loginView.render();
        }
    },
    showCreateAccount: function () {
        if (authController.isAuthenticated()) {
            this.navigate("home", true);
        } else {
            createAccountView.render();
        }
    },
    showHome: function () {
        homeView.render();
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

                    $(document).trigger("authenticated");

                    new Notification({
                        $container: $("#notifications"),
                        message: "Logged in as: <strong>" + authController.getUserFromCache().userName + "</strong>",
                        style: "info"
                    }).notify("show");
                }
            });
        } else {
            new Notification({
                $container: $("#notifications"),
                message: "<strong>Error! </strong>" + params.error,
                style: "danger"
            }).notify("show");
        }

    },
    logout: function () {
        if (authController.isAuthenticated()) {
            authController.deauth({
                success: function () {
                    Backbone.history.navigate("home", {trigger: true});

                    $(document).trigger("deauthenticated");
                },
                error: function (error) {
                    new Notification({
                        $container: $("#notifications"),
                        message: "<strong>Error! </strong>" + error,
                        style: "danger"
                    }).notify("show");;
                }
            });
        } else {
            this.navigate("login", true);
        }
    },
    defaultAction: function () {
        this.navigate("home", true);
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
        navView.render();

        new AppRouter();

        Backbone.history.start();
    }
};

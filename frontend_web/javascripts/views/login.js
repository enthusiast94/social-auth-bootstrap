/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("backbone");
var $ = require("jquery");
var authController = require("../network/auth_controller");
var swig = require("swig");
var Notification = require("../utils/notification");

var LoginView = Backbone.View.extend({
    el: "#content",
    template: $("#login-template").html(),
    events: {
        "submit #login-form": "login"
    },
    render: function () {
        var self = this;

        authController.getAllOauth2Urls({
            success: function (authUrls) {
                var compiledTemplate = swig.render(self.template, {locals: {authUrls: authUrls}});
                self.$el.html(compiledTemplate);

                self.$loginButton = $("#login-submit-button");
                self.$loginEmailInput = $("#login-email-input");
                self.$loginPasswordInput = $("#login-password-input");
            },
            error: function (error) {
                alert(error);
            }
        });
    },
    login: function (event) {
        event.preventDefault();

        this.$loginButton.text("Loading...");
        this.$loginButton.attr("disabled", true);

        var email = this.$loginEmailInput.val().trim();
        var password = this.$loginPasswordInput.val().trim();

        var self = this;
        authController.basicAuth({
            type: "existing",
            data: {
                email: email,
                password: password
            },
            success: function () {
                self.$loginButton.text("Login");
                self.$loginButton.attr("disabled", false);

                Backbone.history.navigate("me", {trigger: true});

                $(document).trigger("authenticated");

                new Notification({
                    $container: $("#notifications"),
                    message: "Logged in as: <strong>" + authController.getUserFromCache().userName + "</strong>",
                    style: "info"
                }).notify("show");
            },
            error: function (error) {
                self.$loginButton.text("Login");
                self.$loginButton.attr("disabled", false);

                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Error! </strong>" + error,
                    style: "danger"
                }).notify("show");
            }
        });
    }
});

module.exports = new LoginView();


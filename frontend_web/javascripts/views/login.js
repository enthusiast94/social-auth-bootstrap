/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var authController = require("../auth_controller");
var swig = require("swig");

var LoginView = Backbone.View.extend({
    el: "#content",
    template: $("#login-template").html(),
    events: {
        "submit #login-form": "login",
        "submit #create-account-form": "createAccount"
    },
    render: function () {
        var self = this;

        authController.getAllOauth2Urls({
            success: function (authUrls) {
                var compiledTemplate = swig.render(self.template, {locals: {authUrls: authUrls}});
                self.$el.html(compiledTemplate);

                self.$loginUsernameInput = $("#login-username-input");
                self.$loginPasswordInput = $("#login-password-input");
                self.$createAccountUsernameInput = $("#create-account-username-input");
                self.$createAccountPasswordInput = $("#create-account-password-input");
                self.$createAccountConfimPasswordInput = $("#create-account-confirm-password-input");
            },
            error: function (error) {
                alert(error);
            }
        });
    },
    login: function (event) {
        event.preventDefault();

        var username = this.$loginUsernameInput.val().trim();
        var password = this.$loginPasswordInput.val().trim();

        if (username.length == 0 || password.length == 0) {
            alert("All fields must be filled");
        } else {
            authController.basicAuth({
                type: "existing",
                username: username,
                password: password,
                success: function () {
                    Backbone.history.navigate("me", {trigger: true});
                },
                error: function (error) {
                    alert(error);
                }
            });
        }
    },
    createAccount: function (event) {
        event.preventDefault();

        var username = this.$createAccountUsernameInput.val().trim();
        var password = this.$createAccountPasswordInput.val().trim();
        var confirmedPassword = this.$createAccountConfimPasswordInput.val().trim();

        if (username.length == 0 || password.length == 0 || confirmedPassword.length == 0) {
            alert("All fields must be filled");
        } else if (password != confirmedPassword) {
            alert("Passwords do not match");
        } else {
            authController.basicAuth({
                type: "new",
                username: username,
                password: password,
                success: function () {
                    Backbone.history.navigate("me", {trigger: true});
                },
                error: function (error) {
                    alert(error);
                }
            });
        }
    }
});

module.exports = new LoginView();


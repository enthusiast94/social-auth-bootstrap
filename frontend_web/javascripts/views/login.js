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
        "submit #login-form": "login"
    },
    render: function () {
        var compiledTemplate = swig.render(this.template);
        this.$el.html(compiledTemplate);

        this.$loginUsernameInput = $("#login-username-input");
        this.$loginPasswordInput = $("#login-password-input");
    },
    login: function (event) {
        event.preventDefault();

        var username = this.$loginUsernameInput.val().trim();
        var password = this.$loginPasswordInput.val().trim();

        if (username.length == 0 || password.length == 0) {
            alert("Username and password are both required");
        } else {
            authController.basicAuth({
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


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
        "click #submit-button": "submit"
    },
    render: function () {
        var compiledTemplate = swig.render(this.template);
        this.$el.html(compiledTemplate);

        this.$usernameInput = $("#username-input");
        this.$passwordInput = $("#password-input");
    },
    submit: function () {
        var username = this.$usernameInput.val().trim();
        var password = this.$passwordInput.val().trim();

        if (username.length == 0 || password.length == 0) {
            alert("username and password are both required fields");
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


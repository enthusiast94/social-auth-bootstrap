/**
 * Created by manas on 03-08-2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var swig = require("swig");
var authController = require("../auth_controller");

var CreateAccountView = Backbone.View.extend({
    el: "#content",
    template: $("#create-account-template").html(),
    events: {
        "submit #create-account-form": "createAccount"
    },
    render: function () {
        var compiledTemplate = swig.render(this.template);
        this.$el.html(compiledTemplate);

        this.$createAccountUsernameInput = $("#create-account-username-input");
        this.$createAccountPasswordInput = $("#create-account-password-input");
        this.$createAccountConfimPasswordInput = $("#create-account-confirm-password-input");
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

                    $(document).trigger("authenticated");
                },
                error: function (error) {
                    alert(error);
                }
            });
        }
    }
});

module.exports = new CreateAccountView();
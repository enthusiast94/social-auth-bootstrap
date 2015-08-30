/**
 * Created by manas on 03-08-2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var swig = require("swig");
var authController = require("../network/auth_controller");
var Notification = require("../utils/notification");

var CreateAccountView = Backbone.View.extend({
    el: "#content",
    template: $("#create-account-template").html(),
    events: {
        "submit #create-account-form": "createAccount"
    },
    render: function () {
        var compiledTemplate = swig.render(this.template);
        this.$el.html(compiledTemplate);

        this.$createAccountEmailInput = $("#create-account-email-input");
        this.$createAccountNameInput = $("#create-account-name-input");
        this.$createAccountPasswordInput = $("#create-account-password-input");
        this.$createAccountConfimPasswordInput = $("#create-account-confirm-password-input");
    },
    createAccount: function (event) {
        event.preventDefault();

        var email = this.$createAccountEmailInput.val().trim();
        var name = this.$createAccountNameInput.val().trim();
        var password = this.$createAccountPasswordInput.val().trim();
        var confirmedPassword = this.$createAccountConfimPasswordInput.val().trim();

        if (password != confirmedPassword) {
            new Notification({
                $container: $("#notifications"),
                message: "<strong>Error! </strong> Passwords do not match.",
                style: "danger"
            }).notify("show");
        } else {
            authController.basicAuth({
                type: "new",
                data: {
                    email: email,
                    name: name,
                    password: password
                },
                success: function () {
                    Backbone.history.navigate("me", {trigger: true});

                    $(document).trigger("authenticated");

                    new Notification({
                        $container: $("#notifications"),
                        message: "Logged in as: <strong>" + authController.getUserFromCache().userName + "</strong>",
                        style: "info"
                    }).notify("show");
                },
                error: function (error) {
                    new Notification({
                        $container: $("#notifications"),
                        message: "<strong>Error! </strong>" + error,
                        style: "danger"
                    }).notify("show");
                }
            });
        }
    }
});

module.exports = new CreateAccountView();
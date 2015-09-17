/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("backbone");
var $ = require("jquery");
var swig = require("swig");
var authController = require("../network/auth_controller");
var Notification = require("../utils/notification");

var MeView = Backbone.View.extend({
    el: "#content",
    template: $("#me-template").html(),
    events: {
        "submit #update-account-form": "updateAccount",
        "submit #change-password-form": "updateAccount",
        "click .unlink-button": "unlinkAccount",
        "click #delete-account-button": "deleteAccount"
    },
    render: function () {
        var self = this;

        authController.getUser({
            success: function (user) {
                var compiledTemplate = swig.render(self.template, {locals: {user: user}});
                self.$el.html(compiledTemplate);

                self.$updateAccountButton = $("#update-account-button");
                self.$changePasswordButton = $("#change-password-button");
                self.$updateAccountEmailInput = $("#update-account-email-input");
                self.$updateAccountNameInput = $("#update-account-name-input");
                self.$changePasswordNewPasswordInput = $("#change-password-new-password-input");
                self.$changePasswordConfirmPasswordInput = $("#change-password-confirm-password-input");
                self.$deleteAccountButton = $("#delete-account-button");
            },
            error: function (error) {
                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Error! </strong>" + error,
                    style: "danger"
                }).notify("show");
            }
        });
    },
    updateAccount: function (event) {
        event.preventDefault();

        var data;
        if (event.target.id == "update-account-form") {
            this.$updateAccountButton.text("Loading...");
            this.$updateAccountButton.attr("disabled", true);

            data = {
                email: this.$updateAccountEmailInput.val().trim(),
                name: this.$updateAccountNameInput.val().trim()
            };
        } else if (event.target.id == "change-password-form") {
            this.$changePasswordButton.text("Loading...");
            this.$changePasswordButton.attr("disabled", true);

            var newPassword = this.$changePasswordNewPasswordInput.val().trim();
            var confirmPassword = this.$changePasswordConfirmPasswordInput.val().trim();

            if (newPassword != confirmPassword) {
                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Error! </strong> Passwords do not match!",
                    style: "danger"
                }).notify("show");
                return;
            } else {
                data = {
                    password: newPassword
                }
            }
        }

        var self = this;

        authController.updateAccount({
            data: data,
            success: function () {
                self.$updateAccountButton.text("Update account");
                self.$changePasswordButton.text("Change password");
                self.$updateAccountButton.attr("disabled", false);
                self.$changePasswordButton.attr("disabled", false);

                Backbone.history.loadUrl();

                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Success! </strong>Account information successfully updated.",
                    style: "success"
                }).notify("show");
            },
            error: function (error) {
                self.$updateAccountButton.text("Update account");
                self.$changePasswordButton.text("Change password");

                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Error! </strong>" + error,
                    style: "danger"
                }).notify("show");
            }
        });
    },
    unlinkAccount: function (event) {
        var $unlinkButton = $(event.target);

        var canUnlink = false;

        // display a confirmation dialog if the last linked account is being unlinked
        if ($(".unlink-button").length == 1) {
            canUnlink = confirm("Note that since this is your last linked account, you would not be able to " +
                "log back into your account if you created your account via an external provider (such as Google), " +
                "unless you change your password before logging out.");
        } else {
            canUnlink = true;
        }

        if (canUnlink) {
            $unlinkButton.text("Loading...");
            $unlinkButton.attr("disabled", true);

            var providerName = $(event.target).attr("data-provider-name");

            authController.unlinkAccount({
                providerName: providerName,
                success: function () {
                    $unlinkButton.text("Unlink");
                    $unlinkButton.attr("disabled", false);

                    Backbone.history.loadUrl();

                    new Notification({
                        $container: $("#notifications"),
                        message: "<strong>Success! </strong>" + providerName + " account successfully unlinked.",
                        style: "success"
                    }).notify("show");
                },
                error: function (error) {
                    $unlinkButton.text("Unlink");

                    new Notification({
                        $container: $("#notifications"),
                        message: "<strong>Error! </strong>" + error,
                        style: "danger"
                    }).notify("show");
                }
            });
        }
    },
    deleteAccount: function () {
        this.$deleteAccountButton.text("Loading...");
        this.$deleteAccountButton.attr("disabled", true);

        var self = this;
        authController.deleteAccount({
            success: function () {
                self.$deleteAccountButton.text("Delete my account");
                self.$deleteAccountButton.attr("disabled", false);

                Backbone.history.navigate("home", {trigger: true});

                $(document).trigger("deauthenticated");

                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Success! </strong> Account successfully deleted.",
                    style: "success"
                }).notify("show");
            },
            error: function (error) {
                self.$deleteAccountButton.text("Delete my account");
                self.$deleteAccountButton.attr("disabled", false);

                new Notification({
                    $container: $("#notifications"),
                    message: "<strong>Error! </strong>" + error,
                    style: "danger"
                }).notify("show");
            }
        });
    }
});

module.exports = new MeView();


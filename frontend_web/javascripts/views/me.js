/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var swig = require("swig");
var authController = require("../auth_controller");

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

                self.$updateAccountEmailInput = $("#update-account-email-input");
                self.$updateAccountNameInput = $("#update-account-name-input");
                self.$changePasswordNewPasswordInput = $("#change-password-new-password-input");
                self.$changePasswordConfirmPasswordInput = $("#change-password-confirm-password-input");
            },
            error: function (error) {
                alert(error);
            }
        });
    },
    updateAccount: function (event) {
        event.preventDefault();

        var data;
        if (event.target.id == "update-account-form") {
            data = {
                email: this.$updateAccountEmailInput.val().trim(),
                name: this.$updateAccountNameInput.val().trim()
            };
        } else if (event.target.id == "change-password-form") {
            var newPassword = this.$changePasswordNewPasswordInput.val().trim();
            var confirmPassword = this.$changePasswordConfirmPasswordInput.val().trim();

            if (newPassword != confirmPassword) {
                alert("Passwords do not match");
                return;
            } else {
                data = {
                    password: newPassword
                }
            }
        }

        authController.updateAccount({
            data: data,
            success: function () {
                Backbone.history.loadUrl();
            },
            error: function (error) {
                alert(error);
            }
        });
    },
    unlinkAccount: function (event) {
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
            var providerName = $(event.target).attr("data-provider-name");

            authController.unlinkAccount({
                providerName: providerName,
                success: function () {
                    Backbone.history.loadUrl();
                },
                error: function (error) {
                    alert(error);
                }
            });
        }
    },
    deleteAccount: function () {
        authController.deleteAccount({
            success: function () {
                Backbone.history.navigate("home", {trigger: true});

                $(document).trigger("deauthenticated");
            },
            error: function (error) {
                alert(error)
            }
        });
    }
});

module.exports = new MeView();


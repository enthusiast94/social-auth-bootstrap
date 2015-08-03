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
        "click #logout-button": "logout"
    },
    render: function () {
        var self = this;

        authController.getUser({
            success: function (user) {
                var compiledTemplate = swig.render(self.template, {locals: {user: user}});
                self.$el.html(compiledTemplate);
            },
            error: function (error) {
                alert(error);
            }
        });
    },
    logout: function () {
        authController.deauth({
            success: function () {
                Backbone.history.navigate("login", {trigger: true});
            }
        });
    }
});

module.exports = new MeView();


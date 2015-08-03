/**
 * Created by manas on 03-08-2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");
var swig = require("swig");
var authController = require("../auth_controller");

var NavView = Backbone.View.extend({
    el: "#nav",
    template: $("#nav-template").html(),
    initialize: function () {
        var self = this;

        $(document).on("authenticated", function () {
            self.render.call(self);
        });
        $(document).on("deauthenticated", function () {
            self.render.call(self);
        });

    },
    events: {

    },
    render: function () {
        var compiledTemplate = swig.render(this.template, {locals: {user: authController.getUserFromCache()}});
        this.$el.html(compiledTemplate);
    }
});

module.exports = new NavView();
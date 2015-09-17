/**
 * Created by manas on 03-08-2015.
 */

var Backbone = require("backbone");
var $ = require("jquery");
var swig = require("swig");
var authController = require("../network/auth_controller");

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

        $(window).on("hashchange", function () {
            self.selectActiveTab(window.location.hash);
        });
    },
    render: function () {
        var compiledTemplate = swig.render(this.template, {locals: {user: authController.getUserFromCache()}});
        this.$el.html(compiledTemplate);

        // select active tab before any hash change event occurs
        this.selectActiveTab(window.location.hash);
    },
    selectActiveTab: function (hash) {
        var self = this;

        var tabs = self.$el.find(".nav-tab");
        tabs.each(function (index) {
            var $tab = $(tabs[index]);

            if ($tab.attr("href") == hash) {
                $tab.parent("li").addClass("active");
            } else {
                $tab.parent("li").removeClass("active");
            }
        });
    }
});

module.exports = new NavView();
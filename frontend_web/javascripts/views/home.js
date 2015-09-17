/**
 * Created by manas on 03-08-2015.
 */

var Backbone = require("backbone");
var $ = require("jquery");
var swig = require("swig");

var HomeView = Backbone.View.extend({
    el: "#content",
    template: $("#home-template").html(),
    events: {

    },
    render: function () {
        var compiledTemplate = swig.render(this.template);
        this.$el.html(compiledTemplate);
    }
});

module.exports = new HomeView();
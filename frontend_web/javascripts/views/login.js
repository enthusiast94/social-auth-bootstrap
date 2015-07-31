/**
 * Created by ManasB on 7/31/2015.
 */

var Backbone = require("Backbone");
var $ = require("jquery");

var LoginView = Backbone.View.extend({
    initialize: function () {
        this.$content = $("#content");
    },
    template: $("#login-template").html(),
    render: function () {
        this.$content.html(this.template);
    }
});

module.exports = new LoginView();


/**
 * Created by manas on 07-08-2015.
 */

var Notification = function (options) {
    var settings = $.extend({
        duration: 3000,
        style: "info",
        hideOnClick: true
    }, options);

    var styles = {
        info: "alert-info",
        success: "alert-success",
        danger: "alert-danger"
    };

    var self = this;

    // create and append notification to provided container
    this.$notification = $($.parseHTML("<div class='notification alert " + styles[settings.style] + "'>" + settings.message + "</div>"));
    settings.$container.append(this.$notification);

    // hide notification on click if hideOnClick is set to true
    this.$notification.click(function () {
        if (settings.hideOnClick) {
            self.notify("hide");
        }
    });

    this.notify = function (action) {
        switch (action) {
            case "show":
                this.$notification.fadeIn();

                var self = this;
                setTimeout(function () {
                    self.$notification.fadeOut();
                }, settings.duration);
                break;

            case "hide":
                this.$notification.hide();
                break;

            default:
                throw new Error("Invalid action");
        }
    };
};


module.exports = Notification;

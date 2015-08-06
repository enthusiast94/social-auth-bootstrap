/**
 * Created by ManasB on 7/31/2015.
 */

var $ = require("jquery");
var API_BASE = "http://localhost:3000";

var authController = {
    basicAuth: function (options) {
        var self = this;

        var types = {
            "new": API_BASE + "/users/create",
            "existing": API_BASE + "/auth"
        };

        if (Object.keys(types).indexOf(options.type) == -1) {
            throw new Error("Invalid userType provided. Allowed values are: " + Object.keys(types));
        }

        $.ajax({
            url: types[options.type],
            method: "POST",
            dataType: "json",
            data: options.data,
            success: function (response) {
                if (response.status == 200) {
                    self.getUser({
                        userId: response.data.userId,
                        accessToken: response.data.accessToken,
                        success: function (user) {
                            if (options.success) options.success();
                        },
                        error: function (error) {
                            if (options.error) options.error(error);
                        }
                    });
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    oauth: function (options) {
        this.getUser({
            userId: options.userId,
            accessToken: options.accessToken,
            success: function (user) {
                if (options.success) options.success();
            },
            error: function (error) {
                if (options.error) options.error(error);
            }
        });
    },
    deauth: function (options) {
        var user = this.getUserFromCache();

        if (user) {
            $.ajax({
                url: API_BASE + "/deauth",
                method: "POST",
                dataType: "json",
                beforeSend: function (jqXHR) {
                    jqXHR.setRequestHeader("Authorization", "Token " + user.accessToken);
                },
                success: function (response) {
                    if (response.status == 200) {
                        localStorage.clear();
                        if (options.success) options.success();
                    } else {
                        if (options.error) options.error(response.error);
                    }
                }
            });
        }
    },
    updateAccount: function (options) {
        var user = this.getUserFromCache();

        $.ajax({
            url: API_BASE + "/users/update/" + user.userId,
            method: "POST",
            dataType: "json",
            data: options.data,
            beforeSend: function (jqXHR) {
                jqXHR.setRequestHeader("Authorization", "Token " + user.accessToken);
            },
            success: function (response) {
                if (response.status == 200) {
                    if (options.success) options.success();
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    deleteAccount: function (options) {
        var user = this.getUserFromCache();

        $.ajax({
            url: API_BASE + "/users/destroy/" + user.userId,
            method: "POST",
            dataType: "json",
            beforeSend: function (jqXHR) {
                jqXHR.setRequestHeader("Authorization", "Token " + user.accessToken);
            },
            success: function (response) {
                if (response.status == 200) {
                    localStorage.clear();

                    if (options.success) options.success();
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    unlinkAccount: function (options) {
        var user = this.getUserFromCache();

        $.ajax({
            url: API_BASE + "/linked-accounts/destroy/" + options.providerName,
            method: "POST",
            dataType: "json",
            beforeSend: function (jqXHR) {
                jqXHR.setRequestHeader("Authorization", "Token " + user.accessToken);
            },
            success: function (response) {
                if (response.status == 200) {
                    if (options.success) options.success();
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    /**
     * Gets currently authenticated user and saves it in cache. If this method is invoked when there is no authenticated user in cache, then
     * userId and accessToken of the newly authenticated user must be a part of the provided options object.
     */
    getUser: function (options) {
        var user = this.getUserFromCache();

        $.ajax({
            url: user ? API_BASE + "/users/" + user.userId : API_BASE + "/users/" + options.userId,
            method: "GET",
            dataType: "json",
            beforeSend: function (jqXHR) {
                jqXHR.setRequestHeader("Authorization", user ? "Token " + user.accessToken : "Token " + options.accessToken);
            },
            success: function (response) {
                if (response.status == 200) {
                    localStorage.setItem(
                        "user",
                        JSON.stringify({
                            userId: user ? user.userId : options.userId,
                            accessToken: user ? user.accessToken : options.accessToken,
                            userName: response.data.name,
                            userAvatar: response.data.avatar
                        })
                    );

                    if (options.success) options.success(response.data);
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    getUserFromCache: function () {
        var user = localStorage.getItem("user");
        if (user) {
            return JSON.parse(user);
        } else {
            return undefined;
        }
    },
    isAuthenticated: function () {
        return (localStorage.getItem("user") != undefined);
    },
    getAllOauth2Urls: function (options) {
        $.ajax({
            url: API_BASE + "/oauth2-urls",
            method: "GET",
            dataType: "json",
            success: function (response) {
                if (response.status == 200) {
                    if (options.success) options.success(response.data);
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    }
};

module.exports = authController;
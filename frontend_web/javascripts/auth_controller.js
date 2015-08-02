/**
 * Created by ManasB on 7/31/2015.
 */

var $ = require("jquery");
var API_BASE = "http://localhost:3000";

var authController = {
    basicAuth: function (options) {
        var self = this;

        var types = {
            "new": API_BASE + "/users",
            "existing": API_BASE + "/auth"
        };


        if (Object.keys(types).indexOf(options.type) == -1) {
            throw new Error("Invalid userType provided. Allowed values are: " + Object.keys(types));
        }

        $.ajax({
            url: types[options.type],
            method: "POST",
            dataType: "json",
            data: {username: options.username, password: options.password},
            success: function (response) {
                if (response.status == 200) {
                    self._getUser(response.data.accessToken, function (response2) {
                        if (response2.status == 200) {
                            localStorage.setItem(
                                "user",
                                JSON.stringify({
                                    username: response2.data.username,
                                    accessToken: response.data.accessToken,
                                    expiresIn: response.data.expiresIn,
                                    createdAt: response.data.createdAt
                                })
                            );

                            if (options.success) options.success();
                        } else {
                            if (options.error) options.error(response.error);
                        }
                    });
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    oauth: function (options) {
        this._getUser(options.accessToken, function (response) {
            if (response.status == 200) {
                localStorage.setItem(
                    "user",
                    JSON.stringify({
                        username: response.data.username,
                        accessToken: options.accessToken,
                        expiresIn: options.expiresIn,
                        createdAt: options.createdAt
                    })
                );

                if (options.success) options.success();
            } else {
                if (options.error) options.error(response.error);
            }
        });
    },
    deauth: function (options) {
        var user = this.getUserFromCache();

        if (user) {
            $.ajax({
                url: API_BASE + "/me/deauth",
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
    _getUser: function (accessToken, cb) {
        $.ajax({
            url: API_BASE + "/me/",
            method: "GET",
            dataType: "json",
            beforeSend: function (jqXHR) {
                jqXHR.setRequestHeader("Authorization", "Token " + accessToken);
            },
            success: function (response) {
                cb(response);
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
    _checkAccessToken: function (user) {
        var hasExpired = (Date.now() - user.createdAt) >= user.expiresIn;
        if (hasExpired) {
            this.deauth();
            $(document).trigger("access.token.expired");
        }

        return !hasExpired;
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
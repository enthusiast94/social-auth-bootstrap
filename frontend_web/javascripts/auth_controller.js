/**
 * Created by ManasB on 7/31/2015.
 */

var $ = require("jquery");
var API_BASE = "http://localhost:3000";

var authController = {
    basicAuth: function (options) {
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
            data: {username: options.username, password: options.password},
            success: function (response) {
                if (response.status == 200) {
                    localStorage.setItem(
                        "user",
                        JSON.stringify({
                            userId: response.data.accessToken.userId,
                            accessToken: response.data.accessToken.value,
                            expiresIn: response.data.accessToken.expiresIn,
                            createdAt: response.data.accessToken.createdAt
                        })
                    );

                    if (options.success) options.success();
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    oauth: function (options) {
        localStorage.setItem(
            "user",
            JSON.stringify({
                userId: options.userId,
                accessToken: options.accessToken,
                expiresIn: options.expiresIn,
                createdAt: options.createdAt
            })
        );

        if (options.success) options.success();
    },
    deauth: function (options) {
        var user = this._getUserFromCache();

        if (user) {
            $.ajax({
                url: API_BASE + "/deauth",
                method: "POST",
                dataType: "json",
                beforeSend: function (jqXHR) {
                    jqXHR.setRequestHeader("Authorization", "Token " + user.accessToken);
                },
                success: function () {
                    localStorage.clear();
                    if (options.success) options.success();
                }
            });
        }
    },
    getUser: function (options) {
        var user = this._getUserFromCache();

        $.ajax({
            url: API_BASE + "/users/" + user.userId,
            method: "GET",
            dataType: "json",
            //beforeSend: function (jqXHR) {
            //    jqXHR.setRequestHeader("Authorization", "Token " + user.accessToken);
            //},
            success: function (response) {
                if (response.status == 200) {
                    if (options.success) options.success(response.data.user);
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    },
    _getUserFromCache: function () {
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
                    if (options.success) options.success(response.data.oauth2Urls);
                } else {
                    if (options.error) options.error(response.error);
                }
            }
        });
    }
};

module.exports = authController;
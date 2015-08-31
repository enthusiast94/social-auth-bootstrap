#Social Authentication Bootstrap 

A boilerplate project that provides out-of-the-box support for token based basic authentication (Email/Password) and open authentication (OAuth 2.0) with various providers, using industry's best practices and modules. 

The project is divided into three parts:
 
1. A back-end built using [Spark micro-framework](http://sparkjava.com/).
2. A single page web application built using [Backbone.JS](http://backbonejs.org/).
3. An Android application targeting [Android Lollipop](https://www.android.com/versions/lollipop-5-0/). 
 

Basically, the back-end exposes a REST API using which both the front-end applications communicate with the server. 

##Table of Contents
- [Features](#features) 
- [Screenshots](#screenshots)
- [Getting Started](#getting-started)
- [Using Your Own API Keys](#using-your-own-api-keys)

##Features
- **Basic Authentication** using Email and Password
- **OAuth 2.0 Authentication** via Google, Facebook, Github and LinkedIn
- **Bootstrap 3** UI on the single page app + **Material Design** on the Android app
- **Account Management:**
	- View and update account details
	- Gravatar
	- Change password
	- Link multiple OAuth strategies to a single account (only if email is common to all strategies)
	- Delete account

##Screenshots
![1](https://github.com/enthusiast94/social-auth-bootstrap/blob/master/screenshots/1.png)
![2](https://github.com/enthusiast94/social-auth-bootstrap/blob/master/screenshots/2.png)
![3](https://github.com/enthusiast94/social-auth-bootstrap/blob/master/screenshots/6.png)

##Getting Started
Tools you need to get started:
- [MongoDB](https://www.mongodb.org/) 
- [Gradle](https://gradle.org/gradle-download/)
- [Android SDK](http://developer.android.com/sdk/index.html) 

> **Note:** Make sure you add the relevant paths of the above tools to your PATH variable. 

Once you have installed the above tools, you need to clone the repository and execute the following commands from the `backend` directory: 
```
$ mongod
$ gradle run 
```
The command `mongod` starts up the MongoDB server, while `gradle run` executes the `run` task, which in turn compiles all the back-end source, along with all the dependencies, and runs the main Java class: `com.enthusiast94.social_auth_bootstrap.Main`, starting up the application server.

>**Note:** By default, the application server will run on port `3005`. You can change this in `com.enthusiast94.social_auth_bootstrap.Main`.   
 
 Now that our back-end is ready, we'll move onto our front-end single page application. Execute the following commands from the `frontend_web` directory in order to install all the client-side JavaScript dependencies (listed in `frontend_web/package.json`), and start the application.  
 ```
 $ npm install
 $ npm start
 ```
 >**Note:** By default, the single page application will run on port `4000`. You can change this by modifying the `start` script included in `frontend_web/package.json`.

And Finally, we'll move on to the Android application. First, you will have to import the Android project included in `frontend_android` into Android Studio or IntelliJ IDEA (with Android plugin). Once it has been imported, you can use the `Gradle Tool Window` to build and run the project. [See here if you would like to run the project via the command line](http://developer.android.com/tools/building/building-cmdline.html). 
   
##Using Your Own API Keys
When you run the project as it is (without making any changes to `backend/config.json`, you'll notice that whenever you try to authenticate via an external provider, it asks you to authorize an app named `social-auth-starter`. This is because I have created an app with this name for all the supported providers separately, and then added their client IDs and secrets to `backend/config.json`. **This is where all OAuth client credentials are stored.** 

Apart from the client ID and secret, you also have to provide the following information:

- **`server_redirect_uri_base`**: This is the base HTTP URL of your back-end server to which the OAuth providers will redirect to passing the user's access token/authorization code (depends on the provider). The path will always be `/oauth2-callback`, whereas the protocol and domain name will depend on how you host the server. The complete redirect URL is constructed by appending the `server_redirect_uri_path` to this.
- **`client_redirect_uri`**: This is the HTTP URL of your front-end application server to which your back-end server will redirect to passing the user's access token and ID. 
- **`default_user_password`**: This is the default password which will be used when creating new users using an OAuth2 strategy. 
- **`server_redirect_uri_path`**: This path needs to be provided for each of the different providers. It will be appended to `server_redirect_uri_base` in order to construct the full redirect URL for the server.   

# Getting started

<!-- toc -->

## Building Cordova Applications

To be able to create an Apache Cordova project you'll need to install several tools. Refer to the [Apache Cordova getting started guide](https://cordova.apache.org/#getstarted) if you need help setting up your Cordova environment.

> Note that if you want to create an iOS application you'll need a Mac to build and debug your application.

You may add the Onegini Cordova Plugin to your existing app, but if you're starting from scratch, you will need to create a Cordova Project first:

```bash
cordova create <PATH> [ID [NAME [CONFIG]]] [options]
```    

Example:
```bash
cordova create ./myapp com.mycompany.myapp "My App"
```

## Access the Onegini Repository

The Onegini Cordova Plugin is a wrapper for the Onegini SDK. For your Cordova app to access the Onegini SDK you will need access to the [Onegini Repository](https://repo.onegini.com/). If you do not have login credentials for the Onegini Repository, you will need to [obtain those first](https://docs.onegini.com/app-developer-quickstart.html#step1).
After obtaining credentials, store export them as environment variables:

```bash
export ARTIFACTORY_USER="<username>"
export ARTIFACTORY_PASSWORD="<password>"
```

In Android projects, Gradle is used to resolve the Android SDK since this is the default tool to manage dependencies. For iOS projects we use a Cordova hook (resolve dependencies) in order to download the iOS SDK from the Onegini repository. Please check the [configuration topic guide](configuration.md#customizing-the-resolve-dependencies-hook) for additional configuration options to this resolve dependencies hook.

## Add the plugin to your project

```bash
cordova plugin add cordova-plugin-onegini
```

## Configure your project

More details about platform configuration can be found in the [Configuration guide](configuration.md). The following steps help you setup a basic configuration.

### Installing the Onegini SDK Configurator

For the Cordova plugin to be able to configure your Cordova app for use with your Token Server, you will need to install the Onegini SDK Configurator.

Download the latest release for your platform from the [release page](https://github.com/Onegini/onegini-sdk-configurator/releases) and extract the zip file.

Point the Cordova Plugin to the location you stored the configurator by setting the `ONEGINI_SDK_CONFIGURATOR_PATH` environment variable.
```bash
export ONEGINI_SDK_CONFIGURATOR_PATH=/path/to/onegini-sdk-configurator
```

### Add your Token Server configuration

For the Onegini SDK to connect to your Token Server, you will need to add the configuration zip file for your platform version to your project. You can download this zip from your Token Server admin console under `Configuration > Applications > My App`. Under _Platform versions_ click `Actions > Export Platform version config`.

Once you have obtained the zip file for your platform place it in your project root as `onegini-config-android.zip` or `ongegini-config-ios.zip` depending on your platform. The SDK Configurator will pick up these files when you add a platform.

### Set your preferences

For now we will use some preferences to make getting up and running easy, you might want to reconsider these for production.

```xml
<preference name="OneginiRootDetectionEnabled" value="false" />
<preference name="OneginiDebugDetectionEnabled" value="false" />
```

### Add a Cordova platform

When you add a platform to your Cordova app, the plugin will automatically configure your platform and resolve dependencies.

```bash
cordova platform add android ios
```

## Run your app

You are now ready to run your application!

```bash
cordova run android ios
```

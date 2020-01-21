package com.onegini.mobile.sdk.cordova.handler;

import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.PluginResult;

import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler;
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn;
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError;

import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.EVENT_SUCCESS;

public class AppToWebSingleSignOnHandler implements OneginiAppToWebSingleSignOnHandler {

  private final CallbackContext callbackContext;

  public AppToWebSingleSignOnHandler(final CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  @Override
  public void onSuccess(final OneginiAppToWebSingleSignOn response) {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .withAppToWebSingleSignOn(response)
        .build();

    sendPluginResult(pluginResult);
  }

  @Override
  public void onError(final OneginiAppToWebSingleSignOnError error) {
    final PluginResult pluginResultBuilder = new PluginResultBuilder()
        .withError()
        .withOneginiError(error)
        .build();

    sendPluginResult(pluginResultBuilder);
  }

  private void sendPluginResult(final PluginResult pluginResult) {
    if (!callbackContext.isFinished()) {
      callbackContext.sendPluginResult(pluginResult);
    }
  }
}

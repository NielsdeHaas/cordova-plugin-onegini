package com.onegini.handler;

import static com.onegini.OneginiCordovaPluginConstants.AUTH_EVENT_SUCCESS;
import static com.onegini.OneginiCordovaPluginConstants.PIN_LENGTH;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.onegini.mobile.sdk.android.handlers.OneginiChangePinHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError;
import com.onegini.util.PluginResultBuilder;

public class ChangePinHandler implements OneginiChangePinHandler {
  private CallbackContext callbackContext;

  public ChangePinHandler(final CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }

  @Override
  public void onSuccess() {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withSuccess()
        .withPinLength(PIN_LENGTH)
        .withAuthenticationEvent(AUTH_EVENT_SUCCESS)
        .build();

    sendPluginResult(pluginResult);
  }

  @Override
  public void onError(final OneginiChangePinError oneginiChangePinError) {
    final PluginResult pluginResult = new PluginResultBuilder()
        .withError()
        .withOneginiError(oneginiChangePinError)
        .build();

    sendPluginResult(pluginResult);
  }

  private void sendPluginResult(final PluginResult pluginResult) {
    if (!callbackContext.isFinished()) {
      callbackContext.sendPluginResult(pluginResult);
    }
  }
}
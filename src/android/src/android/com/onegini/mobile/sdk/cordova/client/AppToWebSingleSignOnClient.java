package com.onegini.mobile.sdk.cordova.client;

import android.net.Uri;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.handler.AppToWebSingleSignOnHandler;
import com.onegini.mobile.sdk.cordova.handler.AuthenticatorRegistrationHandler;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_ILLEGAL_ARGUMENT;

public class AppToWebSingleSignOnClient extends CordovaPlugin {

  private static final String ACTION_START = "start";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_START.equals(action)) {
      startSingleSignOn(args, callbackContext);
      return true;
    }
    return false;
  }

  private void startSingleSignOn(final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    final Uri targetUri = getTargetUri(args);

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        getOneginiClient().getUserClient().getAppToWebSingleSignOn(targetUri, new AppToWebSingleSignOnHandler(callbackContext));
      }
    });
  }

  private Uri getTargetUri(final JSONArray args) throws JSONException {
    return Uri.parse(args.getString(0));
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }
}

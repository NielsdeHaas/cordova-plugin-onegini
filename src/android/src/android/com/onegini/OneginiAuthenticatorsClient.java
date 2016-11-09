package com.onegini;

import static com.onegini.OneginiCordovaPluginConstants.ERROR_NO_SUCH_AUTHENTICATOR;
import static com.onegini.OneginiCordovaPluginConstants.ERROR_NO_USER_AUTHENTICATED;
import static com.onegini.OneginiCordovaPluginConstants.ERROR_PLUGIN_INTERNAL_ERROR;
import static com.onegini.OneginiCordovaPluginConstants.ERROR_PROFILE_NOT_REGISTERED;
import static com.onegini.OneginiCordovaPluginConstants.PARAM_PROFILE_ID;

import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.onegini.mobile.sdk.android.model.OneginiAuthenticator;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.util.ActionArgumentsUtil;
import com.onegini.util.AuthenticatorUtil;
import com.onegini.util.PluginResultBuilder;
import com.onegini.util.UserProfileUtil;

public class OneginiAuthenticatorsClient extends CordovaPlugin {
  private static final String ACTION_GET_REGISTERED_AUTHENTICATORS = "getRegistered";
  private static final String ACTION_GET_ALL_AUTHENTICATORS = "getAll";
  private static final String ACTION_GET_NOT_REGISTERED_AUTHENTICATORS = "getNotRegistered";
  private static final String ACTION_GET_PREFERRED_AUTHENTICATOR = "getPreferred";
  private static final String ACTION_SET_PREFERRED_AUTHENTICATOR = "setPreferred";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_GET_REGISTERED_AUTHENTICATORS.equals(action)) {
      getAuthenticators(args, callbackContext, action);
      return true;
    } else if (ACTION_GET_ALL_AUTHENTICATORS.equals(action)) {
      getAuthenticators(args, callbackContext, action);
      return true;
    } else if (ACTION_GET_NOT_REGISTERED_AUTHENTICATORS.equals(action)) {
      getAuthenticators(args, callbackContext, action);
      return true;
    } else if (ACTION_GET_PREFERRED_AUTHENTICATOR.equals(action)) {
      getPreferredAuthenticator(callbackContext);
      return true;
    } else if (ACTION_SET_PREFERRED_AUTHENTICATOR.equals(action)) {
      setPreferredAuthenticator(args, callbackContext);
      return true;
    }

    return false;
  }

  private void getAuthenticators(final JSONArray args, final CallbackContext callbackContext, final String action) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final UserProfile userProfile;
        try {
          userProfile = getUserProfile(args);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withErrorDescription(ERROR_PROFILE_NOT_REGISTERED)
              .build());

          return;
        }

        final Set<OneginiAuthenticator> authenticatorSet;
        if (ACTION_GET_REGISTERED_AUTHENTICATORS.equals(action)) {
          authenticatorSet = getOneginiClient().getUserClient().getRegisteredAuthenticators(userProfile);
        } else if (ACTION_GET_NOT_REGISTERED_AUTHENTICATORS.equals(action)) {
          authenticatorSet = getOneginiClient().getUserClient().getNotRegisteredAuthenticators(userProfile);
        } else if (ACTION_GET_ALL_AUTHENTICATORS.equals(action)) {
          authenticatorSet = getOneginiClient().getUserClient().getAllAuthenticators(userProfile);
        } else {
          return;
        }

        final JSONArray authenticatorJSONArray;
        try {
          authenticatorJSONArray = AuthenticatorUtil.AuthenticatorSetToJSONArray(authenticatorSet);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withErrorDescription(ERROR_PLUGIN_INTERNAL_ERROR)
              .build());

          return;
        }

        callbackContext.success(authenticatorJSONArray);
      }
    });
  }

  private void getPreferredAuthenticator(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final UserProfile userProfile = getOneginiClient().getUserClient().getAuthenticatedUserProfile();
        if (userProfile == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withErrorDescription(ERROR_NO_USER_AUTHENTICATED)
              .build());

          return;
        }

        final OneginiAuthenticator authenticator = getOneginiClient().getUserClient().getPreferredAuthenticator();

        final JSONObject authenticatorJSON;
        try {
          authenticatorJSON = AuthenticatorUtil.AuthenticatorToJSONObject(authenticator);
        } catch (JSONException e) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withErrorDescription(ERROR_PLUGIN_INTERNAL_ERROR)
              .build());

          return;
        }

        callbackContext.success(authenticatorJSON);
      }
    });
  }

  private void setPreferredAuthenticator(final JSONArray args, final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final UserProfile userProfile = getOneginiClient().getUserClient().getAuthenticatedUserProfile();
        if (userProfile == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withErrorDescription(ERROR_NO_USER_AUTHENTICATED)
              .build());

          return;
        }

        final Set<OneginiAuthenticator> authenticatorSet = getOneginiClient().getUserClient().getRegisteredAuthenticators(userProfile);

        OneginiAuthenticator authenticator;
        try {
          authenticator = ActionArgumentsUtil.getAuthenticatorFromArguments(args, authenticatorSet);
        } catch (JSONException e) {
          authenticator = null;
        }

        if (authenticator == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withErrorDescription(ERROR_NO_SUCH_AUTHENTICATOR)
              .build());

          return;
        }

        getOneginiClient().getUserClient().setPreferredAuthenticator(authenticator);

        callbackContext.sendPluginResult(new PluginResultBuilder()
            .withSuccess()
            .build());
      }
    });
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(cordova.getActivity().getApplicationContext());
  }

  private UserProfile getUserProfile(final JSONArray args) throws JSONException {
    String profileId = args.getJSONObject(0).getString(PARAM_PROFILE_ID);
    Set<UserProfile> registeredUserProfiles = getOneginiClient().getUserClient().getUserProfiles();
    return UserProfileUtil.findUserProfileById(profileId, registeredUserProfiles);
  }
}

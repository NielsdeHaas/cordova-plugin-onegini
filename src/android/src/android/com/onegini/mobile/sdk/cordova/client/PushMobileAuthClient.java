/*
 * Copyright (c) 2017-2019 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onegini.mobile.sdk.cordova.client;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_ILLEGAL_ARGUMENT;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_NO_USER_AUTHENTICATED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_FCM_KEY;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PUSH_MESSAGE;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PROFILE;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_NO_USER_AUTHENTICATED;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_PROFILE_ID;

import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithPushEnrollmentHandler;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthWithPushRequest;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.cordova.OneginiSDK;
import com.onegini.mobile.sdk.cordova.handler.MobileAuthWithPushEnrollmentHandler;
import com.onegini.mobile.sdk.cordova.handler.MobileAuthWithPushHandler;
import com.onegini.mobile.sdk.cordova.util.PendingMobileAuthRequestUtil;
import com.onegini.mobile.sdk.cordova.util.PluginResultBuilder;
import com.onegini.mobile.sdk.cordova.util.UserProfileUtil;

@SuppressWarnings("unused")
public class PushMobileAuthClient extends CordovaPlugin {

  private static final String ACTION_IS_ENROLLED = "isEnrolled";
  private static final String ACTION_ENROLL = "enroll";
  private static final String ACTION_HANDLE = "handle";

  @Override
  public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (ACTION_IS_ENROLLED.equals(action)) {
      isEnrolled(args, callbackContext);
      return true;
    } else if (ACTION_ENROLL.equals(action)) {
      enrollForPush(args, callbackContext);
      return true;
    } else if (ACTION_HANDLE.equals(action)) {
      handlePush(args, callbackContext);
      return true;
    }

    return false;
  }

  private void isEnrolled(final JSONArray args, final CallbackContext callbackContext) {
    final String userProfileId;

    try {
      userProfileId = args.getJSONObject(0).getString(PARAM_PROFILE_ID);
    } catch (JSONException e) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withError()
          .withPluginError(ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PROFILE, ERROR_CODE_ILLEGAL_ARGUMENT)
          .build());

      return;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final Set<UserProfile> userProfiles = getOneginiClient().getUserClient().getUserProfiles();
        final UserProfile userProfile = UserProfileUtil.findUserProfileById(userProfileId, userProfiles);

        if (userProfile == null) {
          callbackContext.sendPluginResult(new PluginResultBuilder()
              .withError()
              .withPluginError(ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PROFILE, ERROR_CODE_ILLEGAL_ARGUMENT)
              .build());

          return;
        }

        final boolean isEnrolled = getOneginiClient().getUserClient().isUserEnrolledForMobileAuthWithPush(userProfile);
        final PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, isEnrolled);
        callbackContext.sendPluginResult(pluginResult);
      }
    });
  }

  private void enrollForPush(final JSONArray args, final CallbackContext callbackContext) {
    final UserProfile userProfile = getOneginiClient().getUserClient().getAuthenticatedUserProfile();

    if (userProfile == null) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withPluginError(ERROR_DESCRIPTION_NO_USER_AUTHENTICATED, ERROR_CODE_NO_USER_AUTHENTICATED)
          .build());

      return;
    }

    final String key;
    try {
      key = args.getString(0);
    } catch (JSONException e) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
        .withError()
        .withPluginError(ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_FCM_KEY, ERROR_CODE_ILLEGAL_ARGUMENT)
        .build());

      return;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        final OneginiMobileAuthWithPushEnrollmentHandler handler = new MobileAuthWithPushEnrollmentHandler(callbackContext);
        getOneginiClient().getUserClient().enrollUserForMobileAuthWithPush(key, handler);
      }
    });
  }

  private void handlePush(final JSONArray args, final CallbackContext callbackContext) {
    final OneginiMobileAuthWithPushRequest mobileAuthWithPushRequest;

    try {
      final String json = args.getString(0);
      mobileAuthWithPushRequest = PendingMobileAuthRequestUtil.pendingMobileAuthRequestFromJSON(json);
    } catch (Exception e) {
      callbackContext.sendPluginResult(new PluginResultBuilder()
          .withError()
          .withPluginError(ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PUSH_MESSAGE, ERROR_CODE_ILLEGAL_ARGUMENT)
          .build());

      return;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        getOneginiClient().getUserClient().handleMobileAuthWithPushRequest(mobileAuthWithPushRequest, MobileAuthWithPushHandler.getInstance());
      }
    });
  }

  private com.onegini.mobile.sdk.android.client.OneginiClient getOneginiClient() {
    return OneginiSDK.getInstance().getOneginiClient(getApplicationContext());
  }

  private Context getApplicationContext() {
    return cordova.getActivity().getApplicationContext();
  }
}

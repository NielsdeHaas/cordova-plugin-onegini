/*
 * Copyright (c) 2017-2018 Onegini B.V.
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

package com.onegini.mobile.sdk.cordova.util;

import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_CODE_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_ERROR_CODE;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_ERROR_DESCRIPTION;
import static com.onegini.mobile.sdk.cordova.OneginiCordovaPluginConstants.PARAM_URL;
import static org.apache.cordova.PluginResult.Status.ERROR;
import static org.apache.cordova.PluginResult.Status.OK;

import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import com.onegini.mobile.sdk.android.handlers.error.OneginiError;
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn;

public class PluginResultBuilder {

  private JSONObject payload;
  private PluginResult.Status status;
  private Boolean shouldKeepCallback = false;

  public PluginResultBuilder() {
    payload = new JSONObject();
  }

  public PluginResultBuilder withSuccess() {
    status = OK;
    return this;
  }

  public PluginResultBuilder withError() {
    status = ERROR;
    return this;
  }

  public PluginResultBuilder shouldKeepCallback() {
    this.shouldKeepCallback = true;

    return this;
  }

  public PluginResultBuilder withPluginError(final String description, final int code) {
    status = ERROR;

    try {
      payload.put(PARAM_ERROR_DESCRIPTION, description);
      payload.put(PARAM_ERROR_CODE, code);
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withOneginiError(final OneginiError oneginiError) {
    status = ERROR;

    if (oneginiError == null) {
      return this;
    }

    try {
      payload.put(PARAM_ERROR_CODE, oneginiError.getErrorType());
      payload.put(PARAM_ERROR_DESCRIPTION, oneginiError.getMessage());
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withOneginiMobileAuthenticationRequest(final OneginiMobileAuthenticationRequest mobileAuthenticationRequest) {
    try {
      payload.put("type", mobileAuthenticationRequest.getType());
      payload.put("message", mobileAuthenticationRequest.getMessage());
      payload.put("profileId", mobileAuthenticationRequest.getUserProfile().getProfileId());
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withEvent(final String eventName) {
    try {
      payload.put("pluginEvent", eventName);
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withRemainingFailureCount(int remainingFailureCount) {
    try {
      payload.put("remainingFailureCount", remainingFailureCount);
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withMaxFailureCount(int maxFailureCount) {
    try {
      payload.put("maxFailureCount", maxFailureCount);
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withPinLength(int pinLength) {
    try {
      payload.put("pinLength", pinLength);
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withProfileId(UserProfile userProfile) {
    try {
      payload.put("profileId", userProfile.getProfileId());
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withOneginiConfigModel(final OneginiClientConfigModel configModel) {
    try {
      payload.put("resourceBaseURL", configModel.getResourceBaseUrl());
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withUri(final Uri uri) {
    try {
      payload.put(PARAM_URL, uri.toString());
    } catch (JSONException e) {
      handleException(e);
    }

    return this;
  }

  public PluginResultBuilder withCustomInfo(final CustomInfo customInfo) {
    if (customInfo != null) {
      try {
        payload.put("customInfoStatus", customInfo.getStatus());
        payload.put("customInfoData", customInfo.getData());
      } catch (JSONException e) {
        handleException(e);
      }
    }
    return this;
  }

  public PluginResultBuilder withIdentityProviderId(final String identityProviderId) {
    if (identityProviderId != null) {
      try {
        payload.put("identityProviderId", identityProviderId);
      } catch (JSONException e) {
        handleException(e);
      }
    }
    return this;
  }

  public PluginResultBuilder withAppToWebSingleSignOn(final OneginiAppToWebSingleSignOn response) {
    if (response != null) {
      try {
        payload.put("redirectUri", response.getRedirectUrl());
        payload.put("token", response.getToken());
      } catch (JSONException e) {
        handleException(e);
      }
    }
    return this;
  }

  private void handleException(JSONException e) {
    this.status = ERROR;

    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(PARAM_ERROR_CODE, ERROR_CODE_PLUGIN_INTERNAL_ERROR);
    payload.put(PARAM_ERROR_DESCRIPTION, ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR + " : " + e.getMessage());
    this.payload = new JSONObject(payload);
  }

  public PluginResult build() {
    PluginResult pluginResult;

    if (payload.length() == 0) {
      pluginResult = new PluginResult(status);
    } else {
      pluginResult = new PluginResult(status, payload);
    }

    pluginResult.setKeepCallback(shouldKeepCallback);

    return pluginResult;
  }
}

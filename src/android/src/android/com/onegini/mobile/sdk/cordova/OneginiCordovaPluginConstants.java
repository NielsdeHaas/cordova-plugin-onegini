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

package com.onegini.mobile.sdk.cordova;

public interface OneginiCordovaPluginConstants {

  // Json params
  String PARAM_SCOPES = "scopes";
  String PARAM_PROFILE_ID = "profileId";
  String PARAM_PIN = "pin";
  String PARAM_URL = "url";
  String PARAM_METHOD = "method";
  String PARAM_HEADERS = "headers";
  String PARAM_BODY = "body";
  String PARAM_AUTHENTICATOR_TYPE = "authenticatorType";
  String PARAM_AUTHENTICATOR_ID = "authenticatorId";
  String PARAM_AUTHENTICATOR_IS_PREFERRED = "isPreferred";
  String PARAM_AUTHENTICATOR_IS_REGISTERED = "isRegistered";
  String PARAM_AUTHENTICATOR_NAME = "name";
  String PARAM_ACCEPT = "accept";
  String PARAM_ERROR_CODE = "code";
  String PARAM_ERROR_DESCRIPTION = "description";
  String PARAM_ANONYMOUS = "anonymous";
  String PARAM_OTP = "otp";
  String PARAM_TRANSACTION_ID = "transactionId";
  String PARAM_MESSAGE = "message";
  String PARAM_TIMESTAMP = "timestamp";
  String PARAM_TIME_TO_LIVE_SECONDS = "timeToLiveSeconds";

  // Error descriptions
  String ERROR_DESCRIPTION_PLUGIN_INTERNAL_ERROR = "Onegini: Internal plugin error";
  String ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PROFILE = "Onegini: Argument provided is not a valid profile object";
  String ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_FCM_KEY = "Onegini: Argument provided is not a valid FCM registration key";
  String ERROR_DESCRIPTION_ILLEGAL_ARGUMENT_PUSH_MESSAGE = "Onegini: Argument provided is not a valid Onegini push message";

  String ERROR_DESCRIPTION_PROFILE_NOT_REGISTERED = "Onegini: No registered user found.";
  String ERROR_DESCRIPTION_NO_USER_AUTHENTICATED = "Onegini: No user authenticated.";
  String ERROR_DESCRIPTION_NO_SUCH_AUTHENTICATOR = "Onegini: No such authenticator found";
  String ERROR_DESCRIPTION_CREATE_PIN_NO_REGISTRATION_IN_PROGRESS = "Onegini: createPin called, but no registration in progress.";
  String ERROR_DESCRIPTION_PROVIDE_PIN_NO_AUTHENTICATION_IN_PROGRESS = "Onegini: providePin called, but no pin authentication in progress.";
  String ERROR_DESCRIPTION_FINGERPRINT_NO_AUTHENTICATION_IN_PROGRESS = "Onegini: received reply for fingerprint authentication, but no fingerprint authentication in progress.";
  String ERROR_DESCRIPTION_INVALID_MOBILE_AUTHENTICATION_METHOD = "Onegini: Invalid mobile authentication method";
  String ERROR_DESCRIPTION_INCORRECT_PIN = "Onegini: Incorrect Pin. Check the maxFailureCount and remainingFailureCount properties for details.";
  String ERROR_DESCRIPTION_OPERATION_CANCELED = "Onegini: the operation was canceled.";
  String ERROR_DESCRIPTION_INVALID_FETCH_AUTH_METHOD = "Onegini: invalid authentication method for resource.fetch";
  String ERROR_DESCRIPTION_INVALID_IDENTITY_PROVIDER_ID = "Onegini: Identity provider with the specified ID not found.";

  // Error codes
  int ERROR_CODE_PLUGIN_INTERNAL_ERROR = 8000;
  int ERROR_CODE_CONFIGURATION = 8001;
  int ERROR_CODE_ILLEGAL_ARGUMENT = 8002;

  int ERROR_CODE_PROFILE_NOT_REGISTERED = 8003;
  int ERROR_CODE_NO_USER_AUTHENTICATED = 8005;
  int ERROR_CODE_NO_SUCH_AUTHENTICATOR = 8006;
  int ERROR_CODE_CREATE_PIN_NO_REGISTRATION_IN_PROGRESS = 8007;
  int ERROR_CODE_PROVIDE_PIN_NO_AUTHENTICATION_IN_PROGRESS = 8008;
  int ERROR_CODE_FINGERPRINT_NO_AUTHENTICATION_IN_PROGRESS = 8009;
  int ERROR_CODE_INVALID_MOBILE_AUTHENTICATION_METHOD = 8010;
  int ERROR_CODE_IO_EXCEPTION = 8011;
  int ERROR_CODE_INCORRECT_PIN = 8012;
  int ERROR_CODE_NO_SUCH_IDENTITY_PROVIDER = 8013;

  int ERROR_CODE_OPERATION_CANCELED = 9006;

  // methods
  String EVENT_SUCCESS = "onSuccess";
  String EVENT_CONFIRMATION_REQUEST = "onConfirmationRequest";
  String EVENT_PIN_REQUEST = "onPinRequest";
  String EVENT_CREATE_PIN_REQUEST = "onCreatePinRequest";
  String EVENT_FINGERPRINT_REQUEST = "onFingerprintRequest";
  String EVENT_FINGERPRINT_FAILED = "onFingerprintFailed";
  String EVENT_FINGERPRINT_CAPTURED = "onFingerprintCaptured";
  String EVENT_ON_REGISTRATION_REQUEST = "onRegistrationRequest";
  String EVENT_ON_CUSTOM_REGISTRATION_INIT_REQUEST = "onCustomRegistrationInitRequest";
  String EVENT_ON_CUSTOM_REGISTRATION_COMPLETE_REQUEST = "onCustomRegistrationCompleteRequest";

  // Extras
  String EXTRA_MOBILE_AUTHENTICATION = "com.onegini.CordovaPluginMobileAuthenticationExtra";

  String TAG = "oneginiCordovaPlugin";

  // Push message
  String PUSH_MSG_CONTENT = "content";
  String PUSH_MSG_TRANSACTION_ID = "og_transaction_id";
  String PUSH_MSG_MESSAGE = "og_message";

  //Identity Providers
  String IDENTITY_PROVIDER_ID = "identityProviderId";
  String IDENTITY_PROVIDER_NAME = "identityProviderName";
}

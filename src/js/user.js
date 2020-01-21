/*
 * Copyright (c) 2017 Onegini B.V.
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

module.exports = (function () {
  var utils = require('./utils');

  function UserActionHandler(options, client, action) {
    var self = this;
    this.callbacks = {};

    this.callbackActions = {
      providePin: function (options) {
        options = utils.getOptionsWithDefaults(options, {}, 'pin');
        if (!options || !options.pin) {
          throw new TypeError('Onegini: missing "pin" argument for providePin');
        }

        utils.callbackExec(client, 'providePin', options, callSuccessCallback, callErrorCallback);
      },

      fallbackToPin: function () {
        utils.onceExec(client, 'fallbackToPin');
      },

      acceptFingerprint: function (options) {
        options = utils.getOptionsWithDefaults(options, {}, 'iosPrompt');
        options.accept = true;
        utils.callbackExec(client, 'respondToFingerprintRequest', options, callSuccessCallback, callErrorCallback);
      },

      denyFingerprint: function () {
        utils.callbackExec(client, 'respondToFingerprintRequest', {accept: false}, callSuccessCallback, callErrorCallback)
      },

      createPin: function (options) {
        options = utils.getOptionsWithDefaults(options, {}, 'pin');
        if (!options || !options.pin) {
          throw new TypeError('Onegini: missing "pin" argument for createPin');
        }

        utils.callbackExec(client, 'createPin', options, callSuccessCallback, callErrorCallback);
      },

      handleRegistrationUrl: function (options) {
        options = utils.getOptionsWithDefaults(options, {}, 'url');
        utils.callbackExec(client, 'respondToRegistrationRequest', options, callSuccessCallback, callErrorCallback);
      },

      acceptRegistrationInitRequest: function (options) {
        options = utils.getOptionsWithDefaults(options, {accept: true}, 'data');
        utils.callbackExec(client, 'respondToCustomRegistrationInitRequest', options, callSuccessCallback, callErrorCallback);
      },

      denyRegistrationInitRequest: function (options) {
        options = utils.getOptionsWithDefaults(options, {accept: false});
        utils.callbackExec(client, 'respondToCustomRegistrationInitRequest', options, callSuccessCallback, callErrorCallback);
      },

      acceptRegistrationCompleteRequest: function (options) {
        options = utils.getOptionsWithDefaults(options, {accept: true}, 'data');
        utils.callbackExec(client, 'respondToCustomRegistrationCompleteRequest', options, callSuccessCallback, callErrorCallback);
      },

      denyRegistrationCompleteRequest: function () {
        options = utils.getOptionsWithDefaults(options, {accept: false});
        utils.callbackExec(client, 'respondToCustomRegistrationCompleteRequest', options, callSuccessCallback, callErrorCallback);
      },

      cancel: function () {
        utils.callbackExec(client, 'cancelFlow', {}, callSuccessCallback, callErrorCallback);
      }
    };

    function callSuccessCallback(options) {
      var event = options.pluginEvent;
      delete options.pluginEvent;

      if (self.callbacks[event]) {
        // for onSuccess we don't need to pass callbackActions
        if (event === 'onSuccess') {
          self.callbacks.onSuccess(options);
        } else {
          self.callbacks[event](self.callbackActions, options);
        }
      }
    }

    function callErrorCallback(err) {
      if (self.callbacks.onError) {
        self.callbacks.onError(err);
      } else {
        console.warn('Onegini: An Error occurred but no error callback was registered');
        console.error('Onegini: ', err);
      }
    }

    utils.callbackExec(client, action, options, callSuccessCallback, callErrorCallback)
  }

  UserActionHandler.prototype.onPinRequest = function (cb) {
    this.callbacks.onPinRequest = cb;
    return this;
  };

  UserActionHandler.prototype.onCreatePinRequest = function (cb) {
    this.callbacks.onCreatePinRequest = cb;
    return this;
  };

  UserActionHandler.prototype.onFingerprintRequest = function (cb) {
    this.callbacks.onFingerprintRequest = cb;
    return this;
  };

  UserActionHandler.prototype.onFingerprintCaptured = function (cb) {
    this.callbacks.onFingerprintCaptured = cb;
    return this;
  };

  UserActionHandler.prototype.onFingerprintFailed = function (cb) {
    this.callbacks.onFingerprintFailed = cb;
    return this;
  };

  UserActionHandler.prototype.onRegistrationRequest = function (cb) {
    this.callbacks.onRegistrationRequest = cb;
    return this;
  };

  UserActionHandler.prototype.onCustomRegistrationInitRequest = function (cb) {
    this.callbacks.onCustomRegistrationInitRequest = cb;
    return this;
  };

  UserActionHandler.prototype.onCustomRegistrationCompleteRequest = function (cb) {
    this.callbacks.onCustomRegistrationCompleteRequest = cb;
    return this;
  };

  UserActionHandler.prototype.onError = function (cb) {
    this.callbacks.onError = cb;
    return this;
  };

  UserActionHandler.prototype.onSuccess = function (cb) {
    this.callbacks.onSuccess = cb;
    return this;
  };

  function authenticate(profileOptions, authenticatorOptions) {
    profileOptions = utils.getOptionsWithDefaults(profileOptions, {}, 'profileId');
    if (!profileOptions.profileId) {
      throw new TypeError("Onegini: missing 'profileId' argument for user.authenticate");
    }

    const options = [profileOptions];

    if (authenticatorOptions) {
      authenticatorOptions = utils.getOptionsWithDefaults(authenticatorOptions, {}, 'authenticatorType');
      if (!authenticatorOptions.authenticatorType) {
        throw new TypeError("Onegini: missing 'authenticatorType' argument for user.authenticate");
      }
      options.push(authenticatorOptions);
    }

    return new UserActionHandler(options, 'OneginiUserAuthenticationClient', 'authenticate');
  }

  function authenticateImplicitly(options, successCb, failureCb) {
    options = utils.getOptionsWithDefaults(options, {
      scopes: []
    }, 'profileId');

    if (!options || !options.profileId) {
      throw new TypeError("Onegini: missing 'profileId' argument for user.authenticateImplicitly");
    }

    return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'authenticateImplicitly', options, successCb, failureCb);
  }

  function register(options) {
    options = utils.getOptionsWithDefaults(options, {}, 'scopes');
    return new UserActionHandler(options, 'OneginiUserRegistrationClient', 'start');
  }

  function changePin() {
    return new UserActionHandler(null, 'OneginiChangePinClient', 'start');
  }

  var authenticators = {
    getAll: function (options, successCb, failureCb) {
      if (!options || !options.profileId) {
        throw new TypeError("Onegini: missing 'profileId' argument for authenticators.getAll");
      }
      return utils.promiseOrCallbackExec('OneginiAuthenticatorsClient', 'getAll', options, successCb, failureCb);
    },

    getRegistered: function (options, successCb, failureCb) {
      if (!options || !options.profileId) {
        throw new TypeError("Onegini: missing 'profileId' argument for authenticators.getRegistered");
      }
      return utils.promiseOrCallbackExec('OneginiAuthenticatorsClient', 'getRegistered', options, successCb, failureCb);
    },

    getNotRegistered: function (options, successCb, failureCb) {
      if (!options || !options.profileId) {
        throw new TypeError("Onegini: missing 'profileId' argument for authenticators.getNotRegistered");
      }
      return utils.promiseOrCallbackExec('OneginiAuthenticatorsClient', 'getNotRegistered', options, successCb, failureCb);
    },

    getPreferred: function (successCb, failureCb) {
      return utils.promiseOrCallbackExec("OneginiAuthenticatorsClient", "getPreferred", [], successCb, failureCb);
    },

    setPreferred: function (options, successCb, failureCb) {
      options = utils.getOptionsWithDefaults(options, {}, 'authenticatorType');
      if (!options || !options.authenticatorType) {
        throw new TypeError("Onegini: missing 'authenticatorType' argument for authenticators.setPreferred");
      }
      return utils.promiseOrCallbackExec('OneginiAuthenticatorsClient', 'setPreferred', options, successCb, failureCb);
    },

    registerNew: function (options) {
      options = utils.getOptionsWithDefaults(options, {}, 'authenticatorType');
      if (!options || !options.authenticatorType) {
        throw new TypeError("Onegini: missing 'authenticatorType' argument for authenticators.registerNew");
      }

      return new UserActionHandler(options, 'OneginiAuthenticatorRegistrationClient', 'start');
    },

    deregister: function (options, successCb, failureCb) {
      if (!options || !options.authenticatorType) {
        throw new TypeError("Onegini: missing 'authenticatorType' argument for authenticators.deregister");
      }
      return utils.promiseOrCallbackExec('OneginiAuthenticatorRegistrationClient', 'deregister', options, successCb, failureCb);
    }
  };

  function deregister(options, successCb, failureCb) {
    if (!options || !options.profileId) {
      throw new TypeError("Onegini: missing 'profileId' argument for deregister");
    }
    return utils.promiseOrCallbackExec('OneginiUserDeregistrationClient', 'deregister', options, successCb, failureCb);
  }

  function isUserRegistered(options, successCb, failureCb) {
    if (!options || !options.profileId) {
      throw new TypeError("Onegini: missing 'profileId' argument for isUserRegistered");
    }
    return utils.promiseOrCallbackExec('OneginiUserRegistrationClient', 'isUserRegistered', options, successCb, failureCb);
  }

  function getUserProfiles(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiUserRegistrationClient', 'getUserProfiles', [], successCb, failureCb);
  }

  function getAuthenticatedUserProfile(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'getAuthenticatedUserProfile', [], successCb, failureCb);
  }

  function getImplicitlyAuthenticatedUserProfile(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'getImplicitlyAuthenticatedUserProfile', [], successCb, failureCb);
  }

  function logout(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'logout', [], successCb, failureCb);
  }

  function validatePinWithPolicy(options, successCb, failureCb) {
    if (!options || !options.pin) {
      throw new TypeError("Onegini: missing 'pin' argument for validatePinWithPolicy");
    }
    return utils.promiseOrCallbackExec('OneginiUserClient', 'validatePinWithPolicy', options, successCb, failureCb);
  }

  function getIdentityProviders(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiIdentityProvidersClient', 'getIdentityProviders', [], successCb, failureCb);
  }

  function getAppToWebSingleSignOn(options, successCb, failureCb) {
    if (!options) {
      throw new TypeError("Onegini: missing 'targetUri' argument for App To Web Single Sign-On");
    }
    return utils.promiseOrCallbackExec("OneginiAppToWebSingleSignOnClient", 'start', options, successCb, failureCb);
  }

  return {
    authenticate: authenticate,
    authenticateImplicitly: authenticateImplicitly,
    register: register,
    changePin: changePin,
    authenticators: authenticators,
    deregister: deregister,
    isUserRegistered: isUserRegistered,
    getUserProfiles: getUserProfiles,
    getAuthenticatedUserProfile: getAuthenticatedUserProfile,
    getImplicitlyAuthenticatedUserProfile: getImplicitlyAuthenticatedUserProfile,
    logout: logout,
    validatePinWithPolicy: validatePinWithPolicy,
    getIdentityProviders: getIdentityProviders,
    getAppToWebSingleSignOn: getAppToWebSingleSignOn
  };
})();

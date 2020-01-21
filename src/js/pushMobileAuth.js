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

module.exports = (function () {
  var utils = require('./utils');

  function isUserEnrolled(options, successCb, failureCb) {
    options = utils.getOptionsWithDefaults(options, {}, 'profileId');
    if (!options || !options.profileId) {
      throw new TypeError("Onegini: missing 'profileId' argument for user.mobileAuth.push.isUserEnrolled");
    }

    return utils.promiseOrCallbackExec('OneginiPushMobileAuthClient', 'isEnrolled', options, successCb, failureCb);
  }

  function enroll(options, successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiPushMobileAuthClient', 'enroll', options, successCb, failureCb);
  }

  function canHandlePushMessage(pushData) {
    if (pushData) {
      var content = pushData.content ? pushData.content : pushData;
      var transactionId = content.og_transaction_id;
      return (typeof transactionId === "string" && transactionId.length > 0);
    }
    return false;
  }

  function handlePushMessage(options, successCb, failureCb) {
    var content = options.content ? options.content : options
    return utils.promiseOrCallbackExec('OneginiPushMobileAuthClient', 'handle', content, successCb, failureCb);
  }

  function getPendingRequests(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiPendingMobileAuthRequestClient', 'fetch', [], successCb, failureCb);
  }

  function handlePendingRequest(options, successCb, failureCb) {
      return utils.promiseOrCallbackExec('OneginiPendingMobileAuthRequestClient', 'handle', options, successCb, failureCb);
   }

  function PushMobileAuthHandler(method) {
    var self = this;
    this.callbacks = {};

    this.callbackActions = {
      accept: function (options) {
        options = utils.getOptionsWithDefaults(options, {}, 'pin');
        options.accept = true;
        utils.promiseOrCallbackExec('OneginiPushMobileAuthRequestClient', 'replyToChallenge', options, self.callbacks.onSuccess, self.callbacks.onError);
      },

      deny: function () {
        var options = {
          accept: false
        };

        utils.promiseOrCallbackExec('OneginiPushMobileAuthRequestClient', 'replyToChallenge', options, self.callbacks.onSuccess, self.callbacks.onError);
      }
    };

    function callSuccessCallback(options) {
      var event = options.pluginEvent;
      delete options.pluginEvent;

      if (self.callbacks[event]) {
        self.callbacks[event](self.callbackActions, options);
      }
    }

    function callErrorCallback(err) {
      self.callbacks.onError(err);
    }

    utils.callbackExec('OneginiPushMobileAuthRequestClient', 'registerChallengeReceiver', {method: method}, callSuccessCallback, callErrorCallback);
  }

  PushMobileAuthHandler.prototype.onConfirmationRequest = function (cb) {
    this.callbacks.onConfirmationRequest = cb;
    return this;
  };

  PushMobileAuthHandler.prototype.onPinRequest = function (cb) {
    this.callbacks.onPinRequest = cb;
    return this;
  };

  PushMobileAuthHandler.prototype.onFingerprintRequest = function (cb) {
    this.callbacks.onFingerprintRequest = cb;
    return this;
  };

  PushMobileAuthHandler.prototype.onFingerprintCaptured = function (cb) {
    this.callbacks.onFingerprintCaptured = cb;
    return this;
  };

  PushMobileAuthHandler.prototype.onFingerprintFailed = function (cb) {
    this.callbacks.onFingerprintFailed = cb;
    return this;
  };

  PushMobileAuthHandler.prototype.onError = function (cb) {
    this.callbacks.onError = cb;
    return this;
  };

  PushMobileAuthHandler.prototype.onSuccess = function (cb) {
    this.callbacks.onSuccess = cb;
    return this;
  };

  function on(method) {
    return new PushMobileAuthHandler(method);
  }

  return {
    isUserEnrolled: isUserEnrolled,
    enroll: enroll,
    canHandlePushMessage: canHandlePushMessage,
    handlePushMessage: handlePushMessage,
    getPendingRequests: getPendingRequests,
    handlePendingRequest: handlePendingRequest,
    on: on
  }
})();

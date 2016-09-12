module.exports = (function () {
  var utils = require('./utils');

  var authenticate = {
    start: function (options, successCb, failureCb) {
      if (!options || !options.profileId) {
        throw new TypeError("Onegini: missing 'profileId' argument for authenticate.start");
      }
      return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'start', options, successCb, failureCb);
    },

    providePin: function(options, successCb, failureCb) {
      if (!options || !options.pin) {
        throw new TypeError("Onegini: missing 'pin' argument for authenticate.providePin");
      }
      utils.callbackExec('OneginiUserAuthenticationClient', 'providePin', options, successCb, failureCb);
    }
  };

  var reauthenticate = {
    start: function (options, successCb, failureCb) {
      if (!options || !options.profileId) {
        throw new TypeError("Onegini: missing 'profileId' argument for reauthenticate.start");
      }
      return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'reauthenticate', options, successCb, failureCb);
    },

    providePin: function (options, successCb, failureCb) {
      if (!options || !options.pin) {
        throw new TypeError("Onegini: missing 'pin' argument for reauthenticate.providePin");
      }
      utils.callbackExec('OneginiUserAuthenticationClient', 'providePin', options, successCb, failureCb);
    }
  };

  var register = {
    start: function (options, successCb, failureCb) {
      return utils.promiseOrCallbackExec('OneginiUserRegistrationClient', 'start', options, successCb, failureCb);
    },

    createPin: function (options, successCb, failureCb) {
      if (!options || !options.pin) {
        throw new TypeError("Onegini: missing 'pin' argument for register.createPin");
      }
      utils.callbackExec('OneginiUserRegistrationClient', 'createPin', options, successCb, failureCb);
    }
  };

  var changePin = {
    start: function (options, successCb, failureCb) {
      if (!options || !options.pin) {
        throw new TypeError("Onegini: missing 'pin' argument for changePin.start");
      }
      return utils.promiseOrCallbackExec('OneginiChangePinClient', 'start', options, successCb, failureCb);
    },

    createPin: function (options, successCb, failureCb) {
      if (!options || !options.pin) {
        throw new TypeError("Onegini: missing 'pin' argument for changePin.createPin");
      }
      utils.callbackExec('OneginiChangePinClient', 'createPin', options, successCb, failureCb);
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

  function logout(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiUserAuthenticationClient', 'logout', [], successCb, failureCb);
  }

  function validatePinWithPolicy(options, successCb, failureCb) {
    if (!options || !options.pin) {
      throw new TypeError("Onegini: missing 'pin' argument for validatePinWithPolicy");
    }
    return utils.promiseOrCallbackExec('OneginiUserClient', 'validatePinWithPolicy', options, successCb, failureCb);
  }

  function getRegisteredAuthenticators(successCb, failureCb) {
    return utils.promiseOrCallbackExec('OneginiUserClient', 'getRegisteredAuthenticators', [], successCb, failureCb);
  }

  return {
    authenticate: authenticate,
    reauthenticate: reauthenticate,
    register: register,
    changePin: changePin,
    deregister: deregister,
    isUserRegistered: isUserRegistered,
    getUserProfiles: getUserProfiles,
    getAuthenticatedUserProfile: getAuthenticatedUserProfile,
    logout: logout,
    validatePinWithPolicy: validatePinWithPolicy,
    getRegisteredAuthenticators: getRegisteredAuthenticators
  };
})();
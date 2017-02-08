/*
 * Copyright (c) 2016 Onegini B.V.
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

#import "OGCDVAuthenticatorRegistrationClient.h"
#import "OGCDVConstants.h"
#import "OGCDVAuthenticatorsClientHelper.h"

@interface OGCDVAuthenticatorRegistrationClient ()<ONGAuthenticatorRegistrationDelegate>
@end

@implementation OGCDVAuthenticatorRegistrationClient {
}

- (void)start:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [[ONGUserClient sharedInstance] authenticatedUserProfile];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoUserAuthenticated andMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
            return;
        }

        self.authenticationCallbackId = command.callbackId;

        NSDictionary *options = command.arguments[0];
        NSSet<ONGAuthenticator *> *nonRegisteredAuthenticators = [[ONGUserClient sharedInstance] nonRegisteredAuthenticatorsForUser:user];
        ONGAuthenticator *authenticator = [OGCDVAuthenticatorsClientHelper authenticatorFromArguments:nonRegisteredAuthenticators options:options];

        if (authenticator == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoSuchAuthenticator andMessage:OGCDVPluginErrDescriptionNoSuchAuthenticator];
            return;
        }

        [[ONGUserClient sharedInstance] registerAuthenticator:authenticator delegate:self];
    }];
}

- (void)providePin:(CDVInvokedUrlCommand *)command
{
    if (!self.pinChallenge) {
        [self sendErrorResultForCallbackId:command.callbackId
                             withErrorCode:OGCDVPluginErrCodeProvidePinNoAuthenticationInProgress
                                andMessage:OGCDVPluginErrDescriptionProvidePinNoAuthenticationInProgress];
        return;
    }

    NSDictionary *options = command.arguments[0];
    NSString *pin = options[OGCDVPluginKeyPin];
    [self.pinChallenge.sender respondWithPin:pin challenge:self.pinChallenge];
}

- (void)deregister:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        ONGUserProfile *user = [[ONGUserClient sharedInstance] authenticatedUserProfile];
        if (user == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withMessage:OGCDVPluginErrDescriptionNoUserAuthenticated];
            return;
        }

        NSDictionary *options = command.arguments[0];
        NSSet<ONGAuthenticator *> *registeredAuthenticators = [[ONGUserClient sharedInstance] registeredAuthenticatorsForUser:user];
        ONGAuthenticator *authenticator = [OGCDVAuthenticatorsClientHelper authenticatorFromArguments:registeredAuthenticators options:options];

        if (authenticator == nil) {
            [self sendErrorResultForCallbackId:command.callbackId withErrorCode:OGCDVPluginErrCodeNoSuchAuthenticator andMessage:OGCDVPluginErrDescriptionNoSuchAuthenticator];
            return;
        }

        [[ONGUserClient sharedInstance] deregisterAuthenticator:authenticator completion:^(BOOL deregistered, NSError *_Nullable error) {
            if (error || !deregistered) {
                [self sendErrorResultForCallbackId:command.callbackId withError:error];
            } else {
                [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
            }
        }];
    }];
}

#pragma mark - ONGAuthenticatorRegistrationDelegate

- (void)userClient:(ONGUserClient *)userClient didRegisterAuthenticator:(ONGAuthenticator *)authenticator forUser:(ONGUserProfile *)userProfile
{
    NSDictionary *message = @{
        OGCDVPluginKeyAuthenticationEvent: OGCDVPluginAuthEventSuccess
    };
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];

    [self.commandDelegate sendPluginResult:result callbackId:self.authenticationCallbackId];
}

- (void)userClient:(ONGUserClient *)userClient didFailToRegisterAuthenticator:(ONGAuthenticator *)authenticator forUser:(ONGUserProfile *)userProfile error:(NSError *)error
{
    [self sendErrorResultForCallbackId:self.authenticationCallbackId withError:error];
}

@end
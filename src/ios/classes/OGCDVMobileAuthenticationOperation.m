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

#import "OGCDVMobileAuthenticationOperation.h"
#import "OGCDVConstants.h"

@implementation OGCDVMobileAuthenticationOperation {
}

@synthesize mobileAuthenticationRequest;
@synthesize mobileAuthenticationMethod;
@synthesize completeOperationCallbackId;
@synthesize pinChallenge;
@synthesize fingerprintChallenge;

- (id)initWithConfirmationChallenge:(void (^)(BOOL confirmRequest))confirmation
                         forRequest:(ONGMobileAuthenticationRequest *)request
                          forMethod:(NSString *)method
{
    if (![super init]) {
        return nil;
    }

    [self initOperationWithRequest:request forMethod:method];
    [self setConfirmationChallengeConfirmationBlock:confirmation];

    return self;
}

- (id)initWithPinChallenge:(ONGPinChallenge *)challenge
                forRequest:(ONGMobileAuthenticationRequest *)request
                 forMethod:(NSString *)method;
{
    if (![super init]) {
        return nil;
    }

    [self initOperationWithRequest:request forMethod:method];
    [self setPinChallenge:challenge];

    return self;
}

- (id)initWithFingerprintChallenge:(ONGFingerprintChallenge *)challenge
                        forRequest:(ONGMobileAuthenticationRequest *)request
                         forMethod:(NSString *)method;
{
    if (![super init]) {
        return nil;
    }

    [self initOperationWithRequest:request forMethod:method];
    [self setFingerprintChallenge:challenge];

    return self;
}

- (void)initOperationWithRequest:(ONGMobileAuthenticationRequest *)request forMethod:(NSString *)method
{
    self.qualityOfService = NSOperationQualityOfServiceBackground;

    [self setMobileAuthenticationMethod:method];
    [self setMobileAuthenticationRequest:request];

}

- (void)start
{
    if ([self isCancelled]) {
        [self willChangeValueForKey:@"isFinished"];
        self._finished = YES;
        [self didChangeValueForKey:@"isFinished"];
        return;
    }

    [self willChangeValueForKey:@"isExecuting"];
    self._executing = YES;
    [self didChangeValueForKey:@"isExecuting"];

    [[OGCDVMobileAuthenticationRequestClient sharedInstance] performSelectorOnMainThread:@selector(setDelegate:)
                                                                              withObject:self
                                                                           waitUntilDone:YES];

    NSDictionary *challengeReceiversCallbackIds = [[OGCDVMobileAuthenticationRequestClient sharedInstance] challengeReceiversCallbackIds];
    NSString *challengeReceiverCallbackId = challengeReceiversCallbackIds[mobileAuthenticationMethod];

    if (challengeReceiverCallbackId) {
        [self sendChallenge:challengeReceiverCallbackId];
    } else {
        [[[OGCDVMobileAuthenticationRequestClient sharedInstance] challengeReceiversCallbackIds] addObserver:self
                                                                                                  forKeyPath:mobileAuthenticationMethod
                                                                                                     options:NSKeyValueObservingOptionNew
                                                                                                     context:NULL];
    }
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context
{
    if ([keyPath isEqualToString:mobileAuthenticationMethod]) {
        [[[OGCDVMobileAuthenticationRequestClient sharedInstance] challengeReceiversCallbackIds] removeObserver:self forKeyPath:mobileAuthenticationMethod];
        NSString *challengeReceiverCallbackId = change[NSKeyValueChangeNewKey];
        [self sendChallenge:challengeReceiverCallbackId];
    }
}

- (void)sendChallenge:(NSString *)callbackId
{
    NSMutableDictionary *message = [[NSMutableDictionary alloc] init];
    message[OGCDVPluginKeyType] = mobileAuthenticationRequest.type;
    message[OGCDVPluginKeyMessage] = mobileAuthenticationRequest.message;
    message[OGCDVPluginKeyProfileId] = mobileAuthenticationRequest.userProfile.profileId;
    message[OGCDVPluginKeyAuthenticationEvent] = [[OGCDVMobileAuthenticationRequestClient sharedInstance] authenticationEventsForMethods][mobileAuthenticationMethod];

    if (pinChallenge) {
        message[OGCDVPluginKeyMaxFailureCount] = @(pinChallenge.maxFailureCount);
        message[OGCDVPluginKeyRemainingFailureCount] = @(pinChallenge.remainingFailureCount);
    }

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:message];
    [pluginResult setKeepCallbackAsBool:YES];

    [[[OGCDVMobileAuthenticationRequestClient sharedInstance] commandDelegate] sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)mobileAuthenticationRequestClient:(OGCDVMobileAuthenticationRequestClient *)mobileAuthenticationRequestClient
  didReceiveConfirmationChallengeResponse:(BOOL)accept withCallbackId:(NSString *)callbackId
{
    [self setCompleteOperationCallbackId:callbackId];
    [self confirmationChallengeConfirmationBlock](accept);
}

- (void)mobileAuthenticationRequestClient:(OGCDVMobileAuthenticationRequestClient *)mobileAuthenticationRequestClient
           didReceivePinChallengeResponse:(BOOL)accept withPin:(NSString *)pin withCallbackId:(NSString *)callbackId
{
    [self setCompleteOperationCallbackId:callbackId];

    if (accept) {
        [self.pinChallenge.sender respondWithPin:pin challenge:pinChallenge];
    } else {
        [self.pinChallenge.sender cancelChallenge:pinChallenge];
    }
}

- (void)mobileAuthenticationRequestClient:(OGCDVMobileAuthenticationRequestClient *)mobileAuthenticationRequestClient
   didReceiveFingerprintChallengeResponse:(BOOL)accept withPrompt:(NSString *)prompt withCallbackId:(NSString *)callbackId
{
    [self setCompleteOperationCallbackId:callbackId];

    if (!accept) {
        [self.fingerprintChallenge.sender cancelChallenge:fingerprintChallenge];
        return;
    }

    if (prompt == nil) {
        [self.fingerprintChallenge.sender respondWithDefaultPromptForChallenge:fingerprintChallenge];
    } else {
        [self.fingerprintChallenge.sender respondWithPrompt:prompt challenge:fingerprintChallenge];
    }
}

- (BOOL)isAsynchronous
{
    return YES;
}

- (BOOL)isExecuting
{
    return self._executing;
}

- (BOOL)isFinished
{
    return self._finished;
}

- (void)completeOperation
{
    [self willChangeValueForKey:@"isFinished"];
    [self willChangeValueForKey:@"isExecuting"];

    self._executing = NO;
    self._finished = YES;

    [self didChangeValueForKey:@"isFinished"];
    [self didChangeValueForKey:@"isExecuting"];
}

@end
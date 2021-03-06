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

#import "CDVPlugin+OGCDV.h"
@import OneginiSDKiOS;

@interface OGCDVChangePinClient : CDVPlugin<ONGChangePinDelegate>

@property (nonatomic, copy) NSString *startCallbackId;

@property (nonatomic) ONGPinChallenge *pinChallenge;
@property (nonatomic) ONGCreatePinChallenge *createPinChallenge;

- (void)start:(CDVInvokedUrlCommand *)command;
- (void)providePin:(CDVInvokedUrlCommand *)command;
- (void)createPin:(CDVInvokedUrlCommand *)command;
- (void)cancelFlow:(CDVInvokedUrlCommand *)command;

@end

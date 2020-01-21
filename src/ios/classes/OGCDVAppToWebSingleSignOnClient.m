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

#import "OGCDVAppToWebSingleSignOnClient.h"

@implementation OGCDVAppToWebSingleSignOnClient

- (void)start:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        NSString *targetUrlString = command.arguments[0];
        NSURL *targetUrl = [[NSURL alloc] initWithString:targetUrlString];
        [[ONGUserClient sharedInstance] appToWebSingleSignOnWithTargetUrl:targetUrl
                                                               completion:^(NSURL * _Nullable redirectUrl, NSString * _Nullable token, NSError * _Nullable error) {
                                                                   if (error != nil) {
                                                                       [self sendErrorResultForCallbackId:command.callbackId withError:error];
                                                                   } else {
                                                                       NSDictionary *payload = @{
                                                                                                 @"token": token ? token : @"",
                                                                                                 @"redirectUri": redirectUrl ? redirectUrl.absoluteString : @""
                                                                                                 };
                                                                       CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:payload];
                                                                       [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                                                                   }
        }];
    }];
}

@end

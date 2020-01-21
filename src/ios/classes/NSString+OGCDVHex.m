/*
 * Copyright (c) 2019 Onegini B.V.
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

#import "NSString+OGCDVHex.h"

@implementation NSString (OGCDVHex)

- (NSData *)ogcdv_dataFromHexString
{
    NSMutableString *formattedString = [self mutableCopy];
    [formattedString replaceOccurrencesOfString:@" " withString:@"" options:NO range:NSMakeRange(0, formattedString.length)];
    
    NSMutableData *data = [NSMutableData dataWithCapacity:self.length / 2];
    
    NSRange range;
    range.length = 2;
    char byteChars[3] = {'\0', '\0', '\0'};
    
    for (int i = 0; i < formattedString.length; i += 2) {
        range.location = i;
        NSString *subString = [formattedString substringWithRange:range];
        byteChars[0] = [subString characterAtIndex:0];
        byteChars[1] = [subString characterAtIndex:1];
        
        unsigned long wholeByte = strtoul(byteChars, NULL, 16);
        [data appendBytes:&wholeByte length:1];
    }
    
    return data;
}

@end

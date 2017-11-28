//
//  ESFirebase.m
//  Tabris.js Firebase Plugin Example
//
//  Created by Patryk Mol on 13.11.17.
//

#import "ESFirebaseHelper.h"
#import <FirebaseAnalytics/FirebaseAnalytics.h>

@implementation ESFirebaseHelper

+ (void)setup {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if ([[NSBundle mainBundle] pathForResource:@"GoogleService-Info" ofType:@"plist"]) {
            [FIRApp configure];
        }
    });
}

@end

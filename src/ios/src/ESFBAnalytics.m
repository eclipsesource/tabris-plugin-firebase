//
//  ESFBAnalytics.m
//  Tabris Plugin Firebase
//
//  Created by Patryk Mol on 13.06.17.
//  Copyright (c) 2017 EclipseSource. All rights reserved.
//

#import "ESFBAnalytics.h"
#import <FirebaseAnalytics/FirebaseAnalytics.h>
#import "ESFirebaseHelper.h"

@implementation ESFBAnalytics

@synthesize analyticsCollectionEnabled = _analyticsCollectionEnabled;
@synthesize screenName = _screenName;
@synthesize userId = _userId;

- (instancetype)initWithObjectId:(NSString *)objectId properties:(NSDictionary *)properties andClient:(TabrisClient *)client {
    self = [super initWithObjectId:objectId properties:properties andClient:client];
    if (self) {
        [self registerSelector:@selector(logEvent:) forCall:@"logEvent"];
        [self registerSelector:@selector(setUserProperty:) forCall:@"setUserProperty"];
    }
    return self;
}

+ (NSMutableSet *)remoteObjectProperties {
    NSMutableSet *properties = [super remoteObjectProperties];
    return properties;
}

+ (NSString *)remoteObjectType {
    return @"com.eclipsesource.firebase.Analytics";
}

- (UIView *)view {
    return nil;
}

+ (void)setup {
    [ESFirebaseHelper setup];
}

- (NSString *)screenName {
    return _screenName;
}

- (void)setScreenName:(NSString *)screenName {
    _screenName = screenName;
    [FIRAnalytics setScreenName:screenName screenClass:screenName];
}

- (NSString *)userId {
    return _userId;
}

- (void)setUserId:(NSString *)userId {
    _userId = userId;
    [FIRAnalytics setUserID:userId];
}

- (void)logEvent:(NSDictionary *)properties {
    if (self.analyticsCollectionEnabled) {
        NSString *name = [properties objectForKey:@"name"];
        NSDictionary *data = [properties objectForKey:@"data"];
        [FIRAnalytics logEventWithName:name parameters:data];
    }
}

- (void)setUserProperty:(NSDictionary *)properties {
    if (self.analyticsCollectionEnabled) {
        NSString *key = [properties objectForKey:@"key"];
        NSString *value = [properties objectForKey:@"value"];
        [FIRAnalytics setUserPropertyString:value forName:key];
    }
}

@end

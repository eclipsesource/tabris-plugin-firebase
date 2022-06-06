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
@synthesize userId = _userId;

- (instancetype)initWithObjectId:(NSString *)objectId properties:(NSDictionary *)properties inContext:(id<TabrisContext>)context {
    self = [super initWithObjectId:objectId properties:properties inContext:context];
    if (self) {
        self.analyticsCollectionEnabled = NO;
        [self registerSelector:@selector(logEvent:) forCall:@"logEvent"];
        [self registerSelector:@selector(setUserProperty:) forCall:@"setUserProperty"];
    }
    return self;
}

+ (NSMutableSet *)remoteObjectProperties {
    NSMutableSet *properties = [super remoteObjectProperties];
    [properties addObject:@"analyticsCollectionEnabled"];
    [properties addObject:@"userId"];
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
    [FIRAnalytics setAnalyticsCollectionEnabled:NO];
}

- (void)setAnalyticsCollectionEnabled:(BOOL)analyticsCollectionEnabled {
    _analyticsCollectionEnabled = analyticsCollectionEnabled;
    [FIRAnalytics setAnalyticsCollectionEnabled:analyticsCollectionEnabled];
}

- (BOOL)analyticsCollectionEnabled {
    return _analyticsCollectionEnabled;
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

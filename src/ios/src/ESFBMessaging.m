//
//  ESFBMessaging.m
//  firebase
//
//  Created by Patryk Mol on 19.06.17.
//
//

#import "ESFBMessaging.h"
#import <FirebaseCore/FirebaseCore.h>
#import <FirebaseMessaging/FirebaseMessaging.h>
#import <FirebaseInstanceID/FirebaseInstanceID.h>
#import <UserNotifications/UserNotifications.h>
#import "ESFirebaseHelper.h"

// Set to 1 to log received notifications and tokens to NSLog and the JS console.
#ifndef ESFB_VERBOSE_LOGGING
#define ESFB_VERBOSE_LOGGING 0
#endif

#if ESFB_VERBOSE_LOGGING
#import <Tabris/LogEntry.h>
#endif

@interface ESFBMessaging () <UNUserNotificationCenterDelegate, FIRMessagingDelegate>
@property (strong, readwrite) NSString *instanceId;
@end

@implementation ESFBMessaging

@synthesize instanceId = _instanceId;
@synthesize token = _token;
@synthesize launchData = _launchData;
@synthesize tokenChangedListener = _tokenChangedListener;
@synthesize instanceIdChangedListener = _instanceIdChangedListener;
@synthesize messageListener = _messageListener;
@synthesize showBannersInForeground = _showBannersInForeground;

NSString *const kGCMMessageIDKey = @"gcm.message_id";
static NSDictionary *launchData;

- (instancetype)initWithObjectId:(NSString *)objectId properties:(NSDictionary *)properties inContext:(id<TabrisContext>)context {
    self = [super initWithObjectId:objectId properties:properties inContext:context];
    if (self) {
        [UNUserNotificationCenter currentNotificationCenter].delegate = self;
        [FIRMessaging messaging].delegate = self;
        [self registerSelector:@selector(resetInstanceId) forCall:@"resetInstanceId"];
        [self registerSelector:@selector(registerForNotifications) forCall:@"requestPermissions"];
        [self registerSelector:@selector(getAllPendingMessages) forCall:@"getAllPendingMessages"];
        [self registerSelector:@selector(clearAllPendingMessages) forCall:@"clearAllPendingMessages"];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(instanceIdRefreshed:) name:kFIRInstanceIDTokenRefreshNotification object:nil];
        __weak ESFBMessaging *weakSelf = self;
        [[FIRInstanceID instanceID] instanceIDWithHandler:^(FIRInstanceIDResult * _Nullable result, NSError * _Nullable error) {
            if (!error) {
                weakSelf.instanceId = result.token;
            }
        }];
    }
    return self;
}

- (void)destroy {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super destroy];
}

+ (NSMutableSet *)remoteObjectProperties {
    NSMutableSet *properties = [super remoteObjectProperties];
    [properties addObject:@"instanceId"];
    [properties addObject:@"token"];
    [properties addObject:@"apnsToken"];
    [properties addObject:@"launchData"];
    [properties addObject:@"tokenChangedListener"];
    [properties addObject:@"instanceIdChangedListener"];
    [properties addObject:@"messageListener"];
    [properties addObject:@"showBannersInForeground"];
    return properties;
}

+ (NSString *)remoteObjectType {
    return @"com.eclipsesource.firebase.Messaging";
}

+ (void)setLaunchData:(NSDictionary *)launchOptions {
    launchData = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
}

- (NSDictionary *)launchData {
    return launchData;
}

- (UIView *)view {
    return nil;
}

+ (void)setup {
    [ESFirebaseHelper setup];
}

- (NSArray *)getAllPendingMessages {
    __block NSArray *array;
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_enter(group);
    [[UNUserNotificationCenter currentNotificationCenter] getDeliveredNotificationsWithCompletionHandler:^(NSArray<UNNotification *> * _Nonnull notifications) {
        if (notifications.count > 0) {
            NSMutableArray *dictionaries = [NSMutableArray arrayWithCapacity:notifications.count];
            for (UNNotification *notification in notifications) {
                [dictionaries addObject:notification.request.content.userInfo];
            }
            array = [[[dictionaries reverseObjectEnumerator] allObjects] copy];
        }
        dispatch_group_leave(group);
    }];
    dispatch_group_wait(group, dispatch_time(DISPATCH_TIME_NOW,NSEC_PER_SEC * 1));
    return array;
}

- (void)clearAllPendingMessages {
    [[UNUserNotificationCenter currentNotificationCenter] removeAllDeliveredNotifications];
}

- (NSString *)token {
    return [FIRMessaging messaging].FCMToken;
}

- (NSString *)apnsToken {
    NSData *apnsToken = [FIRMessaging messaging].APNSToken;
    if (apnsToken) {
        const unsigned char *dataBuffer = (const unsigned char *)[apnsToken bytes];
        if (!dataBuffer) {
            return nil;
        }
        NSMutableString *hexString = [NSMutableString stringWithCapacity:([apnsToken length] * 2)];
        for (int i = 0; i < [apnsToken length]; ++i) {
            [hexString appendString:[NSString stringWithFormat:@"%02lx", (unsigned long)dataBuffer[i]]];
        }
        return [NSString stringWithString:hexString];
    }
    return nil;
}

- (void)registerForNotifications {
    UNAuthorizationOptions authOptions = UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
    [[UNUserNotificationCenter currentNotificationCenter] requestAuthorizationWithOptions:authOptions completionHandler:^(BOOL granted, NSError * _Nullable error) {}];
    [[UIApplication sharedApplication] registerForRemoteNotifications];
}

- (void)resetInstanceId {
    [[FIRInstanceID instanceID] deleteIDWithHandler:^(NSError * _Nullable error) {}];
}

- (void)sendMessage:(NSDictionary *)data {
    if (self.messageListener) {
        [self fireEventNamed:@"message" withAttributes:@{@"data":data}];
    }
}

#if ESFB_VERBOSE_LOGGING
- (void)logToConsole:(NSString *)message {
    if (self.context && self.context.console) {
        LogEntry *entry = [LogEntry entryWithInfo:message];
        [self.context.console display:entry];
    }
}
#endif

#pragma mark - UNUserNotificationCenterDelegate

- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response
         withCompletionHandler:(void (^)(void))completionHandler {
    NSDictionary *userInfo = response.notification.request.content.userInfo;
#if ESFB_VERBOSE_LOGGING
    UNNotificationContent *content = response.notification.request.content;

    NSLog(@"📱 NOTIFICATION TAPPED:");
    NSLog(@"  Title: %@", content.title ?: @"(no title)");
    NSLog(@"  Body: %@", content.body ?: @"(no body)");
    NSLog(@"  Badge: %@", content.badge ?: @"(no badge)");
    NSLog(@"  Category: %@", content.categoryIdentifier ?: @"(no category)");
    NSLog(@"  Sound: %@", content.sound ? content.sound.description : @"(no sound)");
    NSLog(@"  UserInfo: %@", userInfo);
    NSLog(@"  Response Action: %@", response.actionIdentifier);
    
    // Send notification details to JS console
    NSString *consoleMessage = [NSString stringWithFormat:@"📱 NOTIFICATION TAPPED: %@ | %@ | UserInfo: %@", 
                               content.title ?: @"(no title)", 
                               content.body ?: @"(no body)", 
                               userInfo];
    [self logToConsole:consoleMessage];
#endif

    if (userInfo != launchData) {
        [self sendMessage:userInfo];
    }
    completionHandler();
}
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
    NSDictionary *userInfo = notification.request.content.userInfo;
#if ESFB_VERBOSE_LOGGING
    UNNotificationContent *content = notification.request.content;

    NSLog(@"🔔 NOTIFICATION RECEIVED (APP IN FOREGROUND):");
    NSLog(@"  Title: %@", content.title ?: @"(no title)");
    NSLog(@"  Body: %@", content.body ?: @"(no body)");
    NSLog(@"  Badge: %@", content.badge ?: @"(no badge)");
    NSLog(@"  Category: %@", content.categoryIdentifier ?: @"(no category)");
    NSLog(@"  Sound: %@", content.sound ? content.sound.description : @"(no sound)");
    NSLog(@"  UserInfo: %@", userInfo);
    NSLog(@"  Will show banner: %@", self.showBannersInForeground ? @"YES" : @"NO");
    
    // Send notification details to JS console
    NSString *consoleMessage = [NSString stringWithFormat:@"🔔 NOTIFICATION RECEIVED (FOREGROUND): %@ | %@ | UserInfo: %@", 
                               content.title ?: @"(no title)", 
                               content.body ?: @"(no body)", 
                               userInfo];
    [self logToConsole:consoleMessage];
#endif

    [self sendMessage:userInfo];
    UNNotificationPresentationOptions presentation = self.showBannersInForeground ? UNNotificationPresentationOptionAlert : UNNotificationPresentationOptionNone;
    completionHandler(presentation);
}

#pragma mark - FIRMessagingDelegate

- (void)messaging:(FIRMessaging *)messaging didReceiveRegistrationToken:(NSString *)fcmToken {
#if ESFB_VERBOSE_LOGGING
    NSLog(@"🔑 FCM TOKEN UPDATED:");
    NSLog(@"  FCM Token: %@", fcmToken ? fcmToken : @"nil");
    NSString *apns = [self apnsToken];
    NSLog(@"  APNS Token: %@", apns ? apns : @"nil");
    
    // Send token info to JS console
    NSString *consoleMessage = [NSString stringWithFormat:@"🔑 FCM TOKEN UPDATED: %@", fcmToken ? fcmToken : @"nil"];
    [self logToConsole:consoleMessage];
    
    if (apns) {
        NSString *apnsMessage = [NSString stringWithFormat:@"📱 APNS TOKEN: %@", apns];
        [self logToConsole:apnsMessage];
    }
#endif

    if (self.tokenChangedListener) {
        [self fireEventNamed:@"tokenChanged" withAttributes:@{@"token":fcmToken ? fcmToken : [NSNull null]}];
    }
}

- (void)instanceIdRefreshed:(NSNotification *)notification {
    if (self.instanceIdChangedListener) {
        [self fireEventNamed:@"instanceIdChanged" withAttributes:@{@"instanceId":self.instanceId}];
    }
}

@end

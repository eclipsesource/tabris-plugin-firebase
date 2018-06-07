//
//  ESFBMessaging.m
//  firebase
//
//  Created by Patryk Mol on 19.06.17.
//
//

#import "ESFBMessaging.h"
#import <FirebaseMessaging/FirebaseMessaging.h>
#import <FirebaseInstanceID/FirebaseInstanceID.h>
#import <UserNotifications/UserNotifications.h>
#import "ESFirebaseHelper.h"

NSString *const kGCMMessageIDKey = @"gcm.message_id";

@interface ESFBMessagingDelegate : NSObject <FIRMessagingDelegate>
@property (weak) ESFBMessaging *instance;
- (void)registerForNotifications;
@end

#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
#import <UserNotifications/UserNotifications.h>

@interface ESFBMessagingDelegate () <UNUserNotificationCenterDelegate>
@end
#endif

@interface ESFBMessaging ()
- (void)sendMessage:(NSDictionary *)data;
@end

@implementation ESFBMessaging

@synthesize instanceId = _instanceId;
@synthesize token = _token;
@synthesize launchData = _launchData;
@synthesize tokenChangedListener = _tokenChangedListener;
@synthesize instanceIdChangedListener = _instanceIdChangedListener;
@synthesize messageListener = _messageListener;

static ESFBMessagingDelegate *messagingDelegate;
static NSDictionary *launchData;

- (instancetype)initWithObjectId:(NSString *)objectId properties:(NSDictionary *)properties andClient:(TabrisClient *)client {
    self = [super initWithObjectId:objectId properties:properties andClient:client];
    if (self) {
        messagingDelegate.instance = self;
        [self registerSelector:@selector(resetInstanceId) forCall:@"resetInstanceId"];
        [self registerSelector:@selector(registerForNotifications) forCall:@"requestPermissions"];
    }
    return self;
}

+ (NSMutableSet *)remoteObjectProperties {
    NSMutableSet *properties = [super remoteObjectProperties];
    [properties addObject:@"instanceId"];
    [properties addObject:@"token"];
    [properties addObject:@"launchData"];
    [properties addObject:@"tokenChangedListener"];
    [properties addObject:@"instanceIdChangedListener"];
    [properties addObject:@"messageListener"];
    return properties;
}

+ (NSString *)remoteObjectType {
    return @"com.eclipsesource.firebase.Messaging";
}

+ (void)notificationReceived:(NSDictionary *)data {
    if (launchData != data) {
        [messagingDelegate.instance sendMessage:data];
    }
}

+ (void)setLaunchData:(NSDictionary *)launchOptions {
    NSDictionary *data = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (data) {
        launchData = data;
    }
}

- (NSDictionary *)launchData {
    return launchData;
}

- (UIView *)view {
    return nil;
}

+ (void)setup {
    [ESFirebaseHelper setup];
    messagingDelegate = [ESFBMessagingDelegate new];
}

- (NSString *)instanceId {
    return [FIRInstanceID instanceID].token;
}

- (NSString *)token {
    return [FIRMessaging messaging].FCMToken;
}

- (void)registerForNotifications {
    [messagingDelegate registerForNotifications];
}

- (void)resetInstanceId {
    NSString *apnsToken = [[FIRMessaging messaging].APNSToken base64EncodedStringWithOptions:0];
    NSString *path = [[NSBundle mainBundle] pathForResource: @"GoogleService-Info" ofType: @"plist"];
    NSDictionary *plist = [NSDictionary dictionaryWithContentsOfFile: path];
    __weak ESFBMessaging *weakSelf = self;
    [[FIRInstanceID instanceID] tokenWithAuthorizedEntity:[plist objectForKey:@"GCM_SENDER_ID"] scope:kFIRInstanceIDScopeFirebaseMessaging options:@{@"apns_token":apnsToken} handler:^(NSString * _Nullable token, NSError * _Nullable error) {
        if (!error && token) {
            __strong ESFBMessaging *strongSelf = weakSelf;
            [strongSelf messaging:nil didRefreshRegistrationToken:token];
        }
    }];
}

- (void)messaging:(FIRMessaging *)messaging didRefreshRegistrationToken:(NSString *)fcmToken {
    if (self.tokenChangedListener) {
        Message<Notification> *message = [[self notifications] forObject:self];
        [message fireEvent:@"tokenChanged" withAttributes:@{@"token":fcmToken}];
    }
}

- (void)messaging:(FIRMessaging *)messaging didReceiveMessage:(FIRMessagingRemoteMessage *)remoteMessage {
    [self sendMessage:remoteMessage.appData];
}

- (void)instanceIdRefreshed:(NSNotification *)notification {
    if (self.instanceIdChangedListener) {
        Message<Notification> *message = [[self notifications] forObject:self];
        [message fireEvent:@"instanceIdChanged" withAttributes:@{@"instanceId":[FIRInstanceID instanceID].token}];
    }
}

- (void)sendMessage:(NSDictionary *)data {
    if (self.messageListener) {
        Message<Notification> *message = [[self notifications] forObject:self];
        [message fireEvent:@"message" withAttributes:@{@"data":data}];
    }
}

@end

@implementation ESFBMessagingDelegate

- (void)registerForNotifications {
    [FIRMessaging messaging].delegate = self;
    [FIRMessaging messaging].shouldEstablishDirectChannel = YES;
    if (floor(NSFoundationVersionNumber) <= NSFoundationVersionNumber_iOS_9_x_Max) {
        UIUserNotificationType allNotificationTypes =
        (UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge);
        UIUserNotificationSettings *settings =
        [UIUserNotificationSettings settingsForTypes:allNotificationTypes categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:settings];
    } else {
#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
        [UNUserNotificationCenter currentNotificationCenter].delegate = self;
        UNAuthorizationOptions authOptions = UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
        [[UNUserNotificationCenter currentNotificationCenter] requestAuthorizationWithOptions:authOptions completionHandler:^(BOOL granted, NSError * _Nullable error) {}];
#endif
    }
    [[UIApplication sharedApplication] registerForRemoteNotifications];
}

- (void)messaging:(FIRMessaging *)messaging didRefreshRegistrationToken:(NSString *)fcmToken {
    [self.instance messaging:messaging didRefreshRegistrationToken:fcmToken];
}

- (void)messaging:(FIRMessaging *)messaging didReceiveMessage:(FIRMessagingRemoteMessage *)remoteMessage {
     [self.instance messaging:messaging didReceiveMessage:remoteMessage];
}

- (void)instanceIdRefreshed:(NSNotification *)notification {
    [self.instance instanceIdRefreshed:notification];
}

- (void)applicationReceivedRemoteMessage:(FIRMessagingRemoteMessage *)remoteMessage {
    [self.instance messaging:nil didReceiveMessage:remoteMessage];
}

#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
    NSDictionary *userInfo = notification.request.content.userInfo;
    [self.instance sendMessage:userInfo];
    completionHandler(UNNotificationPresentationOptionNone);
}
#endif

@end

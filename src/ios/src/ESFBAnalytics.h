//
//  ESFBAnalytics.h
//  Tabris Plugin Firebase
//
//  Created by Patryk Mol on 13.06.17.
//  Copyright (c) 2017 EclipseSource. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Tabris/BasicWidget.h>

@interface ESFBAnalytics : BasicWidget
@property (nonatomic) BOOL analyticsCollectionEnabled;
@property (nonatomic) NSString *screenName;
@property (nonatomic) NSString *userId;
@end

#!/usr/bin/env node

var fs = require("fs"),
    path = require("path");

var rootdir = process.argv[2];
if (rootdir) {

  module.exports = function(context) {

    var cordova_util = context.requireCordovaModule("cordova-lib/src/cordova/util"),
        ConfigParser = context.requireCordovaModule("cordova-common").ConfigParser,
        projectRoot = cordova_util.isCordova(),
        xml = cordova_util.projectConfig(projectRoot),
        cfg = new ConfigParser(xml);
        
    
    var getProjectFile = function(platform, relPath) {
      return path.join(projectRoot, "platforms", platform, cfg.name(), relPath);
    };

    var replace = function(path, to_replace, replace_with) {
      var data = fs.readFileSync(path, "utf8");
      var result = data.replace(to_replace, replace_with);
      fs.writeFileSync(path, result, "utf8");
    };

    var updateIOSAppDelegate = function() {
      var appDelegate = getProjectFile("ios", "Classes/AppDelegate.m");
      var importReplace = "/* HOOK: import classes for registration */";
      var finishLaunchingReplace = "/* HOOK: applicationDidFinishLaunching */";
      replace(appDelegate, importReplace, "#import \"ESFBMessaging.h\"\n" + importReplace);
      replace(appDelegate, finishLaunchingReplace, "[ESFBMessaging setLaunchData:launchOptions];\n\t" + finishLaunchingReplace);
      replace(appDelegate, "@end", "- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(nonnull void (^)(UIBackgroundFetchResult))completionHandler {" + "\n\t" + "[ESFBMessaging notificationReceived:userInfo];" + "\n\t" + "completionHandler(UIBackgroundFetchResultNewData);\n}" + "\n\n" + "@end")
    };

    updateIOSAppDelegate();

  };

}

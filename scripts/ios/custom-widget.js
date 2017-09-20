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
      var registerReplace = "/* HOOK: tabrisClientWillStartExecuting */";
      replace(appDelegate, importReplace, importReplace + "\n#import <FirebaseAnalytics/FirebaseAnalytics.h>");
      replace(appDelegate, importReplace, importReplace + "\n#import \"ESFBAnalytics.h\"");
      replace(appDelegate, registerReplace, "[self.client addRemoteObject:[ESFBAnalytics class]];" + "\n\t" + registerReplace);
      replace(appDelegate, registerReplace, registerReplace + "\n\t[FIRApp configure];");
    };

    updateIOSAppDelegate();

  };

}

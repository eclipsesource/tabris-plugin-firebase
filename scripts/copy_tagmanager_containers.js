'use strict';

const fs = require('fs');
const path = require('path');

const ANDROID_PLATFORM = 'platforms/android';
const IOS_PLATFORM = 'platforms/ios';

module.exports = function(ctx) {
  let platform = ctx.opts.cordova.platforms[0];
  let containerSourceDir = platform === 'android' ? 'tagmanager-container/android' : 'tagmanager-container/ios';
  let containerTargetDir = platform === 'android' ? `${ANDROID_PLATFORM}/assets/containers` : `${IOS_PLATFORM}/container`;
  if (!fs.existsSync(containerSourceDir) || !fs.readdirSync(containerSourceDir).length) {
    throw new Error(
      `No containers found in /cordova/${containerSourceDir}. \n` +
      'Required by plugin "tabris-plugin-firebase" with enabled "tagmanager" feature.');
  }
  let containerFileName = path.basename(fs.readdirSync(containerSourceDir)[0]);
  if (!fs.existsSync(containerTargetDir)) {
    fs.mkdirSync(containerTargetDir);
  }
  let source = path.join(containerSourceDir, containerFileName);
  let target = path.join(containerTargetDir, containerFileName);
  fs.writeFileSync(target, fs.readFileSync(source, 'utf-8'));
  if (platform === 'ios') {
    addContainerResourceToPbxProject(ctx, containerFileName);
  }
}

function addContainerResourceToPbxProject(context) {
  let xcode = context.requireCordovaModule('xcode');
  let fileList = fs.readdirSync(IOS_PLATFORM);
  let xcodeProjectDirectory = fileList.find(file => file.match(/xcodeproj$/));
  let xcodeProjectFilePath = path.join(IOS_PLATFORM, xcodeProjectDirectory, 'project.pbxproj');
  let xcodeProject = xcode.project(xcodeProjectFilePath);
  xcodeProject.parseSync();
  // See https://github.com/apache/cordova-ios/blob/4.5.x/bin/templates/project/__TEMP__.xcodeproj/project.pbxproj#L95
  const CUSTOM_TEMPLATE_PBX_GROUP = '29B97314FDCFA39411CA2CEA';
  xcodeProject.addResourceFile(`container`, {}, CUSTOM_TEMPLATE_PBX_GROUP);
  fs.writeFileSync(xcodeProjectFilePath, xcodeProject.writeSync());
}

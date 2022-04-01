'use strict';

const fs = require('fs');
const path = require('path');

const ANDROID_PLATFORM = 'platforms/android';
const IOS_PLATFORM = 'platforms/ios';

module.exports = function(ctx) {
  let platform = ctx.opts.cordova.platforms[0];
  let containerSourceDir = platform === 'android' ? path.join('tagmanager-container', 'android') : path.join('tagmanager-container', 'ios');
  let containerTargetDir = platform === 'android' ? path.join(ANDROID_PLATFORM, 'app', 'src', 'main', 'assets', 'containers') : path.join(IOS_PLATFORM, 'container');
  if (!fs.existsSync(containerSourceDir) || !fs.readdirSync(containerSourceDir).length) {
    throw new Error(
      `No containers found in /cordova/${containerSourceDir}. \n` +
      `Required by plugin "${ctx.opts.plugin.id}" with enabled "tagmanager" feature.`);
  }
  let files = fs.readdirSync(containerSourceDir);
  let containerFileName = path.basename(files.filter(file => file.match(/json$/))[0]);
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

/**
 * Licensed under MIT.
 * Source: https://github.com/arnesson/cordova-plugin-firebase/blob/master/scripts/lib/android-helper.js
 */

var fs = require("fs");
var path = require("path");

const DEPENDENCIES = ['classpath \'com.google.firebase:firebase-plugins:1.1.5\''];
const PLUGINS = ['apply plugin: \'com.google.firebase.firebase-perf\''];

var buildGradle = readRootBuildGradle();
buildGradle = addDependencies(buildGradle);
buildGradle = addPlugins(buildGradle);
writeRootBuildGradle(buildGradle);

function readRootBuildGradle() {
  var target = path.join("platforms", "android", "build.gradle");
  return fs.readFileSync(target, "utf-8");
}

function addDependencies(buildGradle) {
  var buildToolsClasspath = /^(\s*)classpath 'com.android.tools.build(.*)/m;
  var match = buildGradle.match(buildToolsClasspath);
  var whitespace = match[1];
  var dependencies = DEPENDENCIES.map(dependency => whitespace + dependency + ' // source: tabris-plugin-firebase').join('\n');
  var modifiedLine = match[0] + '\n' + dependencies;
  return buildGradle.replace(buildToolsClasspath, modifiedLine);
}

function addPlugins(buildGradle) {
  var applicationPlugin = "apply plugin: 'com.android.application'";
  var modifiedLine = applicationPlugin + '\n' + PLUGINS.map(plugin => plugin + ' // source: tabris-plugin-firebase').join('\n');
  return buildGradle.replace(applicationPlugin, modifiedLine);
}

function writeRootBuildGradle(contents) {
  var target = path.join("platforms", "android", "build.gradle");
  fs.writeFileSync(target, contents);
}

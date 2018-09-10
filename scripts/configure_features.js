'use strict';

const fs = require('fs');
const path = require('path');

const FEATURES = ['tagmanager'];

module.exports = function(ctx) {
  let cordovaUtil = ctx.requireCordovaModule('cordova-lib/src/cordova/util');
  let ConfigParser = ctx.requireCordovaModule('cordova-common').ConfigParser;
  let configXmlPath = cordovaUtil.projectConfig(ctx.opts.projectRoot);
  let configParser = new ConfigParser(configXmlPath);
  let configuredFeaturesString = configParser.getPlugin('tabris-plugin-firebase').variables.FEATURES;
  let configuredFeatures = configuredFeaturesString ? configuredFeaturesString.split(' ') : [];
  let pluginXmlPath = path.join(ctx.opts.projectRoot, 'plugins', 'tabris-plugin-firebase', 'plugin.xml');
  let pluginXml = fs.readFileSync(pluginXmlPath, 'utf-8');
  FEATURES.forEach(feature => {
    if (configuredFeatures.includes(feature)) {
      console.log(`Enabling "tabris-plugin-firebase" feature "${feature}"`);
      return;
    }
    let regex = new RegExp(`<!-- BEGIN_FEATURE ${feature} -->(.|\\n)*?<!-- END_FEATURE ${feature} -->`, 'g');
    pluginXml = pluginXml.replace(regex, '');
  });
  fs.writeFileSync(pluginXmlPath, pluginXml);
};

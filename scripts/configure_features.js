'use strict';

const fs = require('fs');

const FEATURES = ['tagmanager'];

module.exports = function(ctx) {
  let cordovaUtil = ctx.requireCordovaModule('cordova-lib/src/cordova/util');
  let ConfigParser = ctx.requireCordovaModule('cordova-common').ConfigParser;
  let configXmlPath = cordovaUtil.projectConfig(ctx.opts.projectRoot);
  let configParser = new ConfigParser(configXmlPath);
  let configuredFeaturesString = configParser.getPlugin('tabris-plugin-firebase').variables.FEATURES;
  let configuredFeatures = configuredFeaturesString ? configuredFeaturesString.split(' ') : [];
  console.log('filepath: ' + ctx.opts.plugin.pluginInfo.filepath);
  console.log('dir: ' + ctx.opts.plugin.pluginInfo.dir);
  console.log('info: ' + ctx.opts.plugin.pluginInfo.getInfo());
  let pluginXmlPath = ctx.opts.plugin.pluginInfo.filepath;
  let pluginXml = fs.readFileSync(pluginXmlPath, 'utf-8');
  FEATURES.forEach(feature => {
    if (configuredFeatures.includes(feature)) {
      console.log(`Enabling "tabris-plugin-firebase" feature "${feature}"`);
      return;
    }
    let regex = new RegExp(`<!-- BEGIN_FEATURE ${feature} -->(.|\\n)*<!-- END_FEATURE ${feature} -->`, 'g');
    pluginXml = pluginXml.replace(regex, '');
  });
  fs.writeFileSync(pluginXmlPath, pluginXml);
};

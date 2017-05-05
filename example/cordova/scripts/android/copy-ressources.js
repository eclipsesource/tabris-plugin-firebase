var fs = require('fs');
var path = require('path');

module.exports = function(context) {
  if (fs.existsSync(path.join(context.opts.projectRoot, 'platforms/android'))) {
    var srcDir = path.join(context.opts.projectRoot, 'res/android');
    var targetDir = path.join(context.opts.projectRoot, 'platforms/android/res');
    copyRecursiveSync(srcDir, targetDir);
  }
};

var copyRecursiveSync = function(src, dest) {
  var exists = fs.existsSync(src);
  var stats = exists && fs.statSync(src);
  var isDirectory = exists && stats.isDirectory();
  if (exists && isDirectory) {
    if (!fs.existsSync(dest)) {
      fs.mkdirSync(dest);
    }
    fs.readdirSync(src).forEach(function(childItemName) {
      copyRecursiveSync(path.join(src, childItemName), path.join(dest, childItemName));
    });
  } else {
    fs.linkSync(src, dest);
  }
};

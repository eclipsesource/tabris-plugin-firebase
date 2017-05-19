const fs = require('fs');
const path = require('path');

module.exports = context => {
  if (fs.existsSync(path.join(context.opts.projectRoot, 'platforms/android'))) {
    const srcDir = path.join(context.opts.projectRoot, 'res/android');
    const targetDir = path.join(context.opts.projectRoot, 'platforms/android/res');
    copyRecursiveSync(srcDir, targetDir);
  }
};

function copyRecursiveSync(src, dest) {
  const exists = fs.existsSync(src);
  const stats = exists && fs.statSync(src);
  const isDirectory = exists && stats.isDirectory();
  if (exists && isDirectory) {
    if (!fs.existsSync(dest)) {
      fs.mkdirSync(dest);
    }
    fs.readdirSync(src)
      .forEach(childItemName => copyRecursiveSync(path.join(src, childItemName), path.join(dest, childItemName)));
  } else {
    fs.linkSync(src, dest);
  }
}

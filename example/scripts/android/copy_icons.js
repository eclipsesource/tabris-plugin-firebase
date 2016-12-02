#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const files = [
  'drawable-xhdpi/ic_stat_trend.png'
];

files.forEach(file => {
  const src = path.join('res/android', file);
  const dst = path.join('platforms/android/res', file);
  if (fs.existsSync(src) && fs.existsSync(path.dirname(dst))) {
    console.log(`copying ${src} to ${dst}`);
    fs.createReadStream(src).pipe(fs.createWriteStream(dst));
  }
});

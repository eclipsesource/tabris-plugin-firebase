#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const files = [
  'drawable-xhdpi/ic_stat_trend.png'
];

files.forEach(file => {
  const src = path.join('res/android', file);
  const dst = path.join('platforms/android/res', file);
  const dstDirname = path.dirname(dst);
  if (!fs.existsSync(src)) return;
  if (!fs.existsSync(dstDirname)) {
    fs.mkdirSync(dstDirname);
  }
  console.log(`copying ${src} to ${dst}`);
  fs.createReadStream(src).pipe(fs.createWriteStream(dst));
});

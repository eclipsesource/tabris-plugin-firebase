'use strict';

const fs = require('fs');

const ANDROID_PLATFORM = 'platforms/android';
const GOOGLE_SERVICES_JSON = 'google-services.json';

if (!fs.existsSync(GOOGLE_SERVICES_JSON)) {
  throw new Error(
    'No "google-services.json" file found in project root directory. ' +
    'Required by plugin "tabris-plugin-firebase" (Android).');
} else {
  if (fs.existsSync(ANDROID_PLATFORM) && fs.statSync(ANDROID_PLATFORM).isDirectory()) {
    fs.createReadStream(GOOGLE_SERVICES_JSON)
      .pipe(fs.createWriteStream(`${ANDROID_PLATFORM}/${GOOGLE_SERVICES_JSON}`));
  }
}

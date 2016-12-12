'use strict';

const fs = require('fs');
const GOOGLE_SERVICES_JSON = 'google-services.json';

if (!fs.existsSync(GOOGLE_SERVICES_JSON)) {
  throw new Error(
    'No "google-services.json" file found in project root directory. ' +
    'Required by plugin "tabris-plugin-firebase" (Android).');
} else {
  let ANDROID_PLATFORM_PATH = 'platforms/android';
  if (fs.existsSync(ANDROID_PLATFORM_PATH) && fs.statSync(ANDROID_PLATFORM_PATH).isDirectory()) {
    fs.createReadStream(GOOGLE_SERVICES_JSON)
      .pipe(fs.createWriteStream(`${ANDROID_PLATFORM_PATH}/${GOOGLE_SERVICES_JSON}`));
  }
}

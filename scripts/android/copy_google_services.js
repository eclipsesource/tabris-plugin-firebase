'use strict';

const fs = require('fs');

const APP_MODULE = 'platforms/android/app';
const GOOGLE_SERVICES_JSON = 'google-services.json';

if (!fs.existsSync(GOOGLE_SERVICES_JSON)) {
  throw new Error(
    'No "google-services.json" file found in /cordova.' +
    'Required by plugin "tabris-plugin-firebase" (Android).');
} else {
  if (fs.existsSync(APP_MODULE) && fs.statSync(APP_MODULE).isDirectory()) {
    fs.createReadStream(GOOGLE_SERVICES_JSON)
      .pipe(fs.createWriteStream(`${APP_MODULE}/${GOOGLE_SERVICES_JSON}`));
  }
}

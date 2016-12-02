'use strict';

const fs = require('fs');
const GOOGLE_SERVICES_JSON = 'google-services.json';

if (!fs.existsSync(GOOGLE_SERVICES_JSON)) {
  throw new Error(
    'No "google-services.json" file found in project root directory. ' +
    'Required by plugin "tabris-plugin-firebase" (Android).');
} else {
  if (fs.statSync('platforms/android').isDirectory()) {
    fs.createReadStream(GOOGLE_SERVICES_JSON).pipe(fs.createWriteStream(`platforms/android/${GOOGLE_SERVICES_JSON}`));
  }
}

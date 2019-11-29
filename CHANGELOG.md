Change Log
==========
## Version 5.0.0

(thx to [fax1ty](https://github.com/fax1ty))

* Firebase Performance implementation (Android only)
* Firebase Crashlytics implementation (Android only)
* Android project dependencies versions have been increased

## Version 4.0.0

* Compatible with Tabris.js version 3.0.0+

## Version 3.0.0

* Compatible with Tabris.js version 3.0 and 3.1

## Version 2.0.0

* Make plugin compatible with Tabris.js 2.2.0+.
* Fix a crash on iOS when GoogleService-Info.plist is missing.
* Fix permissions declaration in AndroidManifest.xml.
* Update the Firebase Analytics framework.
* Fix an issue on Android, which may cause messages to be delivered twice.
* The iOS plugin now inserts the required `aps-environment` entitlement.
* Fix an issue on iOS causing no notification banners to be shown.
* Fix an issue on iOS causing no notifications to be received when the app is not running.

## Version 1.0.1

* Use ES5 in the JavaScript part of the plugin to make it compatible with iOS 9.
* Fix running the example on Android.
* Update error message shown for Android builds when google-services.json is missing.

## Version 1.0.0

Initial version of the plugin supporting Firebase Cloud Messaging on Android and Firebase Analytics on Android and iOS.

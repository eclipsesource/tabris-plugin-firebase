# Firebase Crashlytics

## Enable analytics tracking
By default the plugin does _NOT_ track any usage data. You have to enable crash reports sending by setting `crashlyticsCollectionEnabled` to `true`:

```js
firebase.Crashlytics.setCrashlyticsCollectionEnabled(true);
```

**Note:** changes will only be applied after restarting the application.

## API

The firebase crashlytics API is represented as the global object `firebase.Crashlytics`.

### `Crashlytics`

#### Methods

##### `setCrashlyticsCollectionEnabled(enabled)`

* Enables or disables the collection of data about crashes. 

**Note:** changes will only be applied after restarting the application.

##### `setUserIdentifier(id)`

* Specify a user identifier which will be visible in the Crashlytics UI.

##### `makeCrash()`

* The easiest way to cause a crash - great for testing!

##### `log(value)`

* Add text logging that will be sent with your next report. This logging will only be visible in your Crashlytics dashboard, and will be associated with the next crash or logged exception for this app execution. Newline characters ('\n') will be stripped from msg. The log is rolling with a maximum size of 64k, such that messages are removed (oldest first) if the max size is exceeded.

##### `log(priority, tag, value)` 

* Add text logging that will be sent with your next report, using log(value). The message will also be printed to LogCat.

##### `setBool(key, value)` 

* Sets a value to be associated with a given key for your crash data.

##### `setString(key, value)` 

* Sets a value to be associated with a given key for your crash data.

##### `setInt(key, value)` 

* Sets a value to be associated with a given key for your crash data.
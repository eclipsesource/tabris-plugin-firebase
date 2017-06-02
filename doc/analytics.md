# Firebase Analytics

The `tabris-plugin-firebase` plugin project provides a [Tabris.js](https://tabrisjs.com) API to track app usage.


![Firebase Analytics](img/analytics.png)

## Example


## API

The firebase analytics API is represented as the global object `firebase.Analytics`.

### `Analytics`

#### Properties

All `Analytics` properties are write only.

##### `analyticsCollectionEnabled` : _boolean_

##### `screenName` : _string_

##### `userId` : _string_

#### Methods

##### `logEvent(eventName, data)`

##### `setUserProperty(key, value)`

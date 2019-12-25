# Firebase Remote Config

## API

The firebase remote config API is represented as the global object `firebase.RemoteConfig`.

### `RemoteConfig`

#### Properties

##### `minimumFetchIntervalInSeconds` : _number_

* Specified minimum config fetch interval.

#### Methods

##### `setDefaults(values)`

* Asynchronously sets default configs using the given key-value JSON Object.

The values in defaults must be one of the following types:

* byte[]
* Boolean
* Double
* Long
* String

##### `getBoolean(key)`

* Returns the parameter value for the given key as a boolean. 

##### `getDouble(key)`

* Returns the parameter value for the given key as a double (number).

##### `getLong(key)`

* Returns the parameter value for the given key as a long. (number).

##### `getString(key)`

* Returns the parameter value for the given key as a String.
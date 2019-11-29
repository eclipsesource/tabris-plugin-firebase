# Firebase Crashlytics

## Enable analytics tracking
By default the plugin does _NOT_ track any usage data. You have to enable performance monitoring by setting `performanceCollectionEnabled` to `true`:

```js
firebase.Performance.performanceCollectionEnabled = true;
```

## API

The firebase performance API is represented as the global object `firebase.Performance`.

### `Performance`

#### Properties

All `Performance` properties are _write only_.

##### `performanceCollectionEnabled` : _boolean_

* Enables performance data collection for this app. To make use of firebase performance data collection _has to be enabled_ by the developer. The enablement persists across sessions.

#### Methods

##### `startTrace(name)`

* Creates a Trace object with given name and start the trace.

##### `stopTrace(name)`

* Stops a Trace with given name.

##### `incrementMetrics(name, counter, value)`

* Atomically increments the metric with the given name in this trace by the incrementBy value.

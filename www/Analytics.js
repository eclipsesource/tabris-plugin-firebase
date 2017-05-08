const writeOnly = {
  get: name => console.warn('Can not get write-only property "' + name + '"')
};

class Analytics extends tabris.NativeObject {

  constructor() {
    super();
    this._create('com.eclipsesource.firebase.Analytics');
  }

  _dispose() {
    throw new Error('Analytics can not be disposed');
  }

  logEvent(name, data) {
    if (typeof name !== 'string') {
      throw new Error('Invoking "logEvent" requires a string parameter "name" but received ' + name);
    }
    this._nativeCall('logEvent', {name, data: data ? data : {}});
    return this;
  }

  setUserProperty(key, value) {
    if (typeof key !== 'string') {
      throw new Error('Invoking "setUserProperty" requires a string parameter "key" but received ' + key);
    }
    this._nativeCall('setUserProperty', {key, value});
    return this;
  }
}

tabris.NativeObject.defineProperties(Analytics.prototype, {
  analyticsCollectionEnabled: {type: 'boolean', nocache: true, access: writeOnly},
  screenName: {type: 'string', nocache: true, access: writeOnly},
  userId: {type: 'string', nocache: true, access: writeOnly}
});

module.exports = new Analytics();


var writeOnly = {
  get: function(name) {
    console.warn('Can not get write-only property "' + name + '"');
  }
};

var Analytics = tabris.NativeObject.extend('com.eclipsesource.firebase.Analytics');

Object.assign(Analytics.prototype, {

  _dispose() {
    throw new Error('Analytics can not be disposed');
  },

  logEvent(name, data) {
    if (typeof name !== 'string') {
      throw new Error('Invoking "logEvent" requires a string parameter "name" but received ' + name);
    }
    this._nativeCall('logEvent', {name, data: data ? data : {}});
    return this;
  },

  setUserProperty(key, value) {
    if (typeof key !== 'string') {
      throw new Error('Invoking "setUserProperty" requires a string parameter "key" but received ' + name);
    }
    this._nativeCall('setUserProperty', {key, value});
    return this;
  }

});

tabris.NativeObject.defineProperties(Analytics.prototype, {
  analyticsCollectionEnabled: {type: 'boolean', nocache: true, access: writeOnly},
  screenName: {type: 'string', nocache: true, access: writeOnly},
  userId: {type: 'string', nocache: true, access: writeOnly},
});

module.exports = new Analytics();

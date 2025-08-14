const EVENT_TYPES = ['tokenChanged', 'instanceIdChanged', 'message'];

const Messaging = tabris.NativeObject.extend('com.eclipsesource.firebase.Messaging');

Messaging.prototype._listen = function(name, listening) {
  if (EVENT_TYPES.indexOf(name) > -1) {
    this._nativeListen(name, listening);
  } else {
    tabris.Widget.prototype._listen.call(this, name, listening);
  }
};

Messaging.prototype._dispose = function() {
  throw new Error('Messaging can not be disposed');
};

Messaging.prototype.resetInstanceId = function() {
  this._nativeCall('resetInstanceId');
  return this;
};

Messaging.prototype.requestPermissions = function() {
  if (tabris.device.platform === 'iOS') {
    this._nativeCall('requestPermissions');
  } else {
    console.warn('requestPermissions() is only supported on iOS.');
  }
  return this;
};

Object.defineProperty(Messaging.prototype, 'pendingMessages', {
  get: function() {
    return {
      getAll: () => this._nativeCall('getAllPendingMessages') || [],
      clearAll: () => this._nativeCall('clearAllPendingMessages')
    };
  }
});

tabris.NativeObject.defineProperties(Messaging.prototype, {
  instanceId: {type: 'string', nocache: true, readonly: true},
  token: {type: 'string', nocache: true, readonly: true},
  apnsToken: {type: 'string', nocache: true, readonly: true},
  launchData: {type: 'string', nocache: true, readonly: true}
});

module.exports = new Messaging();

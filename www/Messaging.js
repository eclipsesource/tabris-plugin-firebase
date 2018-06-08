var EVENT_TYPES = ['tokenChanged', 'instanceIdChanged', 'message'];

var readOnly = {
  set: function(name) {
    console.warn(`Can not set read-only property "${name}"`)
  }
};

var Messaging = tabris.NativeObject.extend('com.eclipsesource.firebase.Messaging');

Messaging.prototype._listen = function(name, listening) {
  if (EVENT_TYPES.indexOf(name) > -1) {
    this._nativeListen(name, listening);
  } else {
    tabris.Widget.prototype._listen.call(this, name, listening);
  }
};

Messaging.prototype._dispose = function() {
  throw new Error('Messaging can not be disposed');
}

Messaging.prototype.resetInstanceId = function() {
  this._nativeCall('resetInstanceId');
  return this;
}

Messaging.prototype.requestPermissions = function() {
  if (tabris.device.platform === 'iOS') {
    this._nativeCall('requestPermissions');
  } else {
    console.warn('requestPermissions() is only supported on iOS.');
  }
  return this;
}

tabris.NativeObject.defineProperties(Messaging.prototype, {
  instanceId: {type: 'string', nocache: true, access: readOnly},
  token: {type: 'string', nocache: true, access: readOnly},
  launchData: {type: 'string', nocache: true, access: readOnly}
});

module.exports = new Messaging();

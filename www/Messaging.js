var EVENT_TYPES = ['change:token', 'change:instanceId', 'message'];

var readOnly = {
  set: function(name) {
    console.warn('Can not set read-only property "' + name + '"');
  }
};

var Messaging = tabris.NativeObject.extend('com.eclipsesource.firebase.Messaging');

Messaging.prototype._trigger = function(name, event) {
  if (name === 'change:token') {
    tabris.NativeObject.prototype._triggerChangeEvent.call(this, 'token', event.token);
  } else if (name === 'change:instanceId') {
    tabris.NativeObject.prototype._triggerChangeEvent.call(this, 'instanceId', event.instanceId);
  } else {
    tabris.NativeObject.prototype._trigger.call(this, name, event);
  }
};

Messaging.prototype._listen = function(name, listening) {
  if (EVENT_TYPES.indexOf(name) > -1) {
    this._nativeListen(name, listening);
  } else {
    tabris.NativeObject.prototype._listen.call(this, name, listening);
  }
};

Object.assign(Messaging.prototype, {

  _dispose() {
    throw new Error('Messaging can not be disposed');
  },

  resetInstanceId() {
    this._nativeCall('resetInstanceId');
    return this;
  }

});

tabris.NativeObject.defineProperties(Messaging.prototype, {
  instanceId: {type: 'string', nocache: true, access: readOnly},
  token: {type: 'string', nocache: true, access: readOnly},
  launchData: {type: 'string', nocache: true, access: readOnly}
});

module.exports = new Messaging();

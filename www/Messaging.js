var readOnly = {
  set: function(name) {
    console.warn('Can not set read-only property "' + name + '"');
  }
};

var Messaging = tabris.NativeObject.extend({

  _type: 'com.eclipsesource.firebase.Messaging',

  _properties: {
    instanceId: {type: 'string', nocache: true, access: readOnly},
    token: {type: 'string', nocache: true, access: readOnly},
    launchData: {type: 'string', nocache: true, access: readOnly}
  },

  _events: {
    tokenRefresh: {
      trigger: function(event) {
        this.trigger('tokenRefresh', this, event.token);
      }
    },
    message: {
      trigger: function(event) {
        this.trigger('message', this, event.data);
      }
    },
    instanceidchange: {
      trigger: function(event) {
        this.trigger('instanceidchange', this, event.instanceId);
      }
    }
  },

  _dispose() {
    throw new Error('Messaging can not be disposed');
  },

  resetInstanceId() {
    this._nativeCall('resetInstanceId');
    return this;
  }
});

module.exports = new Messaging();

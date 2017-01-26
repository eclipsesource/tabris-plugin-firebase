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
    'change:token': {
      trigger: function(event) {
        this.trigger('change:token', this, event.token);
      }
    },
    'change:instanceId': {
      trigger: function(event) {
        this.trigger('change:instanceId', this, event.instanceId);
      }
    },
    message: {
      trigger: function(event) {
        this.trigger('message', this, event.data);
      }
    }
  }

});

Object.assign(Messaging.prototype, {

  _dispose() {
    throw new Error('Messaging can not be disposed');
  },

  resetInstanceId() {
    this._nativeCall('resetInstanceId');
    return this;
  }

});

module.exports = new Messaging();

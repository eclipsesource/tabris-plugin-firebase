const EVENT_TYPES = ['tokenChanged', 'instanceIdChanged', 'message'];

const readOnly = {
  set: name => console.warn(`Can not set read-only property "${name}"`)
};

class Messaging extends tabris.NativeObject {

  constructor() {
    super();
    this._create('com.eclipsesource.firebase.Messaging');
  }

  _listen(name, listening) {
    if (EVENT_TYPES.indexOf(name) > -1) {
      this._nativeListen(name, listening);
    } else {
      super._listen(name, listening);
    }
  }

  _dispose() {
    throw new Error('Messaging can not be disposed');
  }

  resetInstanceId() {
    this._nativeCall('resetInstanceId');
    return this;
  }

}

tabris.NativeObject.defineProperties(Messaging.prototype, {
  instanceId: {type: 'string', nocache: true, access: readOnly},
  token: {type: 'string', nocache: true, access: readOnly},
  launchData: {type: 'string', nocache: true, access: readOnly}
});

module.exports = new Messaging();

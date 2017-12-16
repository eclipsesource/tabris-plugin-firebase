
const EVENT_TYPES = ['onValueSaved', 'ErrorSaving', 'onDataChange',      
    'onCancelled', 'onChildAdded', 'onChildChanged', 'onChildMoved', 'onChildRemoved'];

const readOnly = {
  set: name => console.warn(`Can not set read-only property "${name}"`)
};

class Reference extends tabris.NativeObject {
    
  constructor(path) {
    super();
    console.error("Path: " + path);
    this._create('com.eclipsesource.firebase.Reference', {path: path});
    this._nativeCall('create', {path: path});
    this.path = path;
  }

  _listen(name, listening) {
    if (EVENT_TYPES.indexOf(name) > -1) {
      this._nativeListen(name, listening);
    } else {
      super._listen(name, listening);
    }
  }

  _dispose() {
    throw new Error('DatabaseReference can not be disposed...?');
  }



  child(path){
      return new Reference(this.path + "/" + path);
  }

  setValue(value) {
    this._nativeCall('setValue', {value: value ? value : {}});
    return this;
  }

  push(){
    return new Reference(this.path + "/" + this._nativeCall('push', {}));
  }

  keepSynced(keep){
    this._nativeCall('keepSynced', {keepSynced: keep});
    return this;
  }

}
tabris.NativeObject.defineProperties(Reference.prototype, {
  key: {type: 'string', nocache: true, access: readOnly},
});

module.exports = Reference;
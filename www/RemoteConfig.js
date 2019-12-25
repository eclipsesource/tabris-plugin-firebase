var RemoteConfig = tabris.NativeObject.extend('com.eclipsesource.firebase.RemoteConfig');

RemoteConfig.prototype._dispose = function () {
    throw new Error('RemoteConfig can not be disposed');
};

RemoteConfig.prototype.setDefaults = function (values) {
    if (typeof values == 'undefined')
        throw new Error('Invoking "setDefaults" requires a not null parameter "values" but received ' + values);
    this._nativeCall('setDefaults', { values: values });
    return this;
}

RemoteConfig.prototype.getBoolean = function (key) {
    if (typeof key == 'string')
        throw new Error('Invoking "getBoolean" requires a string parameter "key" but received ' + key);
    this._nativeCall('getBoolean', { key: key });
    return this;
}

RemoteConfig.prototype.getDouble = function (key) {
    if (typeof key == 'string')
        throw new Error('Invoking "getDouble" requires a string parameter "key" but received ' + key);
    this._nativeCall('getDouble', { key: key });
    return this;
}

RemoteConfig.prototype.getLong = function (key) {
    if (typeof key == 'string')
        throw new Error('Invoking "getLong" requires a string parameter "key" but received ' + key);
    this._nativeCall('getLong', { key: key });
    return this;
}

RemoteConfig.prototype.getString = function (key) {
    if (typeof key == 'string')
        throw new Error('Invoking "getString" requires a string parameter "key" but received ' + key);
    this._nativeCall('getString', { key: key });
    return this;
}

tabris.NativeObject.defineProperties(RemoteConfig.prototype, {
    minimumFetchIntervalInSeconds: { type: 'number', default: 3600 }
});

module.exports = new RemoteConfig();

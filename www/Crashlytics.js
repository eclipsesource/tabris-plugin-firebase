var Crashlytics = tabris.NativeObject.extend('com.eclipsesource.firebase.Crashlytics');

Crashlytics.prototype._dispose = function () {
    throw new Error('Crashlytics can not be disposed');
};

Crashlytics.prototype.setCrashlyticsCollectionEnabled = function (enabled) {
    if (typeof enabled !== 'boolean')
        throw new Error('Invoking "setCrashlyticsCollectionEnabled" requires a boolean parameter "enabled" but received ' + enabled);
    this._nativeCall('setCrashlyticsCollectionEnabled', { enabled: enabled });
    return this;
}

Crashlytics.prototype.setUserIdentifier = function (id) {
    if (typeof id !== 'string')
        throw new Error('Invoking "setUserIdentifier" requires a string parameter "id" but received ' + id);
    this._nativeCall('setUserIdentifier', { id: id });
    return this;
}

Crashlytics.prototype.makeCrash = function () {
    this._nativeCall('makeCrash', { id: id });
    return this;
}

Crashlytics.prototype.log = function (st, nd, rd) {
    if (typeof st == 'number') {
        if (typeof nd !== 'string')
            throw new Error('Invoking "log" requires a string parameter "tag" but received ' + nd);
        if (typeof rd !== 'string')
            throw new Error('Invoking "log" requires a string parameter "value" but received ' + rd);
        this._nativeCall('log', { priority: st, tag: nd, value: rd });
    }
    else {
        if (typeof st !== 'string')
            throw new Error('Invoking "log" requires a string parameter "value" but received ' + st);
        this._nativeCall('log', { value: st });
    }
    return this;
}

// Customizing
Crashlytics.prototype.setBool = function (key, value) {
    if (typeof key !== 'string')
        throw new Error('Invoking "setBool" requires a string parameter "key" but received ' + key);
    if (typeof value !== 'boolean')
        throw new Error('Invoking "setBool" requires a boolean parameter "value" but received ' + value);
    this._nativeCall('setBool', { key: key, value: value });
    return this;
}

Crashlytics.prototype.setString = function (key, value) {
    if (typeof key !== 'string')
        throw new Error('Invoking "setString" requires a string parameter "key" but received ' + key);
    if (typeof value !== 'string')
        throw new Error('Invoking "setString" requires a string parameter "value" but received ' + value);
    this._nativeCall('setString', { key: key, value: value });
    return this;
}

Crashlytics.prototype.setInt = function (key, value) {
    if (typeof key !== 'string')
        throw new Error('Invoking "setInt" requires a string parameter "key" but received ' + key);
    if (typeof value !== 'number')
        throw new Error('Invoking "setInt" requires a number parameter "value" but received ' + value);
    this._nativeCall('setInt', { key: key, value: value });
    return this;
}

module.exports = new Crashlytics();

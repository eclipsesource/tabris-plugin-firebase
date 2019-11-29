var Performance = tabris.NativeObject.extend('com.eclipsesource.firebase.Performance');

Performance.prototype._dispose = function () {
    throw new Error('Performance can not be disposed');
};

Performance.prototype.startTrace = function (name) {
    if (typeof name !== 'string')
        throw new Error('Invoking "startTrace" requires a string parameter "name" but received ' + name);
    this._nativeCall('startTrace', { name: name });
    return this;
}

Performance.prototype.stopTrace = function (name) {
    if (typeof name !== 'string')
        throw new Error('Invoking "stopTrace" requires a string parameter "name" but received ' + name);
    this._nativeCall('stopTrace', { name: name });
    return this;
}

Performance.prototype.incrementMetrics = function (name, counter, value) {
    if (typeof name !== 'string')
        throw new Error('Invoking "incrementMetrics" requires a string parameter "name" but received ' + name);
    if (typeof counter !== 'string')
        throw new Error('Invoking "incrementMetrics" requires a string parameter "counter" but received ' + counter);
    if (typeof value !== 'number')
        throw new Error('Invoking "incrementMetrics" requires a number parameter "value" but received ' + value);
    this._nativeCall('incrementMetrics', { name: name, counter: counter, value: value });
    return this;
}

tabris.NativeObject.defineProperties(Performance.prototype, {
    performanceCollectionEnabled: { type: 'boolean', default: null }
});

module.exports = new Performance();

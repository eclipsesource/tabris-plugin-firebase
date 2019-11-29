package com.eclipsesource.firebase.performance

import android.app.Activity
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace

class Performance(private val activity: Activity) {

    private val firebasePerformance = FirebasePerformance.getInstance()

    fun setPerformanceCollectionEnabled(enabled: Boolean) {
        firebasePerformance.isPerformanceCollectionEnabled = enabled
    }

    private val traces = hashMapOf<String, Trace>()

    fun startTrace(name: String) {
        if (traces.containsKey(name)) {
            traces[name]?.start()
        } else {
            val trace = firebasePerformance.newTrace(name)
            traces[name] = trace
            trace.start()
        }
    }

    fun stopTrace(name: String) = traces[name]?.stop()

    fun incrementMetrics(name: String, counter: String, value: Long) = traces[name]?.incrementCounter(counter, value)

}
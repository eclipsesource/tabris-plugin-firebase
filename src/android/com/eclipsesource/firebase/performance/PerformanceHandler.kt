package com.eclipsesource.firebase.performance

import com.eclipsesource.tabris.android.ActivityScope
import com.eclipsesource.tabris.android.BooleanProperty
import com.eclipsesource.tabris.android.ObjectHandler
import com.eclipsesource.tabris.android.Property
import com.eclipsesource.tabris.android.internal.ktx.getLong
import com.eclipsesource.v8.V8Object

class PerformanceHandler(private val scope: ActivityScope) : ObjectHandler<Performance> {
    override val type = "com.eclipsesource.firebase.Performance"

    override val properties = listOf<Property<Performance, *>>(
            BooleanProperty("performanceCollectionEnabled", {
                setPerformanceCollectionEnabled(it ?: false)
            })
    )

    override fun create(id: String, properties: V8Object) = Performance(scope.activity)

    override fun call(performance: Performance, method: String, properties: V8Object) = when (method) {
        "startTrace" -> performance.startTrace(properties.getString("name"))
        "stopTrace" -> performance.stopTrace(properties.getString("name"))
        "incrementMetrics" ->  performance.incrementMetrics(properties.getString("name"), properties.getString("counter"), properties.getLong("value"))
        else -> null
    }

}
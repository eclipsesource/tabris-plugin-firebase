package com.eclipsesource.firebase.remote_config

import com.eclipsesource.tabris.android.ActivityScope
import com.eclipsesource.tabris.android.LongProperty
import com.eclipsesource.tabris.android.ObjectHandler
import com.eclipsesource.tabris.android.Property
import com.eclipsesource.tabris.android.internal.ktx.toMap
import com.eclipsesource.v8.V8Object

class RemoteConfigHandler(private val scope: ActivityScope) : ObjectHandler<RemoteConfig> {
    override val type = "com.eclipsesource.firebase.RemoteConfig"

    override val properties = listOf<Property<RemoteConfig, *>>(
            LongProperty("minimumFetchIntervalInSeconds", {
                setMinimumFetchIntervalInSeconds(it ?: 3600)
            }, { return@LongProperty getMinimumFetchIntervalInSeconds() })
    )

    override fun create(id: String, properties: V8Object) = RemoteConfig(scope.activity).also { it.setMinimumFetchIntervalInSeconds(3600) }

    override fun call(performance: RemoteConfig, method: String, properties: V8Object) = when (method) {
        "setDefaults"-> performance.setDefaults(properties.getObject("values").toMap())
        "getBoolean" -> performance.getBoolean(properties.getString("key"))
        "getDouble" -> performance.getDouble(properties.getString("key"))
        "getLong" -> performance.getLong(properties.getString("key"))
        "getString" -> performance.getString(properties.getString("key"))
        else -> null
    }

}
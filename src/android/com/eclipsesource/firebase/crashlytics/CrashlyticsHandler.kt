package com.eclipsesource.firebase.crashlytics

import com.eclipsesource.tabris.android.ActivityScope
import com.eclipsesource.tabris.android.ObjectHandler
import com.eclipsesource.v8.V8Object

class CrashlyticsHandler(private val scope: ActivityScope) : ObjectHandler<Crashlytics> {
    override val type = "com.eclipsesource.firebase.Crashlytics"

    override fun create(id: String, properties: V8Object) = Crashlytics(scope.activity).also { it.init() }

    override fun call(crashlytics: Crashlytics, method: String, properties: V8Object) = when (method) {
        "setCrashlyticsCollectionEnabled" -> crashlytics.setCrashlyticsCollectionEnabled(properties.getBoolean("enabled"))
        "setUserIdentifier" -> crashlytics.setUserIdentifier(properties.getString("id"))
        "makeCrash" -> crashlytics.makeCrash()
        "log" -> {
            if (properties.contains("priority")) crashlytics.log(properties.getInteger("priority"), properties.getString("tag"), properties.getString("value"))
            else crashlytics.log(properties.getString("value"))
        }
        // Customizing
        "setBool" -> crashlytics.setBool(properties.getString("key"), properties.getBoolean("value"))
        "setString" -> crashlytics.setString(properties.getString("key"), properties.getString("value"))
        "setInt" -> crashlytics.setInt(properties.getString("key"), properties.getInteger("value"))
        else -> null
    }

}
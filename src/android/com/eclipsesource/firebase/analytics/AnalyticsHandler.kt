package com.eclipsesource.firebase.analytics

import com.eclipsesource.tabris.android.*
import com.eclipsesource.tabris.android.internal.ktx.getObjectOrNull
import com.eclipsesource.tabris.android.internal.ktx.getStringOrNull
import com.eclipsesource.v8.V8Object

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class AnalyticsHandler(private val scope: ActivityScope) : ObjectHandler<Analytics> {

    override val type = "com.eclipsesource.firebase.Analytics"

    override val properties = listOf<Property<Analytics, *>>(
            BooleanProperty("analyticsCollectionEnabled", {
                setAnalyticsCollectionEnabled(it ?: false)
            }),
            StringProperty("screenName", { setScreenName(it) }),
            StringProperty("userId", { setUserId(it.orEmpty()) })
    )

    override fun create(id: String, properties: V8Object) = Analytics(scope.activity)

    override fun call(analytics: Analytics, method: String, properties: V8Object) = when (method) {
        "logEvent" -> analytics.logEvent(properties.getString("name"), properties.getObjectOrNull("data"))
        "setUserProperty" -> analytics.setUserProperty(properties.getString("key"), properties.getStringOrNull("value"))
        else -> null
    }

}

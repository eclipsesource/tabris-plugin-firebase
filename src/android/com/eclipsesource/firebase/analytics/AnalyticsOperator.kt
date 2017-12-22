package com.eclipsesource.firebase.analytics

import android.app.Activity
import com.eclipsesource.tabris.android.AbstractOperator
import com.eclipsesource.tabris.android.Properties
import com.eclipsesource.tabris.android.TabrisContext

private const val TYPE = "com.eclipsesource.firebase.Analytics"
private const val METHOD_LOG_EVENT = "logEvent"
private const val METHOD_SET_USER_PROPERTY = "setUserProperty"
private const val PROP_NAME = "name"
private const val PROP_DATA = "data"
private const val PROP_VALUE = "value"
private const val PROP_KEY = "key"

class AnalyticsOperator(private val activity: Activity, tabrisContext: TabrisContext)
  : AbstractOperator<Analytics>() {

  private val propertyHandler by lazy { AnalyticsPropertyHandler() }

  override fun getType() = TYPE

  override fun getPropertyHandler(analytics: Analytics) = propertyHandler

  override fun create(id: String, properties: Properties) = Analytics(activity)

  override fun call(analytics: Analytics, method: String, properties: Properties) = when (method) {
    METHOD_LOG_EVENT -> analytics
        .logEvent(properties.getString(PROP_NAME), properties.getProperties(PROP_DATA).all)
    METHOD_SET_USER_PROPERTY -> analytics
        .setUserProperty(properties.getString(PROP_KEY), properties.getString(PROP_VALUE))
    else -> null
  }

}

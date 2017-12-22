package com.eclipsesource.firebase.analytics

import com.eclipsesource.tabris.android.Properties
import com.eclipsesource.tabris.android.PropertyHandlerAdapter

private const val PROP_ANALYTICS_COLLECTION_ENABLED = "analyticsCollectionEnabled"
private const val PROP_SCREEN_NAME = "screenName"
private const val PROP_USER_ID = "userId"

class AnalyticsPropertyHandler : PropertyHandlerAdapter<Analytics>() {

  override fun set(analytics: Analytics, properties: Properties) {
    properties.all.forEach { (key, value) ->
      when (key) {
        PROP_ANALYTICS_COLLECTION_ENABLED -> analytics.setAnalyticsCollectionEnabled(value as Boolean)
        PROP_SCREEN_NAME -> analytics.setScreenName(value as String)
        PROP_USER_ID -> analytics.setUserId(value as String)
      }
    }
  }

}

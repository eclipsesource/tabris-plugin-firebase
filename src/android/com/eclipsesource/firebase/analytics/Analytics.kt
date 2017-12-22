package com.eclipsesource.firebase.analytics

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(private val activity: Activity) {

  private val firebaseAnalytics = FirebaseAnalytics.getInstance(activity.applicationContext)

  fun setAnalyticsCollectionEnabled(enabled: Boolean) {
    firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
  }

  fun setScreenName(name: String) {
    firebaseAnalytics.setCurrentScreen(activity, name, null)
  }

  fun setUserProperty(key: String, value: String) {
    firebaseAnalytics.setUserProperty(key, value)
  }

  fun setUserId(userId: String) {
    firebaseAnalytics.setUserId(userId)
  }

  fun logEvent(name: String, data: Map<String, Any>) {
    firebaseAnalytics.logEvent(name, createLogDataBundle(data))
  }

  private fun createLogDataBundle(data: Map<String, Any>?): Bundle {
    with(Bundle()) {
      data?.forEach { (key, value) ->
        when (value) {
          is String -> putString(key, value)
          is Long -> putLong(key, value)
          is Int -> putLong(key, value.toLong())
          is Double -> putDouble(key, value)
          is Float -> putDouble(key, value.toDouble())
        }
      }
      return this;
    }
  }

}

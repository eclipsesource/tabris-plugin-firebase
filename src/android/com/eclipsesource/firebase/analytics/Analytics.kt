package com.eclipsesource.firebase.analytics

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import com.eclipsesource.tabris.android.Properties
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.ArrayList

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
          is Properties -> putParcelable(key, createLogDataBundle(value.all))
          is List<*> -> {
            if (value.all { it is String })
              putStringArrayList(key, ArrayList(value.map { it as String } ))
            else
              putParcelableArrayList(key, ArrayList(value.map { mapListValue(it) }))
          }
        }
      }
      return this;
    }
  }

  private fun mapListValue(value: Any?): Parcelable {
    return when (value) {
      is Properties -> createLogDataBundle(value.all)
      is List<*> -> mapListValue(value)
      else -> throw IllegalArgumentException("Arrays may only contain objects or other arrays.")
    }
  }

}

package com.eclipsesource.firebase.analytics

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import com.eclipsesource.tabris.android.internal.ktx.asSequence
import com.eclipsesource.tabris.android.internal.ktx.forEach
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

class Analytics(private val activity: Activity) {

  private val firebaseAnalytics = FirebaseAnalytics.getInstance(activity.applicationContext)

  fun setAnalyticsCollectionEnabled(enabled: Boolean) = firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)

  fun setUserProperty(key: String, value: String?) = firebaseAnalytics.setUserProperty(key, value)

  fun setUserId(userId: String?) = firebaseAnalytics.setUserId(userId)

  fun logEvent(name: String, data: V8Object?) = firebaseAnalytics.logEvent(name, createLogDataBundle(data))

  private fun createLogDataBundle(data: V8Object?): Bundle {
    with(Bundle()) {
      data?.forEach<Any> { key, value ->
        when (value) {
          is String -> putString(key, value)
          is Long -> putLong(key, value)
          is Int -> putLong(key, value.toLong())
          is Double -> putDouble(key, value)
          is Float -> putDouble(key, value.toDouble())
          is V8Object -> putParcelable(key, createLogDataBundle(value))
          is V8Array -> {
            if (value.asSequence<Any>().all { it is String })
              putStringArrayList(key, ArrayList(value.asSequence<String>().toList()))
            else
              putParcelableArrayList(key, ArrayList(value.asSequence<Any>().map { mapListValue(it) }.toList()))
          }
        }
      }
      return this;
    }
  }

  private fun mapListValue(value: Any?): Parcelable {
    return when (value) {
      is V8Object -> createLogDataBundle(value)
      is V8Array -> mapListValue(value)
      else -> throw IllegalArgumentException("V8Arrays may only contain V8Objects or V8Arrays.")
    }
  }

}

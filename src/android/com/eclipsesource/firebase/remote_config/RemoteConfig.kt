package com.eclipsesource.firebase.remote_config

import android.app.Activity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfig(private val activity: Activity) {

    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private var minimumFetchIntervalInSeconds: Long = 0

    fun setMinimumFetchIntervalInSeconds(interval: Long) {
        minimumFetchIntervalInSeconds = interval
        firebaseRemoteConfig.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(interval)
                        .build()
        )
    }

    fun getMinimumFetchIntervalInSeconds(): Long {
        return minimumFetchIntervalInSeconds
    }

    fun setDefaults(values: Map<String, Any>) = firebaseRemoteConfig.setDefaultsAsync(values)!!

    fun getBoolean(key: String) = firebaseRemoteConfig.getBoolean(key)

    fun getDouble(key: String) = firebaseRemoteConfig.getDouble(key)

    fun getLong(key: String) = firebaseRemoteConfig.getLong(key)

    fun getString(key: String) = firebaseRemoteConfig.getString(key)

}
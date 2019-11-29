package com.eclipsesource.firebase.crashlytics

import android.app.Activity
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

class Crashlytics(private val activity: Activity) {

    private var firebaseCrashlytics: CrashlyticsCore? = null

    fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        activity.getPreferences(Context.MODE_PRIVATE).edit().putBoolean("crashlyticsCollectionEnabled", enabled).apply()
    }

    fun init() {
        val enabled = activity.getPreferences(Context.MODE_PRIVATE).getBoolean("crashlyticsCollectionEnabled", false)
        val core = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(!enabled).build())
                .build()
        Fabric.with(activity.applicationContext, core)
        firebaseCrashlytics = core.core
    }

    fun setUserIdentifier(id: String) = firebaseCrashlytics?.setUserIdentifier(id)

    fun makeCrash() = firebaseCrashlytics?.crash()

    fun log(value: String) = firebaseCrashlytics?.log(value)

    fun log(priority: Int, tag: String, value: String) = firebaseCrashlytics?.log(priority, tag, value)

    // Customizing
    fun setBool(key: String, value: Boolean) = firebaseCrashlytics?.setBool(key, value)

    fun setInt(key: String, value: Int) = firebaseCrashlytics?.setInt(key, value)

    fun setString(key: String, value: String) = firebaseCrashlytics?.setString(key, value)
}
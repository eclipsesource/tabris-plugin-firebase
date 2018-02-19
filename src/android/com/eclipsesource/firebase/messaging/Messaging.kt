package com.eclipsesource.firebase.messaging

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.eclipsesource.tabris.android.TabrisActivity
import com.eclipsesource.tabris.android.TabrisContext
import com.eclipsesource.tabris.android.internal.toolkit.AppState
import com.eclipsesource.tabris.android.internal.toolkit.AppState.*
import com.eclipsesource.tabris.android.internal.toolkit.IAppStateListener
import com.google.firebase.iid.FirebaseInstanceId
import java.io.IOException
import java.util.concurrent.Executors

internal const val ACTION_TOKEN_REFRESH = "com.eclipsesource.firebase.messaging.TOKEN_REFRESH"
internal const val ACTION_MESSAGE = "com.eclipsesource.firebase.messaging.MESSAGE"
internal const val ACTION_LAUNCH_TABRIS_ACTIVITY = "com.eclipsesource.firebase.messaging.LAUNCH_TABRIS_ACTIVITY"
internal const val EXTRA_DATA = "com.eclipsesource.firebase.messaging.EXTRA_DATA"
internal const val EXTRA_TOKEN = "com.eclipsesource.firebase.messaging.EXTRA_TOKEN"

private const val EVENT_MESSAGE = "message"
private const val EVENT_TOKEN_CHANGED = "tokenChanged"
private const val EVENT_INSTANCE_ID_CHANGED = "instanceIdChanged"
private const val PROP_DATA = "data"
private const val PROP_INSTANCE_ID = "instanceId"
private const val PROP_TOKEN = "token"

class Messaging(activity: Activity, private val tabrisContext: TabrisContext) : IAppStateListener {

  val launchData = (activity as TabrisActivity).intentOfCreate?.getSerializableExtra(EXTRA_DATA)

  private val tokenReceiver = TokenReceiver()
  private val messageReceiver = MessageReceiver()
  private val launchTabrisActivityReceiver = LaunchTabrisActivityReceiver()
  private val broadcastManager = LocalBroadcastManager.getInstance(activity.applicationContext)

  init {
    tabrisContext.widgetToolkit.addAppStateListener(this)
    registerMessageReceiver()
    registerTokenReceiver()
    registerLaunchTabrisActivityReceiver()
  }

  override fun stateChanged(appState: AppState, intent: Intent?) {
    when (appState) {
      START -> registerMessageReceiver()
      NEW_INTENT -> notifyOfMessage(intent)
      STOP -> broadcastManager.unregisterReceiver(messageReceiver)
      else -> Unit
    }
  }

  private fun registerMessageReceiver() {
    broadcastManager.unregisterReceiver(messageReceiver)
    broadcastManager.registerReceiver(messageReceiver, IntentFilter(ACTION_MESSAGE))
  }

  private fun registerTokenReceiver() {
    broadcastManager.registerReceiver(tokenReceiver, IntentFilter(ACTION_TOKEN_REFRESH))
  }

  private fun registerLaunchTabrisActivityReceiver() {
    broadcastManager.registerReceiver(launchTabrisActivityReceiver, IntentFilter(ACTION_LAUNCH_TABRIS_ACTIVITY))
  }

  fun unregisterAllListeners() {
    with(broadcastManager) {
      unregisterReceiver(tokenReceiver)
      unregisterReceiver(messageReceiver)
      unregisterReceiver(launchTabrisActivityReceiver)
    }
  }

  private fun notifyOfMessage(intent: Intent?) {
    if (intent != null && intent.hasExtra(EXTRA_DATA)) {
      tabrisContext.objectRegistry.getRemoteObjectForObject(this)
          ?.notify(EVENT_MESSAGE, PROP_DATA, intent.getSerializableExtra(EXTRA_DATA))
    }
  }

  fun resetInstanceId() {
    Executors.newSingleThreadExecutor().submit {
      try {
        val instanceId = FirebaseInstanceId.getInstance()
        instanceId.deleteInstanceId()
        tabrisContext.widgetToolkit.executeInUiThread {
          tabrisContext.objectRegistry.getRemoteObjectForObject(this)
              ?.notify(EVENT_INSTANCE_ID_CHANGED, PROP_INSTANCE_ID, instanceId.id)
        }
        instanceId.token // will trigger a "tokenChanged" event
      } catch (e: IOException) {
        Log.e(Messaging::class.java.simpleName, "Could not reset firebase messaging instance id", e)
      }
    }
  }

  private class LaunchTabrisActivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      context.startActivity(Intent(context, TabrisActivity::class.java).apply {
        addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP)
        putExtra(EXTRA_DATA, intent.getSerializableExtra(EXTRA_DATA))
      })
    }
  }

  private inner class TokenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      if (intent.action == ACTION_TOKEN_REFRESH) {
        tabrisContext.objectRegistry.getRemoteObjectForObject(this)
            ?.notify(EVENT_TOKEN_CHANGED, PROP_TOKEN, intent.getStringExtra(EXTRA_TOKEN))
      }
    }

  }

  private inner class MessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      if (intent.action == ACTION_MESSAGE) {
        notifyOfMessage(intent)
      }
    }

  }

}

package com.eclipsesource.firebase.messaging

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.eclipsesource.tabris.android.*
import com.eclipsesource.tabris.android.ActivityState.NEW_INTENT
import com.eclipsesource.tabris.android.ActivityState.START
import com.eclipsesource.tabris.android.ActivityState.STOP
import com.google.firebase.iid.FirebaseInstanceId
import java.io.IOException
import java.io.Serializable
import java.util.concurrent.Executors

internal const val ACTION_TOKEN_REFRESH = "com.eclipsesource.firebase.messaging.TOKEN_REFRESH"
internal const val ACTION_MESSAGE = "com.eclipsesource.firebase.messaging.MESSAGE"
internal const val ACTION_LAUNCH_TABRIS_ACTIVITY = "com.eclipsesource.firebase.messaging.LAUNCH_TABRIS_ACTIVITY"
internal const val EXTRA_DATA = "com.eclipsesource.firebase.messaging.EXTRA_DATA"
internal const val EXTRA_TOKEN = "com.eclipsesource.firebase.messaging.EXTRA_TOKEN"

class Messaging(private val scope: ActivityScope) : Events.ActivityStateListener {

  val launchData: Serializable? = scope.activityCreationIntent.getSerializableExtra(EXTRA_DATA)

  private val tokenReceiver = TokenReceiver()
  private val messageReceiver = MessageReceiver()
  private val launchTabrisActivityReceiver = LaunchTabrisActivityReceiver()
  private val broadcastManager = LocalBroadcastManager.getInstance(scope.context)

  init {
    scope.events.addActivityStateListener(this)
    registerMessageReceiver()
    registerTokenReceiver()
    registerLaunchTabrisActivityReceiver()
  }

  override fun activityStateChanged(activityState: ActivityState, intent: Intent?) {
    when (activityState) {
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
    intent?.getSerializableExtra(EXTRA_DATA)?.let {
      scope.remoteObject(this)?.notify("message", "data", it)
    }
  }

  fun resetInstanceId() {
    Executors.newSingleThreadExecutor().submit {
      try {
        val instanceId = FirebaseInstanceId.getInstance()
        instanceId.deleteInstanceId()
        scope.post { remoteObject(this)?.notify("instanceIdChanged", "instanceId", instanceId.id) }
        instanceId.token // will trigger a "tokenChanged" event
      } catch (e: IOException) {
        Log.e(Messaging::class.java.simpleName, "Could not reset firebase messaging instance id", e)
      }
    }
  }

  fun clearAllPendingMessages() {
    NotificationManagerCompat.from(scope.context).cancelAll()
  }

  fun getAllPendingMessages(): List<Serializable> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return (scope.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).activeNotifications
          .asSequence()
          .sortedBy { it.postTime }
          .map { it.notification.extras.getSerializable(EXTRA_DATA) }
          .toList()
    }
    return emptyList()
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
        scope.remoteObject(this)?.notify("tokenChanged", "token", intent.getStringExtra(EXTRA_TOKEN))
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

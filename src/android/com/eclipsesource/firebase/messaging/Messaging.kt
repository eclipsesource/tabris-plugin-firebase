package com.eclipsesource.firebase.messaging

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import com.eclipsesource.tabris.android.ActivityScope
import com.eclipsesource.tabris.android.ActivityState
import com.eclipsesource.tabris.android.ActivityState.*
import com.eclipsesource.tabris.android.Events.ActivityStateListener
import com.eclipsesource.tabris.android.post
import com.google.firebase.iid.FirebaseInstanceId
import java.io.IOException
import java.io.Serializable
import java.util.concurrent.Executors

internal const val EXTRA_DATA = "com.eclipsesource.firebase.messaging.EXTRA_DATA"

class Messaging(private val scope: ActivityScope) : ActivityStateListener {

  val launchData: Serializable? = scope.activity.intent.getSerializableExtra(EXTRA_DATA)

  init {
    scope.events.addActivityStateListener(this)
    MessagingHandler.resetMessaging()
    val currentState = scope.activity.lifecycle.currentState
    if (currentState == STARTED || currentState == RESUMED) {
      updateMessaging()
    }
  }

  override fun activityStateChanged(activityState: ActivityState, intent: Intent?) {
    when (activityState) {
      START -> updateMessaging()
      NEW_INTENT -> {
        intent?.let { data ->
          @Suppress("UNCHECKED_CAST")
          (data.getSerializableExtra(EXTRA_DATA) as? Map<String, String>)?.let {
            notifyOfMessage(it)
          }
        }
      }
      STOP -> MessagingHandler.resetMessaging()
      else -> Unit
    }
  }

  fun resetInstanceId() {
    Executors.newSingleThreadExecutor().submit {
      try {
        val instanceId = FirebaseInstanceId.getInstance()
        instanceId.deleteInstanceId()
        scope.post { remoteObject(this)?.notify("instanceIdChanged", "instanceId", instanceId.id) }
        instanceId.instanceId // will trigger a "onNewToken" event
      } catch (e: IOException) {
        scope.log.error("Could not reset firebase messaging instance id", e)
      }
    }
  }

  fun clearAllPendingMessages() {
    NotificationManagerCompat.from(scope.context).cancelAll()
  }

  fun getAllPendingMessages(): List<Serializable?> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return requireNotNull(scope.context.getSystemService<NotificationManager>()).activeNotifications
        .asSequence()
        .sortedBy { it.postTime }
        .mapNotNull { it.notification.extras.getSerializable(EXTRA_DATA) }
        .toList()
    }
    return emptyList()
  }

  fun onMessageReceived(data: Map<String, String>) = notifyOfMessage(data)

  fun onTokenReceived(token: String) {
    scope.post {
      scope.remoteObject(this@Messaging)?.notify("tokenChanged", "token", token)
    }
  }

  private fun notifyOfMessage(data: Map<String, String>) {
    scope.post {
      scope.remoteObject(this@Messaging)?.notify("message", "data", data)
    }
  }

  private fun updateMessaging() {
    try {
      MessagingHandler.setMessaging(this)
    } catch (e: Exception) {
      scope.log.error(e)
    }
  }

}

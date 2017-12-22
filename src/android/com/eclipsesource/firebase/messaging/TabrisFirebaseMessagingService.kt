package com.eclipsesource.firebase.messaging

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

// There are two types of messages data messages and notification messages. Data messages are handled
// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
// is in the foreground. When the app is in the background an automatically generated notification is displayed.
// When the user taps on the notification they are returned to the app. Messages containing both notification
// and data payloads are treated as notification messages. The Firebase console always sends notification
// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

private const val META_DATA_ICON = "com.google.firebase.messaging.default_notification_icon"
private const val DATA_KEY_ID = "id"
private const val DATA_KEY_TITLE = "title"
private const val DATA_KEY_BODY = "body"
private const val NOTIFICATION_CHANNEL_DEFAULT = "default"

class TabrisFirebaseMessagingService : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    val intent = Intent(ACTION_MESSAGE).apply { putExtra(EXTRA_DATA, HashMap(remoteMessage.data)) }
    if (!LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)) {
      // if nobody consumed the message broadcast the app is in the background
      showNotification(remoteMessage)
    }
  }

  private fun showNotification(remoteMessage: RemoteMessage) {
    remoteMessage.data?.let {
      val id = getId(it)
      val title = it[DATA_KEY_TITLE]
      val body = it[DATA_KEY_BODY]
      if (!title.isNullOrBlank() && !body.isNullOrBlank()) {
        NotificationManagerCompat.from(this)
            .notify(id, createNotification(id, title!!, body!!, it))
      }
    }
  }

  private fun getId(data: Map<String, String>): Int {
    try {
      return Integer.parseInt(data[DATA_KEY_ID])
    } catch (ignored: Exception) {
      return Random().nextInt()
    }
  }

  private fun createNotification(id: Int, title: String, text: String, data: Map<String, String>): Notification {
    createNotificationChannel()
    val contentIntent = PendingIntent.getBroadcast(this, id,
        createLaunchIntent(data), PendingIntent.FLAG_UPDATE_CURRENT)
    return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_DEFAULT)
        .setContentTitle(title)
        .setContentText(text)
        .setStyle(NotificationCompat.BigTextStyle().bigText(text))
        .setAutoCancel(true)
        .setSmallIcon(getIcon())
        .setContentIntent(contentIntent).build()
  }

  private fun getIcon(): Int {
    try {
      val info = packageManager.getApplicationInfo(packageName, GET_META_DATA)
      return info.metaData.getInt(META_DATA_ICON, applicationInfo.icon)
    } catch (e: Exception) {
      return applicationInfo.icon
    }
  }

  private fun createNotificationChannel() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      val notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_DEFAULT) == null) {
        notificationManager.createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL_DEFAULT,
            packageManager.getApplicationLabel(applicationInfo), IMPORTANCE_DEFAULT))
      }
    }
  }

  private fun createLaunchIntent(data: Map<String, String>): Intent {
    val intent = Intent(this, NotificationOpenedReceiver::class.java)
    val bundle = Bundle()
    bundle.putSerializable(EXTRA_DATA, HashMap(data))
    intent.putExtras(bundle)
    return intent
  }

}

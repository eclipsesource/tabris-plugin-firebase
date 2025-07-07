package com.eclipsesource.firebase.messaging

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
class TabrisFirebaseMessagingService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    MessagingHandler.tokenReceived(token)
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    if (!MessagingHandler.messageReceived(remoteMessage.data)) {
      // if nobody consumed the message broadcast the app is in the background
      showNotification(remoteMessage)
    }
  }

  private fun showNotification(remoteMessage: RemoteMessage) {
    val data = remoteMessage.data
    val id = data[DATA_KEY_ID]?.toInt() ?: Random().nextInt()
    val title = data[DATA_KEY_TITLE]
    val body = data[DATA_KEY_BODY]
    if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
      NotificationManagerCompat.from(this).notify(id, createNotification(title, body, data))
    }
  }

  private fun createNotification(
    title: String, text: String, data: Map<String, String>
  ): Notification {
    createNotificationChannel()
    return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_DEFAULT)
      .setExtras(Bundle().apply { putSerializable(EXTRA_DATA, HashMap(data)) })
      .setContentTitle(title)
      .setContentText(text)
      .setStyle(NotificationCompat.BigTextStyle().bigText(text))
      .setAutoCancel(true)
      .setSmallIcon(getIcon())
      .setContentIntent(createContentIntent(data))
      .build()
  }

  private fun getIcon() = try {
    packageManager.getApplicationInfo(packageName, GET_META_DATA).metaData.getInt(
      META_DATA_ICON,
      applicationInfo.icon
    )
  } catch (e: Exception) {
    applicationInfo.icon
  }

  private fun createNotificationChannel() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      baseContext.getSystemService<NotificationManager>()?.let { notificationManager ->
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_DEFAULT) == null) {
          notificationManager.createNotificationChannel(
            NotificationChannel(
              NOTIFICATION_CHANNEL_DEFAULT,
              packageManager.getApplicationLabel(applicationInfo),
              IMPORTANCE_DEFAULT
            )
          )
        }
      }
    }
  }

  private fun createContentIntent(data: Map<String, String>): PendingIntent {
    val intent = packageManager.getLaunchIntentForPackage(packageName)?.let {
      it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      it.putExtra(EXTRA_DATA, HashMap(data))
    }
    return PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
  }

}

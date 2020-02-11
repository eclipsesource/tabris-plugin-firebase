package com.eclipsesource.firebase.messaging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import java.io.Serializable

class NotificationOpenedReceiver : BroadcastReceiver() {

  @Suppress("UNCHECKED_CAST")
  override fun onReceive(context: Context, intent: Intent) {
    val data = intent.getSerializableExtra(EXTRA_DATA) as HashMap<String, String>
    if (!MessagingHandler.messageReceived(data)) {
      // if nobody consumed the data broadcast the app is in the background
      context.startActivity(createAppIntent(context, data))
    }
  }

  private fun createAppIntent(context: Context, data: Serializable) =
      context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        it.putExtra(EXTRA_DATA, data)
      }

}

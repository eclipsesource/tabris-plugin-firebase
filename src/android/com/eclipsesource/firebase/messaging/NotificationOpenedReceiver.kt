package com.eclipsesource.firebase.messaging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

import java.io.Serializable

class NotificationOpenedReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val data = intent.getSerializableExtra(EXTRA_DATA)
    if (!sendDataBroadcast(context, ACTION_MESSAGE, data)) {
      // if nobody consumed the data broadcast the app is in the background
      launchActivity(context, data)
    }
  }

  private fun launchActivity(context: Context, data: Serializable) {
    if (!sendDataBroadcast(context, ACTION_LAUNCH_TABRIS_ACTIVITY, data)) {
      // if nobody resolved the action to launch the tabris activity into the foreground
      // we launch the entire app
      context.startActivity(createAppIntent(context, data))
    }
  }

  private fun sendDataBroadcast(context: Context, action: String, data: Serializable) =
      LocalBroadcastManager.getInstance(context.applicationContext)
          .sendBroadcast(Intent(action).apply { putExtra(EXTRA_DATA, data) })

  private fun createAppIntent(context: Context, data: Serializable) =
      context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        it.putExtra(EXTRA_DATA, data)
      }

}

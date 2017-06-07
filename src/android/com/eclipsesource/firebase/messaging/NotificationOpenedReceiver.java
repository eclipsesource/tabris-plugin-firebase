package com.eclipsesource.firebase.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;

public class NotificationOpenedReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Serializable data = intent.getSerializableExtra(Messaging.EXTRA_DATA);
    if (!sendDataBroadcast(context, Messaging.ACTION_MESSAGE, data)) {
      // if nobody consumed the data broadcast the app is in the background
      launchActivity(context, data);
    }
  }

  private void launchActivity(Context context, Serializable data) {
    if (!sendDataBroadcast(context, Messaging.ACTION_LAUNCH_TABRIS_ACTIVITY, data)) {
      // if nobody resolved the action to launch the tabris activity into the foreground
      // we launch the entire app
      context.startActivity(createAppIntent(context, data));
    }
  }

  private boolean sendDataBroadcast(Context context, String action, Serializable data) {
    Intent intent = new Intent(action);
    intent.putExtra(Messaging.EXTRA_DATA, data);
    return getBroadcastManager(context).sendBroadcast(intent);
  }

  @NonNull
  private Intent createAppIntent(Context context, Serializable data) {
    Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra(Messaging.EXTRA_DATA, data);
    return intent;
  }

  private LocalBroadcastManager getBroadcastManager(Context context) {
    return LocalBroadcastManager.getInstance(context.getApplicationContext());
  }

}

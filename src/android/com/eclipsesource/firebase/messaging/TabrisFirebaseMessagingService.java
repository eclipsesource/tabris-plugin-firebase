package com.eclipsesource.firebase.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.content.pm.PackageManager.GET_META_DATA;
import static android.text.TextUtils.isEmpty;

public class TabrisFirebaseMessagingService extends FirebaseMessagingService {

  private static final String META_DATA_ICON = "com.google.firebase.messaging.default_notification_icon";
  private static final String DATA_KEY_ID = "id";
  private static final String DATA_KEY_TITLE = "title";
  private static final String DATA_KEY_BODY = "body";
  private static final String NOTIFICATION_CHANNEL_DEFAULT = "default";

  // There are two types of messages data messages and notification messages. Data messages are handled
  // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
  // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
  // is in the foreground. When the app is in the background an automatically generated notification is displayed.
  // When the user taps on the notification they are returned to the app. Messages containing both notification
  // and data payloads are treated as notification messages. The Firebase console always sends notification
  // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    Intent intent = new Intent(Messaging.ACTION_MESSAGE);
    intent.putExtra(Messaging.EXTRA_DATA, new HashMap<>(remoteMessage.getData()));
    if (!LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent)) {
      // if nobody consumed the message broadcast the app is in the background
      showNotification(remoteMessage);
    }
  }

  private void showNotification(RemoteMessage remoteMessage) {
    Map<String, String> data = remoteMessage.getData();
    if (data != null) {
      int id = getId(data);
      String title = data.get(DATA_KEY_TITLE);
      String body = data.get(DATA_KEY_BODY);
      if (!(isEmpty(title) && isEmpty(body))) {
        NotificationManagerCompat.from(this).notify(id, createNotification(id, title, body, data));
      }
    }
  }

  private int getId(Map<String, String> data) {
    try {
      return Integer.parseInt(data.get(DATA_KEY_ID));
    } catch (Exception ignored) {
      return new Random().nextInt();
    }
  }

  private Notification createNotification(int id, String title, String text, Map<String, String> data) {
    createNotificationChannel();
    PendingIntent contentIntent = PendingIntent.getBroadcast(this, id,
        createLaunchIntent(data), PendingIntent.FLAG_UPDATE_CURRENT);
    return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_DEFAULT)
        .setContentTitle(title)
        .setContentText(text)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
        .setAutoCancel(true)
        .setSmallIcon(getIcon())
        .setContentIntent(contentIntent).build();
  }

  private void createNotificationChannel() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationManager notificationManager = (NotificationManager) getBaseContext()
          .getSystemService(Context.NOTIFICATION_SERVICE);
      if (notificationManager != null
          && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_DEFAULT) == null) {
        notificationManager.createNotificationChannel(
            new NotificationChannel(NOTIFICATION_CHANNEL_DEFAULT, getAppName(), IMPORTANCE_DEFAULT));
      }
    }
  }

  private CharSequence getAppName() {
    return getPackageManager().getApplicationLabel(getApplicationInfo());
  }

  private int getIcon() {
    int defaultIcon = getApplicationInfo().icon;
    try {
      ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), GET_META_DATA);
      return info.metaData.getInt(META_DATA_ICON, defaultIcon);
    } catch (Exception e) {
      return defaultIcon;
    }
  }

  @NonNull
  private Intent createLaunchIntent(Map<String, String> data) {
    Intent intent = new Intent(this, NotificationOpenedReceiver.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(Messaging.EXTRA_DATA, new HashMap<>(data));
    intent.putExtras(bundle);
    return intent;
  }

}

package com.eclipsesource.firebase.messaging;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.eclipsesource.tabris.android.RemoteObject;
import com.eclipsesource.tabris.android.TabrisActivity;
import com.eclipsesource.tabris.android.TabrisContext;
import com.eclipsesource.tabris.android.internal.toolkit.AppState;
import com.eclipsesource.tabris.android.internal.toolkit.IAppStateListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Messaging implements IAppStateListener {

  static final String ACTION_TOKEN_REFRESH = "com.eclipsesource.firebase.messaging.TOKEN_REFRESH";
  static final String ACTION_MESSAGE = "com.eclipsesource.firebase.messaging.MESSAGE";
  static final String ACTION_LAUNCH_TABRIS_ACTIVITY = "com.eclipsesource.firebase.messaging.LAUNCH_TABRIS_ACTIVITY";

  static final String EXTRA_DATA = "com.eclipsesource.firebase.messaging.EXTRA_DATA";
  static final String EXTRA_TOKEN = "com.eclipsesource.firebase.messaging.EXTRA_TOKEN";

  private Activity activity;
  private final TabrisContext tabrisContext;
  private final TokenReceiver tokenReceiver;
  private final MessageReceiver messageReceiver;
  private final LaunchTabrisActivityReceiver launchTabrisActivityReceiver;

  Messaging(Activity activity, TabrisContext tabrisContext) {
    this.activity = activity;
    this.tabrisContext = tabrisContext;
    tokenReceiver = new TokenReceiver();
    messageReceiver = new MessageReceiver();
    launchTabrisActivityReceiver = new LaunchTabrisActivityReceiver();
    tabrisContext.getWidgetToolkit().addAppStateListener(this);
    registerMessageReceiver();
    registerTokenReceiver();
    registerLaunchTabrisActivityReceiver();
  }

  @Override
  public void stateChanged(AppState appState, Intent intent) {
    switch (appState) {
      case START:
        registerMessageReceiver();
        break;
      case NEW_INTENT:
        if (intent != null && intent.hasExtra(EXTRA_DATA)) {
          notifyOfMessage(intent);
        }
        break;
      case STOP:
        getBroadcastManager().unregisterReceiver(messageReceiver);
        break;
    }
  }

  private void registerMessageReceiver() {
    getBroadcastManager().registerReceiver(messageReceiver, new IntentFilter(ACTION_MESSAGE));
  }

  private void registerTokenReceiver() {
    getBroadcastManager().registerReceiver(tokenReceiver, new IntentFilter(ACTION_TOKEN_REFRESH));
  }

  private void registerLaunchTabrisActivityReceiver() {
    getBroadcastManager().registerReceiver(launchTabrisActivityReceiver, new IntentFilter(ACTION_LAUNCH_TABRIS_ACTIVITY));
  }

  void unregisterAllListeners() {
    getBroadcastManager().unregisterReceiver(tokenReceiver);
    getBroadcastManager().unregisterReceiver(messageReceiver);
  }

  private LocalBroadcastManager getBroadcastManager() {
    return LocalBroadcastManager.getInstance(activity.getApplicationContext());
  }

  Object getLaunchData() {
    Intent intent = ((TabrisActivity)activity).getIntentOfCreate();
    if (intent != null) {
      return intent.getSerializableExtra(EXTRA_DATA);
    }
    return null;
  }

  private void notifyOfMessage(Intent intent) {
    Serializable data = intent.getSerializableExtra(EXTRA_DATA);
    tabrisContext.getObjectRegistry().getRemoteObjectForObject(Messaging.this)
        .notify("message", "data", data);
  }

  void resetInstanceId() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          FirebaseInstanceId.getInstance().deleteInstanceId();
          tabrisContext.getWidgetToolkit().executeInUiThread(new Runnable() {
            @Override
            public void run() {
              RemoteObject remoteObject = tabrisContext.getObjectRegistry().getRemoteObjectForObject(Messaging.this);
              if (remoteObject != null) {
                String instanceId = FirebaseInstanceId.getInstance().getId();
                remoteObject.notify("instanceIdChanged", "instanceId", instanceId);
              }
            }
          });
          FirebaseInstanceId.getInstance().getToken(); // will trigger a "tokenChanged" event
        } catch (IOException e) {
          Log.e(Messaging.class.getSimpleName(), "Could not reset firebase messaging instance id", e);
        }
      }
    });
  }

  private static class LaunchTabrisActivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Intent launchIntent = new Intent(context, TabrisActivity.class);
      launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
          | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      launchIntent.putExtra(Messaging.EXTRA_DATA, intent.getSerializableExtra(EXTRA_DATA));
      context.startActivity(launchIntent);
    }
  }

  private class TokenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_TOKEN_REFRESH)) {
        String token = intent.getStringExtra(EXTRA_TOKEN);
        tabrisContext.getObjectRegistry().getRemoteObjectForObject(Messaging.this)
            .notify("tokenChanged", "token", token);
      }
    }

  }

  private class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_MESSAGE)) {
        notifyOfMessage(intent);
      }
    }

  }

}

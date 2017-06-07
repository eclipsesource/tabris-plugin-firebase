package com.eclipsesource.firebase.messaging;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class TabrisFirebaseInstanceIDService extends FirebaseInstanceIdService {

  @Override
  public void onTokenRefresh() {
    Intent intent = new Intent(Messaging.ACTION_TOKEN_REFRESH);
    intent.putExtra(Messaging.EXTRA_TOKEN, FirebaseInstanceId.getInstance().getToken());
    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
  }

}
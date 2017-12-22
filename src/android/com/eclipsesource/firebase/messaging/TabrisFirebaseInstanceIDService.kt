package com.eclipsesource.firebase.messaging

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class TabrisFirebaseInstanceIDService : FirebaseInstanceIdService() {

  override fun onTokenRefresh() {
    val intent = Intent(ACTION_TOKEN_REFRESH)
    intent.putExtra(EXTRA_TOKEN, FirebaseInstanceId.getInstance().token)
    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
  }

}
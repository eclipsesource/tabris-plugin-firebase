package com.eclipsesource.firebase.messaging

import com.eclipsesource.tabris.android.PropertyHandlerAdapter
import com.google.firebase.iid.FirebaseInstanceId

private const val PROP_INSTANCE_ID = "instanceId"
private const val PROP_TOKEN = "token"
private const val PROP_LAUNCH_DATA = "launchData"

class MessagingPropertyHandler : PropertyHandlerAdapter<Messaging>() {

  override fun get(messaging: Messaging, property: String) = when (property) {
    PROP_INSTANCE_ID -> FirebaseInstanceId.getInstance().id
    PROP_TOKEN -> FirebaseInstanceId.getInstance().token
    PROP_LAUNCH_DATA -> messaging.launchData
    else -> null
  }

}

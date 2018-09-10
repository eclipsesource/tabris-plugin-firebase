package com.eclipsesource.firebase.messaging

import android.app.Activity
import com.eclipsesource.tabris.android.AbstractOperator
import com.eclipsesource.tabris.android.Properties
import com.eclipsesource.tabris.android.TabrisContext

private const val TYPE = "com.eclipsesource.firebase.Messaging"
private const val METHOD_RESET_INSTANCE_ID = "resetInstanceId"
private const val METHOD_GET_ALL = "getAllPendingMessages"
private const val METHOD_CLEAR_ALL = "clearAllPendingMessages"

class MessagingOperator(private val activity: Activity, private val tabrisContext: TabrisContext)
  : AbstractOperator<Messaging>() {

  private val propertyHandler by lazy { MessagingPropertyHandler() }

  override fun getType() = TYPE

  override fun getPropertyHandler(messaging: Messaging) = propertyHandler

  override fun create(id: String, properties: Properties) = Messaging(activity, tabrisContext)

  override fun call(messaging: Messaging, method: String, properties: Properties): Any? = when (method) {
    METHOD_RESET_INSTANCE_ID -> messaging.resetInstanceId()
    METHOD_CLEAR_ALL -> messaging.clearAllPendingMessages()
    METHOD_GET_ALL -> messaging.getAllPendingMessages()
    else -> null
  }

  override fun destroy(messaging: Messaging) = messaging.unregisterAllListeners()

}

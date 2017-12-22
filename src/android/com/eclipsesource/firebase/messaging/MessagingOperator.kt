package com.eclipsesource.firebase.messaging

import android.app.Activity
import com.eclipsesource.tabris.android.AbstractOperator
import com.eclipsesource.tabris.android.Properties
import com.eclipsesource.tabris.android.TabrisContext

private const val TYPE = "com.eclipsesource.firebase.Messaging"
private const val METHOD_RESET_INSTANCE_ID = "resetInstanceId"

class MessagingOperator(private val activity: Activity, private val tabrisContext: TabrisContext)
  : AbstractOperator<Messaging>() {

  private val propertyHandler by lazy { MessagingPropertyHandler() }

  override fun getType() = TYPE

  override fun getPropertyHandler(messaging: Messaging) = propertyHandler

  override fun create(id: String, properties: Properties) = Messaging(activity, tabrisContext)

  override fun call(messaging: Messaging, method: String, properties: Properties) = when (method) {
    METHOD_RESET_INSTANCE_ID -> messaging.resetInstanceId()
    else -> null
  }

  override fun destroy(messaging: Messaging) = messaging.unregisterAllListeners()

}

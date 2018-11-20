package com.eclipsesource.firebase.messaging

import com.eclipsesource.tabris.android.*
import com.eclipsesource.v8.V8Object
import com.google.firebase.iid.FirebaseInstanceId

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class MessagingHandler(private val scope: ActivityScope) : ObjectHandler<Messaging> {

  override val type = "com.eclipsesource.firebase.Messaging"

  override val properties = listOf<Property<Messaging, *>>(
      StringProperty("instanceId") { FirebaseInstanceId.getInstance().id },
      StringProperty("token") { FirebaseInstanceId.getInstance().token },
      V8ObjectProperty("launchData") { launchData }
  )

  override fun create(id: String, properties: V8Object) = Messaging(scope)

  override fun call(messaging: Messaging, method: String, properties: V8Object): Any? = when (method) {
    "resetInstanceId" -> messaging.resetInstanceId()
    "clearAllPendingMessages" -> messaging.clearAllPendingMessages()
    "getAllPendingMessages" -> messaging.getAllPendingMessages()
    else -> null
  }

  override fun destroy(nativeObject: Messaging) = nativeObject.unregisterAllListeners()

}

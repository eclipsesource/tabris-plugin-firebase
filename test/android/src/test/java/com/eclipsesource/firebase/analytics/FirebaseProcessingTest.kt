package com.eclipsesource.firebase.analytics

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.measurement.api.AppMeasurementSdk
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FirebaseProcessingTest {

  @Test fun `Firebase processing preserves supported item fields for all affected events`() {
    val context = RuntimeEnvironment.getApplication()
    shadowOf(context).grantPermissions("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE")
    FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
      .setApplicationId("1:123456789:android:1234567890abcdef")
      .setApiKey("test-api-key")
      .setProjectId("analytics-local-regression").build())
    val sdk = AppMeasurementSdk.getInstance(context)
    val processed = LinkedBlockingQueue<Pair<String, Bundle>>()
    val listener = AppMeasurementSdk.OnEventListener { _, name, params, _ ->
      processed.offer(name to params)
    }
    sdk.registerOnMeasurementEventListener(listener)
    sdk.setMeasurementEnabled(true)
    val activity = mockk<Activity>()
    every { activity.applicationContext } returns context
    val analytics = Analytics(activity)
    val events = listOf("view_item_list", "view_search_results", "begin_checkout",
      "checkout_progress", "ecommerce_purchase")
    try {
      for (event in events) {
        analytics.logEvent(event, obj(mapOf(
          "items" to array(*(1..10).map { product(it) }.toTypedArray()),
          "item_list" to "Favourites", "screen_name" to "Favourites",
          "search_term" to "pump", "checkout_step" to 2,
          "currency" to "EUR", "value" to 123.45,
          "shipping" to 4.5, "tax" to 20.0, "transaction_id" to "order-123"
        )))
        var result = processed.poll(15, TimeUnit.SECONDS)
        while (result != null && result.first != event) result = processed.poll(15, TimeUnit.SECONDS)
        assertNotNull("No processed callback for $event", result)
        val params = result!!.second
        assertFalse("$event: unexpected Firebase error", params.containsKey("_err"))
        val items = params.getParcelableArray("items")!!
        assertEquals(event, 10, items.size)
        items.forEachIndexed { index, parcelable ->
          val item = parcelable as Bundle
          val expected = productFields(index + 1)
          val supportedKeys = if (System.getProperty("firebaseVersion") == "21.1.1") {
            expected.keys - setOf("item_available", "on_stock_value", "product_category")
          } else expected.keys
          for (key in supportedKeys) {
            val value = expected[key]
            assertEquals("$event item $index $key", if (value is Int) value.toLong() else value, item.get(key))
          }
          assertFalse("$event item $index: numeric parameter name", item.keySet().any { it.all(Char::isDigit) })
          if (System.getProperty("firebaseVersion") == "21.1.1") {
            // This SDK disallows custom item parameters independently of array conversion.
            assertEquals(23L, item.getLong("_err"))
          } else {
            assertFalse("$event item $index: Firebase error", item.containsKey("_err"))
          }
        }
        assertEquals("Favourites", params.getString("item_list"))
        assertEquals("Favourites", params.getString("screen_name"))
        assertEquals("pump", params.getString("search_term"))
        assertEquals(2L, params.getLong("checkout_step"))
        assertEquals(123.45, params.getDouble("value"), 0.0)
        assertEquals("EUR", params.getString("currency"))
        assertEquals("order-123", params.getString("transaction_id"))
        assertEquals(4.5, params.getDouble("shipping"), 0.0)
        assertEquals(20.0, params.getDouble("tax"), 0.0)
      }
    } finally {
      sdk.unregisterOnMeasurementEventListener(listener)
    }
  }
}

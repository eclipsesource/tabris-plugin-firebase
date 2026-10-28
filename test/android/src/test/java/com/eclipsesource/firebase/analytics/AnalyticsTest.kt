package com.eclipsesource.firebase.analytics

import android.app.Activity
import android.os.Bundle
import com.eclipsesource.v8.V8Object
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class AnalyticsTest {

  private lateinit var analytics: Analytics
  private lateinit var firebase: FirebaseAnalytics

  @Before fun setUp() {
    firebase = mockk(relaxed = true)
    mockkStatic(FirebaseAnalytics::class)
    every { FirebaseAnalytics.getInstance(any()) } returns firebase
    val activity = mockk<Activity>()
    every { activity.applicationContext } returns mockk()
    analytics = Analytics(activity)
  }

  @After fun tearDown() = unmockkAll()

  @Test fun `all affected events retain ten products and event parameters at the Firebase boundary`() {
    val events = listOf("view_item_list", "view_search_results", "begin_checkout",
      "checkout_progress", "ecommerce_purchase")
    for (event in events) {
      val input = linkedMapOf<String, Any>(
        "items" to array(*(1..10).map { product(it) }.toTypedArray()),
        "item_list" to "Favourites", "screen_name" to "Favourites",
        "search_term" to "pump", "checkout_step" to 2,
        "currency" to "EUR", "value" to 123.45,
        "shipping" to 4.5, "tax" to 20.0, "transaction_id" to "order-123"
      )
      val bundle = log(event, obj(input))
      assertEquals(input.keys, bundle.keySet())
      input.filterKeys { it != "items" }.forEach { (key, value) ->
        assertEquals("$event: $key", if (value is Int) value.toLong() else value, bundle.get(key))
      }
      // A Bundle here reproduces the bug: its keys would be "0", "1", ... .
      assertTrue("$event: items must be a native list", bundle.get("items") is ArrayList<*>)
      val items = bundle.getParcelableArrayList<Bundle>("items")!!
      assertEquals(event, 10, items.size)
      items.forEachIndexed { index, item ->
        val expected = productFields(index + 1)
        assertEquals(expected.keys, item.keySet())
        expected.forEach { (key, value) ->
          assertEquals("$event: item $index $key", if (value is Int) value.toLong() else value, item.get(key))
        }
        assertFalse(item.keySet().any { it.all(Char::isDigit) })
        assertFalse(item.containsKey("_err"))
      }
    }
  }

  @Test fun `string arrays and nested object parameters still convert`() {
    val bundle = log("custom_event", obj(mapOf(
      "labels" to array("one", "two"), "context" to obj(mapOf("name" to "test"))
    )))
    assertEquals(listOf("one", "two"), bundle.getStringArrayList("labels"))
    assertEquals("test", bundle.getBundle("context")!!.getString("name"))
  }

  @Test fun `null event data remains an empty bundle`() {
    assertTrue(log("custom_event", null).isEmpty)
  }

  private fun log(event: String, data: V8Object?): Bundle {
    val captured = slot<Bundle>()
    every { firebase.logEvent(event, capture(captured)) } just Runs
    analytics.logEvent(event, data)
    return captured.captured
  }

}


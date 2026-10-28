package com.eclipsesource.firebase.analytics

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import io.mockk.every
import io.mockk.mockk

internal fun product(index: Int) = obj(productFields(index))

internal fun productFields(index: Int): Map<String, Any> = linkedMapOf(
  "item_id" to "product-$index", "item_name" to "Product $index",
  "item_category" to "Parts", "product_category" to "Pumps",
  "item_available" to "Available", "on_stock_value" to "> 10",
  "price" to index + 0.25, "currency" to "EUR", "quantity" to index,
  "value" to (index + 0.25) * index, "index" to index
)

internal fun obj(values: Map<String, Any>): V8Object = mockk<V8Object>().also { obj ->
  every { obj.keys } returns values.keys.toTypedArray()
  values.forEach { (key, value) -> every { obj.get(key) } returns value }
}

// Mocking V8Array preserves the inheritance relationship that caused the bug.
internal fun array(vararg values: Any): V8Array = mockk<V8Array>().also { array ->
  every { array.length() } returns values.size
  every { array.keys } returns values.indices.map(Int::toString).toTypedArray()
  values.forEachIndexed { index, value ->
    every { array.get(index) } returns value
    every { array.get(index.toString()) } returns value
  }
}

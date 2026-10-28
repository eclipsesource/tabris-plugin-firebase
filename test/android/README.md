# Android analytics regression tests

This standalone test build compiles the production `Analytics.kt` with the plugin's
Firebase Analytics dependency. Converter tests use Robolectric Bundles and mock
the V8 inputs and Firebase entry point. A separate processing test runs the real
Firebase SDK with synthetic project settings and checks its processed event
callbacks. No native V8 runtime or production Firebase credentials are needed.
It is separate from the legacy plugin example build so that its old Gradle/Java
requirements do not prevent running the regression tests.

Requires Java 17, Android SDK 36, Gradle 8.14.5, and a locally published Tabris
3.11.0 Maven repository (from the supplied tabris-android checkout):

```powershell
gradle -p test/android -PtabrisRepository=D:/Projects/Android/tabris-android/build/m2repository testDebugUnitTest
gradle -p test/android -PtabrisRepository=D:/Projects/Android/tabris-android/build/m2repository '-PfirebaseVersion=22.0.2' testDebugUnitTest
```

A compatible Gradle wrapper from the tabris-android checkout can be used instead
of `gradle`. The tests check all five affected event names, ten products per event,
all supplied product fields, integer/double values, and event-level parameters.
They also check that string arrays and nested objects still convert.

Both Firebase versions preserve ten items, product identification, category,
price, currency, quantity, value and index after local SDK validation, along with
the event parameters. Firebase 22.0.2 also preserves `item_available`,
`on_stock_value` and `product_category`. Firebase 21.1.1 rejects those three custom
item fields with item-level error 23; that SDK restriction is independent of this
converter bug. The converter itself preserves all supplied fields in both cases.

The processing test uses Firebase's local measurement implementation under
Robolectric. It does not verify uploads, console reporting, a real device's Google
Play services implementation, or the app's Tag Manager container.

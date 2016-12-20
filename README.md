# Tabris.js Firebase Plugin

The `tabris-plugin-firebase` plugin project provides a [Tabris.js](https://tabrisjs.com) API to receive [firebase cloud messages](https://firebase.google.com/docs/cloud-messaging/). The plugin allows to receive notification data when the app is in the foreground or to automatically display a notification when the app is not running or in the background. Currently only Android is supported with iOS support coming in the future.

![Firebase plugin on Android](assets/screenshots/firebase.png)

## Example

The following snippet shows how the `tabris-plugin-firebase` plugin can be used to receive a cloud message in a Tabris.js app.

```js
console.log('Token to send to server: ' + firebase.Messaging.token);

firebase.Messaging.on('tokenRefresh', (messaging, token) => console.log('Server token refreshed: ' + token));

firebase.Messaging.on('message', (messaging, data) => console.log('Received message: ' + JSON.stringify(data)));

console.log('Message from app cold start: ' + firebase.Messaging.launchData);
```

A more elaborate example can be found in the [example](example/) folder. It provides a Tabris.js cordova project that demonstrates the various features of the `tabris-plugin-firebase` integration. When building the example project make sure to run `npm install` inside its `www` folder to fetch the Tabris.js dependencies.

To [send a message](https://firebase.google.com/docs/cloud-messaging/send-message) from the server side a `curl` command similar to the following `POST` request can be used:

```shell
curl -X POST -H "Authorization: key=<server-key>" -H "Content-Type: application/json" -d '{
    "to": "<token>",
    "data": {
      "title": "New data",
      "text": "The new data arrived",
      "payload": "custom data"
    }
  }
' https://fcm.googleapis.com/fcm/send
```

## Integrating the plugin
Using the plugin follows the standard cordova plugin mechanism. The Tabris.js website provides detailed information on how to [integrate custom plugins](https://tabrisjs.com/documentation/latest/build#adding-plugins) in your Tabris.js app.

### Add the plugin to your project

The plugin can be added like any other cordova plugin. Either via the `cordova plugin add` command or as an entry in the apps `config.xml` (recommended when published):

```xml
<plugin name="tabris-plugin-firebase" spec="1.0.0" />
```

To fetch the latest development version use the GitHub url:

```xml
<plugin name="tabris-plugin-firebase" spec="https://github.com/eclipsesource/tabris-plugin-firebase" />
```

#### Notification icon

An Android [notification icon](https://developer.android.com/guide/practices/ui_guidelines/icon_design_status_bar.html) can be provided via the plugin variable `ANDROID_NOTIFICATION_ICON`. To configure an icon the name of an Android drawable inside the Android platform `res` folder has to be specified. A build hook can be used to copy the notification icon from your project into the `android` platform `res` folder. See the [example project](example/scripts/android/copy_icons.js) for a snippet to get you started.

The icon can be configured inside your apps `config.xml`:

```xml
<plugin name="tabris-plugin-firebase" spec="1.0.0">
  <variable name="ANDROID_NOTIFICATION_ICON" value="icon_drawable_name" />
</plugin>
```

Alternatively the image can be added during the `cordova plugin add` command:

```bash
cordova plugin add <path-to-tabris-firebase-plugin> --variable ANDROID_NOTIFICATION_ICON=`icon_drawable_name`
```

## Sending message with notifications

When the server sends a message to a device it can create two types of messages: "notification" messages and "data" messages. Messages that contain a `notification` key in its json payload are treated as "notification" message. More details of the differences between "notification" and "data" messages can be found in the [firebase documentation](https://firebase.google.com/docs/cloud-messaging/concept-options#notifications_and_data_messages).

The key difference for this plugin is that "notification" messages create their notification automatically when the app is in the background. Tapping on the notification _does not forward the notification data to the app_.

To create a notification that also delivers its data to the app, when the notification is tapped, a regular "data" notification has to be created. A "data" message does _not_ have a key `notification`.

To configure the notification several properties can be set:

- `id` : `number`
- `title` : `string`
- `text` : `string`

The following message send from a server would create a notification similar to the [screenshot](assets/screenshots/firebase.png) above:

```shell
POST /fcm/send HTTP/1.1
Host: fcm.googleapis.com
Authorization: key=<server-key>
Content-Type: application/json

{
  "to": "<token>",
  "data": {
    "title": "New data available",
    "text": "The new data can be used in a multitude of ways",
    "payload": "custom data"
  }
}
```

Note that the json object above does not contain a `notification` key. It only provides the `to` key to declare the message receiver and the data payload send to the app.

Using the same `id` for multiple messages updates an existing notification on the users device. Omitting the `id` creates a random id on the device so that each message results in a unique notification.

## Compatibility

Compatible with [Tabris.js 2.0.0](https://github.com/eclipsesource/tabris-js/releases/tag/v2.0.0)

## Development of the widget

While not required by the consumer of the widget, this repository provides Android specific development artifacts. These artifacts allow to more easily consume the native source code when developing the native parts of the widget.

### Android

The project provides a gradle based build configuration, which also allows to import the project into Android Studio.

In order to reference the Tabris.js specific APIs, the environment variable `TABRIS_ANDROID_CORDOVA_PLATFORM` has to point to the Tabris.js Android Cordova platform root directory.

```bash
export TABRIS_ANDROID_CORDOVA_PLATFORM=/home/user/tabris-android-cordova
```
 The environment variable is consumed in the gradle projects [build.gradle](plugin/project/android/build.gradle) file.

## Copyright

Published under the terms of the [BSD 3-Clause License](LICENSE).

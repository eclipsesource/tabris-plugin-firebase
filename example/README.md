# Tabris.js Firebase Example

## Prerequisites

1. To run the example it is recommended to use the [Tabris.js CLI](https://www.npmjs.com/package/tabris-cli).

2. For Android place your `google-services.json` file in the root directory of the example project.

2. For iOS place your `GoogleService-Info.plist` file in the root directory of the example project and reference it as a `resource-file`:

```xml
<platform name="ios">
  <resource-file src="GoogleService-Info.plist" />
</platform>
```

## Running the Example

Using the Tabris.js CLI the example can be started via

```sh
$ tabris run <platform>
```

# Tabris.js Firebase Example

## Prerequisites

1. To run the example it is recommended to use the [tabris-cli](https://www.npmjs.com/package/tabris-cli). The tabris-cli requires a local [Tabris.js platform](https://tabrisjs.com/download) which should be referenced by a corresponding environment variable.

2. For Android place your `google-services.json` file in the root directory of the example project.

2. For iOS place your `GoogleService-Info.plist` file in the root directory of the example project and reference it as a `resource-file`:

```xml
<platform name="ios">
  <resource-file src="GoogleService-Info.plist" />
</platform>
```

## Running the Example

Using the tabris-cli the example can be started via

```sh
$ tabris run <platform>
```

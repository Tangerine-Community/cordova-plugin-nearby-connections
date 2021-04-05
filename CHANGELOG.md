# Changelog for cordova-plugin-nearby-connections

## v0.6.1
- Removed requestLegacyExternalStorage from plugin.xml; instead, add the following lines to your app's config.xml. This will reduce potential for cordova plugin merge conflicts.

```js
<edit-config file="app/src/main/AndroidManifest.xml" mode="merge" target="/manifest/application" xmlns:android="http://schemas.android.com/apk/res/android"> 
    <application android:requestLegacyExternalStorage="true" /> 
</edit-config>
```

## v0.6.0
- Added support for scoped storage in Android 10 using `requestLegacyExternalStorage="true"`. For Android 11, we will need to remove that switch and use the commented out implementation starting in lines 1210 of this plugin. Those lines are comented-out because the current version of com.google.android.gms:play-services-nearby (17.0.0) does not have the `payload.asFile().asUri();` api implemented.

## v0.5.0

- Changed API for startAdvertising to accept a name as an alternative to the random name assigned by the plugin for the device
- When setState is triggered more than once, it clears all endpoints and stops discovery, and restarts that discovery. Basically attempts to reset the plugin. 


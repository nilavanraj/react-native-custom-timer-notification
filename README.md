# react-native-custom-timer-notification

custom timer notification for react native 🔔

## Installation

```sh
npm install react-native-custom-timer-notification
```

      <!-- <service android:name="com.reactnativecustomtimernotification.ForegroundService"/> -->
AndroidManifest
```xml

      <receiver android:name="com.reactnativecustomtimernotification.NotificationEventReceiver" />
      <receiver android:name="com.reactnativecustomtimernotification.OnClickBroadcastReceiver" />
      <!--
      if foreground service used add this line
      -->
      <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
      <service android:name="com.reactnativecustomtimernotification.ForegroundService"/>
```
## Usage
```payload``` sent will be received when click or canceled
```title``` Title of the notification
```body``` Body of the notification
```id```  unique number
```sec``` Time in seconds

```js
import { multiply } from "react-native-custom-timer-notification";

// ...
// onclick and cancel listner
DeviceEventEmitter.addListener("notificationClick",event=>{
console.log(event)
})

const result = await TimerNotification({
      payload: JSON.stringify("notificationOpen?.data"), 
      title: "My notification",
      body:"Much longer text that cannot fit one line... ",
      id: 1,
      sec:60,
      remove:false, // optional 
      foreground:false,
     })
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

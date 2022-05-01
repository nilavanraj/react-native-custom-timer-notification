# react-native-custom-timer-notification

custom timer notification for react native 🔔
![ezgif com-gif-maker](https://user-images.githubusercontent.com/58332892/166133982-effe321c-a0fd-4315-bb29-cc7ee29d0bd4.gif)


## Installation

```sh
npm install react-native-custom-timer-notification
```

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
```payload``` sent will be received when click or canceled <br>
```title``` Title of the notification <br>
```body``` Body of the notification <br>
```id```  unique number <br>
```sec``` Time in seconds <br>

```js
import { TimerNotification } from "react-native-custom-timer-notification";

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

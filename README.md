# react-native-custom-timer-notification

Custom timer notification for React Native Android 🔔<br>
 **Now version 0.8 Supports full size custom notifications** <br>
<p align="center">
  <img  src="https://user-images.githubusercontent.com/58332892/208312749-58586dba-da62-4531-85bb-62346a57aa03.gif">
</p>
<p align="center">
  <img width="80%" src="https://user-images.githubusercontent.com/58332892/166133982-effe321c-a0fd-4315-bb29-cc7ee29d0bd4.gif">
</p>


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
### Timer notification

Only Timer notifiction with default Title and body
```js

import { TimerNotification } from "react-native-custom-timer-notification";
```

| Property | Description |
| --- | --- |
| `payload` | sent data will be received when click or canceled |
| `title` |Title of the notification|
| `body` |Body of the notification |
| `id` |unique number|
| `date` |Time at which zero comes|


#### Example

```js

  TimerNotification({
      payload: JSON.stringify('notification.data'),
      title: 'My notification',
      body: 'Much longer text that cannot fit one line... ',
      id: 1,
      remove: false, // optional
      foreground: false,
      date: new Date(Date.now() + 20000),
      isCountDown: true, // false for positive timer 
      setCustomContentView:true // optional
    });
    
```
<h3>Full custom notification </h3>

Full custom notifiction with custom image, text and cronometer.
```js

import { CustomNotification, TYPES, FB_TYPE } from "react-native-custom-timer-notification";
```

| Property | Description |
| --- | --- |
| `eventData` | sent data will be received when click or canceled |
| `title` |Title of the notification|
| `body` |Body of the notification |
| `id` |unique number|
| `View` |View that needs to be added (Array)|

<h3> View Properties </h3>

| Property | Description |
| --- | --- |
| `name` | text that needs to be displayed |
| `size` |Size of text|
| `type` |Type of view (Text,Image, Cronometer) |
| `bold` |Font (NORMAL,BOLD,ITALIC,BOLD_ITALIC)|
| `uri` |Image in base64|
| `PaddingLeft` |Left Padding|
| `PaddingTop` |PaddingTop|
| `PaddingRight` |PaddingRight|
| `PaddingBottom` |PaddingBottom|
| `color` |Text color|
| `ZeroTime` |Time at which zero comes|

#### Example

```js

    CustomNotification(
      {
        eventData: JSON.stringify('notificationOpen?.data'),
        title: 'notificationOpen.data.title',
        body: ' notificationOpen.data.body',
        id: 1,

        View: [
          {
            name: 'Limited Sales',
            size: 20,
            type: TYPES.Text,
            bold: FB_TYPE.BOLD_ITALIC,
            PaddingLeft: 10,
            PaddingTop: 50,
            PaddingRight: 0,
            PaddingBottom: 0,
            setViewVisibility: false,
            color: '#ed1a45',
          },
          {
            uri: image,
            type: TYPES.Image,
            PaddingLeft: 0,
            PaddingTop: 0,
            PaddingRight: 0,
            PaddingBottom: 0,
          },
          {
            name: 'Buy now',
            size: 30,
            type: TYPES.Text,
            bold: FB_TYPE.BOLD_ITALIC,
            PaddingLeft: 10,
            PaddingTop: 100,
            PaddingRight: 0,
            PaddingBottom: 0,
            setViewVisibility: false,
            color: '#fbd335',
          },
          {
            type: TYPES.Cronometer,
            size: 30,
            ZeroTime: new Date(Date.now() + 20000),
            PaddingLeft: 800,
            color: '#0000FF',
            PaddingTop: 0,
            PaddingRight: 0,
            PaddingBottom: 0,
          },
        ],
      },
      (e: any) => {
        console.log(e);
      }
    );
    
```
### Remove Notifications 
```js
import { RemoveTimer } from "react-native-custom-timer-notification";

RemoveTimer(1);

```
### onclick and cancel listner
```js
import { onEvent } from "react-native-custom-timer-notification";

onEvent(event=>{
console.log(event)
});

```
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

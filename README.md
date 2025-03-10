# React Native Custom Timer Notification 🔔

A powerful, flexible notification library for React Native Android that enables custom timer and GIF-based notifications with advanced customization options.

Now v0.9.1 supports Gif in Android 14+ 
<p align="center">
  <img width="50%" src="https://github.com/user-attachments/assets/5578e1aa-ac4c-458e-a661-c334e6bf8741">
</p>

##  Key Features

- **Customizable Timer Notifications**: Create dynamic, time-based notifications
- **Animated GIF Support**: Enhance notifications with animated graphics
- **Fully Customizable Notification Layouts**: Design unique notification experiences


##  Installation

```bash
npm install react-native-custom-timer-notification
```

## 🛠 Android Setup

### 1. Add Permissions to AndroidManifest.xml

Open `android/app/src/main/AndroidManifest.xml` and add:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### 2. Register Receivers and Services

Inside the `<application>` tag, add:

```xml
<receiver android:name="com.reactnativecustomtimernotification.NotificationEventReceiver" />
<receiver android:name="com.reactnativecustomtimernotification.OnClickBroadcastReceiver" />
<service android:name="com.reactnativecustomtimernotification.ForegroundService"/>
```

##  Usage Scenarios

### Basic Timer Notification

```javascript
import { TimerNotification } from "react-native-custom-timer-notification";

TimerNotification({
  id: 1,
  title: "Meeting Reminder",
  body: "Team meeting starts in:",
  date: "30-12-2024 15:00:00",
  payload: "meeting-123"
});
```
### Animated Notification with GIF


```javascript
TimerNotification({
 id: 2,
 title: <p style="color: #ff5722; font-size: 18px;"><b>🔥 Limited-Time Deal! Hurry Up! ⏳</b></p>,
 body: <p style="font-size: 14px;">⏳ Time is running out! <b>Claim your exclusive discount</b> before it's too late.</p>,
 subtitle: "💸",
 date: new Date(Date.now() + 20000), 
 giffyUrl: "https://media1.tenor.com/m/EBdqcf-JxpYAAAAC/6m-rain.gif",
 payload: "offer-456"
});
```
#### Options

| Parameter | Type     | Required | Description                     |
|-----------|----------|----------|---------------------------------|
| id        | number   | Yes      | Unique notification identifier  |
| title     | string   | Yes      | Notification title with HTML support |
| body      | string   | Yes      | Notification message with HTML support |
| date      | Date     | No       | End date with time (dd-MM-yyyy HH:mm:ss) |
| giffyUrl    | string   | No       | URL to GIF animation           |
| payload   | string   | No       | Custom data payload            |

##  Full Custom Notification

Create fully customized notifications with detailed configurations:
<p align="center">
  <img  src="https://user-images.githubusercontent.com/58332892/208312749-58586dba-da62-4531-85bb-62346a57aa03.gif">
</p>

```javascript
import { CustomNotification, TYPES, FB_TYPE } from "react-native-custom-timer-notification";

CustomNotification({
  eventData: JSON.stringify('notification_data'),
  title: 'Custom Notification',
  body: 'Detailed Notification',
  id: 1,
  View: [
    {
      name: 'Limited Sales',
      size: 20,
      type: TYPES.Text,
      bold: FB_TYPE.BOLD_ITALIC,
      color: '#ed1a45',
    },
    // Additional view configurations
  ]
});
```

#### Options
| Property | Description |
| --- | --- |
| `eventData` | sent data will be received when clicked or canceled |
| `title` |Title of the notification|
| `body` |Body of the notification |
| `id` |unique number|
| `View` |View that needs to be added (Array)|

#### View Options

| Property | Description |
| --- | --- |
| `name` | text that needs to be displayed |
| `size` |Size of text|
| `type` |Type of view (Text, Image, Cronometer) |
| `bold` |Font (NORMAL, BOLD,ITALIC, BOLD_ITALIC)|
| `uri` |Image in base64|
| `PaddingLeft` |Left Padding|
| `PaddingTop` |PaddingTop|
| `PaddingRight` |PaddingRight|
| `PaddingBottom` |PaddingBottom|
| `color` |Text color|
| `ZeroTime` |Time at which zero comes|

### Event Handling

```javascript
import { onEvent } from "react-native-custom-timer-notification";

// Listen for notification interactions (press/cancel)
onEvent(event => {
  const { action, payload } = event;
  
  switch(action) {
    case 'press':
      // Handle notification press/click
      console.log('Notification pressed:', payload);
      break;
    case 'cancel':
      // Handle notification dismissal
      console.log('Notification cancelled:', payload);
      break;
  }
});
```
### Remove Notifications

```javascript
import { RemoveTimer } from "react-native-custom-timer-notification";

// Remove a specific notification by ID
RemoveTimer(1);
```

## ⭐️ Support the Project

If you find this library helpful, please consider:
- Giving it a GitHub star ⭐️
- Creating issues for bug reports and feature requests
- Contributing with pull requests

## 📄 License

MIT Licensed. See LICENSE file for details.

## 🔍 Keywords

react-native, notifications, timer-notifications, gif-notifications, android-notifications, custom-notifications, countdown-timer, animated-notifications, react-native-notifications, mobile-notifications, push-notifications, notification-system, react-native-android, notification-timer, countdown-notifications, custom-notification-layout



# React Native Custom Timer Notification 🔔

A powerful, flexible notification library for React Native Android that enables custom timer-based notifications with advanced customization options.

## 🚀 Key Features

- **Customizable Timer Notifications**: Create dynamic, time-based notifications
- **Animated GIF Support**: Enhance notifications with animated graphics
- **Fully Customizable Notification Layouts**: Design unique notification experiences


## 📦 Installation

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

## 🎯 Usage Scenarios

### Basic Timer Notification

<p align="center">
  <img width="80%" src="https://user-images.githubusercontent.com/58332892/166133982-effe321c-a0fd-4315-bb29-cc7ee29d0bd4.gif">
</p>

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

<p align="center">
  <img width="50%" src="https://github.com/user-attachments/assets/cea88228-602a-42df-8326-569fd0a03cf1">
</p>

```javascript
CustomTimerNotification.TimerNotification({
  id: 2,
  title: "Special Offer!",
  body: "Limited time offer ends in:",
  date: "25-12-2024 23:59:59",
  gifUrl: "https://example.com/animation.gif",
  payload: "offer-456"
});
```
#### Options

| Parameter | Type     | Required | Description                     |
|-----------|----------|----------|---------------------------------|
| id        | number   | Yes      | Unique notification identifier  |
| title     | string   | Yes      | Notification title             |
| body      | string   | Yes      | Notification message           |
| date      | string   | Yes      | End date (dd-MM-yyyy HH:mm:ss) |
| gifUrl    | string   | No       | URL to GIF animation           |
| payload   | string   | No       | Custom data payload            |

## 🎨 Full Custom Notification

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
#### ViewOptions

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


## 🤝 Contributing

See the contributing guide to learn how to contribute to the repository and the development workflow.


## License

MIT



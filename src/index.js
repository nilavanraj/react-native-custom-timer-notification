import { NativeModules, Platform, DeviceEventEmitter } from 'react-native';
const parseDate = (rawDate) => {
  let hours;
  let day;
  let month;

  if (rawDate.getHours().toString().length === 1) {
    hours = `0${rawDate.getHours()}`;
  } else {
    hours = `${rawDate.getHours()}`;
  }

  if (rawDate.getDate().toString().length === 1) {
    day = `0${rawDate.getDate()}`;
  } else {
    day = `${rawDate.getDate()}`;
  }

  if (rawDate.getMonth().toString().length === 1) {
    month = `0${rawDate.getMonth() + 1}`;
  } else {
    month = `${rawDate.getMonth() + 1}`;
  }

  return `${day}-${month}-${rawDate.getFullYear()} ${hours}:${rawDate.getMinutes()}:${rawDate.getSeconds()}`;
};
const LINKING_ERROR =
  `The package 'react-native-custom-timer-notification' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const CustomTimerNotification = NativeModules.CustomTimerNotification
  ? NativeModules.CustomTimerNotification
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const CustomNotificationModule = NativeModules.CustomNotificationModule
  ? NativeModules.CustomNotificationModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
export function TimerNotification(a) {
  const data = a;
  data.date = parseDate(data.date);

  if (Platform.OS === 'android')
    return CustomTimerNotification.TimerNotification(data);
  return null;
}
export function RemoveTimer(a, b = false) {
  const payload = {
    id: a,
    foreground: b || false,
  };

  CustomTimerNotification.RemoveTimer(payload);
}

export function CustomNotification(a, cb) {
  const data = a;

  data.View = data.View.map((item) => {
    if (item.type == 3)
      return {
        ...item,
        ZeroTime: parseDate(item.ZeroTime),
      };

    return {
      ...item,
    };
  });

  if (Platform.OS === 'android')
    return CustomNotificationModule.CustomNotification(data, cb);
  return null;
}

export function onEvent(listener) {
  DeviceEventEmitter.addListener('notificationClick', (event) =>
    listener(event)
  );
}

export const TYPES = {
  Image: 1,
  Text: 2,
  Cronometer: 3,
};

export const FB_TYPE = {
  NORMAL: 0,
  BOLD: 1,
  ITALIC: 2,
  BOLD_ITALIC: 3,
};

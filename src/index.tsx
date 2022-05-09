import { NativeModules, Platform, DeviceEventEmitter } from 'react-native';

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

export function TimerNotification(a: object): any {
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
  const data = a;
  data.date = parseDate(data.date)
  
  if (Platform.OS === 'android')
    return CustomTimerNotification.TimerNotification(data);
  return null;
}

export function onEvent(listener: Function): void {
  DeviceEventEmitter.addListener('notificationClick', (event) =>
    listener(event)
  );
}

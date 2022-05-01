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
  if (Platform.OS === 'android')
    return CustomTimerNotification.TimerNotification(a);
  return null;
}

export function onEvent(listener: Function): void {
  DeviceEventEmitter.addListener('notificationClick', (event) =>
    listener(event)
  );
}

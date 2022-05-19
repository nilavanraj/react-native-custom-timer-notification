import * as React from 'react';

import {
  TimerNotification,
  RemoveTimer,
  onEvent,
} from 'react-native-custom-timer-notification';
onEvent((event: any) => {
  console.log(event);
});
export default function App() {
  React.useEffect(() => {
    TimerNotification({
      payload: JSON.stringify('notificationOpen?.data'),
      title: 'My notification',
      body: 'Much longer text that cannot fit one line... ',
      id: 160211114,
      remove: false, // optional
      foreground: true,
      date: new Date(Date.now()),
      isCountDown: false,
    });
    setTimeout(() => {
      RemoveTimer(160211114, true);
    }, 5000);
  }, []);

  return <></>;
}

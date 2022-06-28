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
      foreground: false,
      date: new Date(Date.now()+20000),
      isCountDown: true,
      setCustomContentView:true // optional
    });

  }, []);

  return <></>;
}

import * as React from 'react';
import { Button } from 'react-native';
import {
  TimerNotification,
  RemoveTimer,
  onEvent,
  CustomNotification,
  TYPES,
  FB_TYPE,
} from 'react-native-custom-timer-notification';
import { image } from './image';

onEvent((event: any) => {
  console.log(event);
});

export default function App() {
  const CN = React.useCallback(() => {
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
  }, []);

  const TN = React.useCallback(() => {
    TimerNotification({
      payload: JSON.stringify('notificationOpen?.data'),
      title: 'My notification',
      body: 'Much longer text that cannot fit one line... ',
      id: 160211114,
      remove: false, // optional
      foreground: false,
      date: new Date(Date.now() + 20000),
      isCountDown: true, // false for positive timer
      setCustomContentView: true, // optional
    });
  }, []);

  return (
    <>
      <Button title="Custom Notification" onPress={CN} />
      <Button title="Timer Notification" onPress={TN} />
      <Button
        title="remove"
        onPress={() => {
          RemoveTimer(1);
        }}
      />
    </>
  );
}

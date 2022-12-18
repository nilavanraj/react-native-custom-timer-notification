import * as React from 'react';

import {
  TimerNotification,
  RemoveTimer,
  onEvent,
  CustomNotification,
} from 'react-native-custom-timer-notification';
import { Dimensions } from 'react-native';
import { image, image2 } from './image';
const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

onEvent((event: any) => {
  console.log(event);
});

export default function App() {
  React.useEffect(() => {
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
            type: 2,
            bold: 3,
            PaddingLeft: 10,
            PaddingTop: 50,
            PaddingRight: 0,
            PaddingBottom: 0,
            setViewVisibility: false,
            color: '#ed1a45',
          },
          {
            size: 50,
            uri: image,
            type: 1,
            PaddingLeft: 0,
            PaddingTop: 0,
            PaddingRight: 0,
            PaddingBottom: 0,
          },
          {
            name: 'Buy now',
            size: 30,
            type: 2,
            bold: 3,
            PaddingLeft: 10,
            PaddingTop: 100,
            PaddingRight: 0,
            PaddingBottom: 0,
            setViewVisibility: false,
            color: '#fbd335',
          },
          {
            type: 3,
            ZeroTime: new Date(Date.now() + 20000),
            PaddingLeft: 800,
            hide: true,
            size: 25,
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

  return <></>;
}

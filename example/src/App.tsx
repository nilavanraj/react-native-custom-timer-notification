import * as React from 'react';

import {
  TimerNotification,
  RemoveTimer,
  onEvent,
  multiply,
} from 'react-native-custom-timer-notification';
import { image } from './image';
const image1 = `iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=`
onEvent((event: any) => {
  console.log(event);
});
export default function App() {
  React.useEffect(() => {
    multiply(
      {
        teamImg1:
          'https://s3.ap-south-1.amazonaws.com/leaguex/team-images/bblw/MLSW.png',
        teamImg2:
          'https://s3.ap-south-1.amazonaws.com/leaguex/team-images/bblw/MLSW.png',
        eventData: JSON.stringify('notificationOpen?.data'),
        title: 'notificationOpen.data.title',
        body: ' notificationOpen.data.body',
        id: 160211114,
        TextView: [
          {
            name: 'nilavan1',
            size: 35,
            PaddingLeft: 0,
            PaddingTop: 0,
            PaddingRight: 0,
            PaddingBottom: 100,
            setViewVisibility: false,
          },
          {
            name: 'nilavan1',
            size: 25,
            PaddingLeft: 0,
            PaddingTop: 10,
            PaddingRight: 0,
            PaddingBottom: 100,
            setViewVisibility: false,
          },
        ],
        ImageView: [
          {
            size: 50,
            url:"iVBORw0KGgoAAAANSUhEUgAAAAgAAAAIAQMAAAD+wSzIAAAABlBMVEX///+/v7+jQ3Y5AAAADklEQVQI12P4AIX8EAgALgAD/aNpbtEAAAAASUVORK5CYII",
            PaddingLeft: -100,
            PaddingTop: 0,
            PaddingRight: 0,
            PaddingBottom: 0,
          },
          {
            size: 50,
            url: image1,
            PaddingLeft: 200,
            PaddingTop: 0,
            PaddingRight: 0,
            PaddingBottom: 0,
          }
          
        ],
        toFixed: function (fractionDigits?: number | undefined): string {
          throw new Error('Function not implemented.');
        },
        toExponential: function (fractionDigits?: number | undefined): string {
          throw new Error('Function not implemented.');
        },
        toPrecision: function (precision?: number | undefined): string {
          throw new Error('Function not implemented.');
        },
      },
      (e: any) => {
        console.log(e);
      }
    );
    // TimerNotification({
    //   payload: JSON.stringify('notificationOpen?.data'),
    //   title: 'My notification',
    //   body: 'Much longer text that cannot fit one line... ',
    //   id: 160211114,
    //   remove: false, // optional
    //   foreground: false,
    //   date: new Date(Date.now() + 20000),
    //   isCountDown: true,
    //   setCustomContentView: true, // optional
    // });
  }, []);

  return <></>;
}

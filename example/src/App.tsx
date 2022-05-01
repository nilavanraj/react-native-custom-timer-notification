import * as React from 'react';

import { StyleSheet, View, Text,DeviceEventEmitter } from 'react-native';
import { TimerNotification,onEvent } from 'react-native-custom-timer-notification';
onEvent(event=>{
console.log(event)
});
export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    TimerNotification({
      payload: JSON.stringify("notificationOpen?.data"),
      title: "My notification",
      body:"Much longer text that cannot fit one line... ",
      id: 160211114,
      sec:1800,
      remove:false, // optional 
      foreground:false,

     })
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});

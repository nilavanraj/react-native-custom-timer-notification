# react-native-custom-timer-notification

custom timer notification

## Installation

```sh
npm install react-native-custom-timer-notification
```

## Usage

```js
import { multiply } from "react-native-custom-timer-notification";

// ...

const result = await TimerNotification({
      eventData: JSON.stringify("notificationOpen?.data"),
      title: "My notification",
      body:"Much longer text that cannot fit one line... ",
      id: 1,
      sec:60,
      remove:false, // optional 
      foreground:false,
     })
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

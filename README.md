# ⚡️ @pagopa/io-react-native-cie-pid

![CI workflow](https://github.com/pagopa/io-react-native-cie-pid/actions/workflows/ci.yml/badge.svg)

A RN library for pid issuing based on native IPZS Android SDK (iOS will be available soon)

## Installation

```sh
npm install @pagopa/io-react-native-cie-pid
```

## Usage

```js
import CieManager from '@pagopa/io-react-native-cie-pid';

// ...

const startCieManager = async () => {
  try {
    await CieManager.start();
    await CieManager.onEvent(handleCieEvent);
    await CieManager.onError(handleCieError);
    await CieManager.onSuccess(handleCieSuccess);
  } catch (error) {
    console.error(error);
  }
};

const stopCieManager = async () => {
  try {
    await CieManager.stopListeningNFC();
    await CieManager.removeAllListeners();
    setCieStarted(false);
  } catch (error) {
    console.error(error);
  }
};
```

## Example

You can use the sample app to test and understand how to use the library.

```bash
cd example

yarn install

cd ios && bundler exec pod install && cd ..

# To use iOS
yarn ios

# To use Android
yarn android
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)

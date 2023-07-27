import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package '@pagopa/io-react-native-cie-pid' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const IoReactNativeCiePid = NativeModules.IoReactNativeCiePid
  ? NativeModules.IoReactNativeCiePid
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const isIosDeviceCompatible =
  IoReactNativeCiePid !== null &&
  Platform.OS === 'ios' &&
  parseInt(Platform.Version, 10) >= 13;

// On Android, the events are sent by sending an intent from the native code to
// the JS code. The JS code registers a broadcast receiver that will receive
// the intent and emit the event.
//
// On iOS, the events are sent by sending a notification from the native code to
// the JS code. The JS code registers a listener for the notification and emits
// the event.
//

type CIEEvent =
  | 'ON_TAG_DISCOVERED_NOT_CIE'
  | 'ON_TAG_DISCOVERED'
  | 'ON_TAG_LOST'
  | 'ON_CARD_PIN_LOCKED'
  | 'ON_PIN_ERROR'
  | 'PIN_INPUT_ERROR'
  | 'CERTIFICATE_EXPIRED'
  | 'CERTIFICATE_REVOKED'
  | 'AUTHENTICATION_ERROR'
  | 'ON_NO_INTERNET_CONNECTION'
  | 'STOP_NFC_ERROR'
  | 'START_NFC_ERROR'
  | 'EXTENDED_APDU_NOT_SUPPORTED'
  // iOS dedicated events
  | 'PIN Locked'
  | 'TAG_ERROR_NFC_NOT_SUPPORTED'
  | 'Transmission Error';

export type PidData = {
  name: string;
  surname: string;
  fiscalCode: string;
  birthDate: string;
};

export type CieData = {
  url: string;
  pidData: PidData;
};

export type Event = {
  event: CIEEvent;
  attemptsLeft: number;
};

type EventHandler = (event: Event) => void;

const CieManager = () => {
  const _eventSuccessHandlers: EventHandler[] = [];
  const _eventErrorHandlers: EventHandler[] = [];
  const _eventHandlers: EventHandler[] = [];

  /**
   * private
   */
  const _registerEventEmitter = () => {
    if (Platform.OS === 'ios' && !isIosDeviceCompatible) {
      return;
    }
    const NativeCieEmitter = new NativeEventEmitter(IoReactNativeCiePid);
    NativeCieEmitter.addListener('onEvent', (e) => {
      _eventHandlers.forEach((h) => h(e));
    });
    NativeCieEmitter.addListener('onSuccess', (e) => {
      _eventSuccessHandlers.forEach((h) => h(e.event));
    });
    NativeCieEmitter.addListener('onError', (e) => {
      _eventErrorHandlers.forEach((h) => h(e.event));
    });
  };

  _registerEventEmitter();

  const onEvent = (listener: any) => {
    if (!_eventHandlers.includes(listener)) {
      _eventHandlers.push(listener);
    }
  };

  const onError = (listener: any) => {
    if (!_eventErrorHandlers.includes(listener)) {
      _eventErrorHandlers.push(listener);
    }
  };

  const onSuccess = (listener: any) => {
    if (!_eventSuccessHandlers.includes(listener)) {
      _eventSuccessHandlers.push(listener);
    }
  };

  const removeAllListeners = () => {
    _eventSuccessHandlers.length = 0;
    _eventErrorHandlers.length = 0;
    _eventHandlers.length = 0;
  };

  /**
   * set the CIE pin. If the format doesn't respect a 8 length string of digits
   * the promise will be rejected
   */
  const setPin = (pin: string) => {
    if (Platform.OS === 'ios') {
      return new Promise<void>((resolve, reject) => {
        if (!isIosDeviceCompatible) {
          reject('this device is not compatible');
          return;
        }
        IoReactNativeCiePid.setPin(pin);
        resolve();
      });
    }
    return new Promise<void>((resolve, reject) => {
      IoReactNativeCiePid.setPin(pin, (err: any) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  };

  const setAlertMessage = (key: any, value: any) => {
    if (isIosDeviceCompatible) {
      IoReactNativeCiePid.setAlertMessage(key, value);
    }
  };

  const setAuthenticationUrl = (url: string) => {
    if (Platform.OS === 'ios') {
      if (!isIosDeviceCompatible) {
        return;
      }
      IoReactNativeCiePid.setAuthenticationUrl(url);
      return;
    }
    IoReactNativeCiePid.setAuthenticationUrl(url);
  };

  const start = (config?: any) => {
    if (Platform.OS === 'ios') {
      if (!isIosDeviceCompatible) {
        return Promise.reject('not compatible');
      }
      if (config !== undefined) {
        Object.entries(config).forEach((kv) =>
          IoReactNativeCiePid.setAlertMessage(kv[0], kv[1])
        );
      }
    }
    return new Promise<void>((resolve, reject) => {
      IoReactNativeCiePid.start((err: any) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  };

  const startListeningNFC = () => {
    if (Platform.OS === 'ios') {
      if (isIosDeviceCompatible) {
        return Promise.resolve();
      }
      return Promise.reject('not implemented');
    }
    return new Promise<void>((resolve, reject) => {
      IoReactNativeCiePid.startListeningNFC((err: any) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  };

  const stopListeningNFC = () => {
    if (Platform.OS === 'ios') {
      // do nothing
      return Promise.resolve();
    }
    return new Promise<void>((resolve, reject) => {
      IoReactNativeCiePid.stopListeningNFC((err: any) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  };

  const isCIEAuthenticationSupported = async () => {
    try {
      const nfcFeature = await hasNFCFeature();
      const apiLevelSupport = await hasApiLevelSupport();
      return Promise.resolve<boolean>(nfcFeature && apiLevelSupport);
    } catch {
      return Promise.resolve(false);
    }
  };

  /**
   * Return true if the nfc is enabled, on the device in Settings screen
   * is possible enable or disable it.
   */
  const isNFCEnabled = () => {
    return new Promise<boolean>((resolve) => {
      IoReactNativeCiePid.isNFCEnabled((result: boolean) => {
        resolve(result);
      });
    });
  };

  /**
    return a Promise will be resolved with true if the current OS supports the authentication.
    This method is due because with API level < 23 a security exception is raised
    read more here - https://github.com/teamdigitale/io-cie-android-sdk/issues/10
   */
  const hasApiLevelSupport = () => {
    if (Platform.OS === 'ios') {
      return Promise.resolve<boolean>(isIosDeviceCompatible);
    }
    return new Promise<boolean>((resolve) => {
      IoReactNativeCiePid.hasApiLevelSupport((result: boolean) => {
        resolve(result);
      });
    });
  };

  /**
   * Check if the hardware module nfc is installed (only for Android devices)
   */
  const hasNFCFeature = () => {
    return new Promise<boolean>((resolve) => {
      IoReactNativeCiePid.hasNFCFeature((result: boolean) => {
        resolve(result);
      });
    });
  };

  /**
   * It opens OS Settings on NFC section
   *
   */
  const openNFCSettings = () => {
    if (Platform.OS === 'ios') {
      return Promise.reject('not implemented');
    }
    return new Promise<void>((resolve, reject) => {
      IoReactNativeCiePid.openNFCSettings((err: any) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  };

  const launchCieID = () => {
    if (Platform.OS === 'ios') {
      return Promise.reject('not implemented');
    }
    return new Promise<void>((resolve, reject) => {
      IoReactNativeCiePid.launchCieID((err: any) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  };

  return {
    onEvent,
    onError,
    onSuccess,
    removeAllListeners,
    setPin,
    setAlertMessage,
    setAuthenticationUrl,
    start,
    startListeningNFC,
    stopListeningNFC,
    isCIEAuthenticationSupported,
    isNFCEnabled,
    hasApiLevelSupport,
    hasNFCFeature,
    openNFCSettings,
    launchCieID,
  };
};

export default CieManager();

import * as React from 'react';
import CieManager from 'io-react-native-cie-pid';
import {
  Button,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
} from 'react-native';

const styles = StyleSheet.create({
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
  },
});

export default function App() {
  const [ciePin, onChangeCiePin] = React.useState('');
  const [info, setInfo] = React.useState('');

  const cieAuthorizationUri =
    'https://idserver.servizicie.interno.gov.it/idp/protocol/openid-connect/auth';

  React.useEffect(() => {
    CieManager.start()
      .then(async () => {
        CieManager.onEvent(handleCieEvent);
        CieManager.onError(handleCieError);
        CieManager.onSuccess(handleCieSuccess);
        setInfo('CieManager started');
      })
      .catch(() => {
        setInfo('CieManager not started');
      });
  }, []);

  const handleCieEvent = async (event: any) => {
    console.log(event);
  };

  const handleCieError = async (event: any) => {
    console.log(event);
  };

  const handleCieSuccess = async (event: any) => {
    console.log(event);
  };

  const handleCieAuthentication = async () => {
    try {
      await CieManager.setPin(ciePin);
      CieManager.setAuthenticationUrl(cieAuthorizationUri);
      await CieManager.startListeningNFC();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <SafeAreaView>
      <TextInput
        style={styles.input}
        onChangeText={onChangeCiePin}
        value={ciePin}
        placeholder="insert cie pin"
        keyboardType="numeric"
      />
      <Button title="continue" onPress={handleCieAuthentication} />
      <Text>{info}</Text>
    </SafeAreaView>
  );
}

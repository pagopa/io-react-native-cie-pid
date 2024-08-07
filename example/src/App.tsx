import * as React from 'react';
import CieManager, { type CieData } from '@pagopa/io-react-native-cie-pid';
import {
  ActivityIndicator,
  Button,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

const styles = StyleSheet.create({
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
  },
  content: {
    justifyContent: 'center',
    padding: 20,
    height: '100%',
  },
  padding: {
    padding: 10,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  horizontal: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 10,
  },
});

export default function App() {
  const [cieStarted, setCieStarted] = React.useState(false);
  const [ciePin, onChangeCiePin] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(false);
  const [cieData, setCieData] = React.useState<CieData>();

  const handleCieEvent = async (event: any) => {
    console.log('=== ON CIE EVENT ===');
    console.log(event);
  };

  const handleCieError = async (event: any) => {
    console.log('=== ON CIE ERROR ===');
    console.log(event);
  };

  const handleCieSuccess = async (event: any) => {
    console.log('=== ON CIE SUCCESS ===');
    try {
      const cieDataParsed: CieData = JSON.parse(event);
      setCieData(cieDataParsed);
      setIsLoading(false);
    } catch {
      console.log("can't parse cie data");
    }
    CieManager.stopListeningNFC();
    CieManager.removeAllListeners();
  };

  const handleCieAuthentication = async () => {
    try {
      await CieManager.setPin(ciePin);
      await CieManager.startListeningNFC();
      await CieManager.start();
    } catch (error) {
      console.error(error);
    }
  };

  const startCieManager = async () => {
    try {
      await CieManager.onEvent(handleCieEvent);
      await CieManager.onError(handleCieError);
      await CieManager.onSuccess(handleCieSuccess);
      setCieStarted(true);
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

  const renderHome = () => {
    return (
      <View style={styles.content}>
        <Button title="Start CIE authentication" onPress={startCieManager} />
      </View>
    );
  };

  const renderCiePinScreen = () => {
    return (
      <View style={styles.content}>
        {isLoading ? (
          renderLoadingIndicator()
        ) : (
          <>
            <TextInput
              style={styles.input}
              onChangeText={onChangeCiePin}
              value={ciePin}
              placeholder="insert cie pin"
              keyboardType="numeric"
            />
            <View style={styles.padding} />
            <Button title="Continue" onPress={handleCieAuthentication} />
            <View style={styles.padding} />
            <Button title="Stop CIE Manager" onPress={stopCieManager} />
            <View style={styles.padding} />
            {cieData?.pidData && (
              <>
                <Text>{`Name: ${cieData?.pidData.name}`}</Text>
                <Text>{`Surname: ${cieData?.pidData.surname}`}</Text>
                <Text>{`Fiscalcode: ${cieData?.pidData.fiscalCode}`}</Text>
                <Text>{`Birthdate: ${cieData?.pidData.birthDate}`}</Text>
              </>
            )}
          </>
        )}
      </View>
    );
  };

  const renderLoadingIndicator = () => {
    return (
      <View style={[styles.content]}>
        <ActivityIndicator size="large" />
        <View style={styles.padding} />
        <Text style={styles.padding}>
          {
            'Place your ID card on the back of your smartphone and hold it still until completed'
          }
        </Text>
      </View>
    );
  };

  return (
    <SafeAreaView>
      {!cieStarted ? renderHome() : renderCiePinScreen()}
    </SafeAreaView>
  );
}

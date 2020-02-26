import React from 'react';
import {StyleSheet, View} from 'react-native';
import Header from './components/Header';
import NotificationHandler from './components/NotificationHandler';
import firebase from 'react-native-firebase';

const App = () => {
  const analytics = firebase.analytics();
  analytics.setAnalyticsCollectionEnabled(true);
  analytics.logEvent('Init', {isAlive: true});
  firebase
    .messaging()
    .getToken()
    .then(fcmToken => {
      if (fcmToken) {
        console.log('FCM Token: ', fcmToken);
      } else {
        console.log("Couldn't get the FCM token");
      }
    });
  return (
    <View style={styles.screen}>
      <Header title="Firebase Notification Demo" />
      <NotificationHandler/>
    </View>
  );
};

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
});

export default App;

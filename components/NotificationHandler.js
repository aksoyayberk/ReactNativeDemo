import React, {Component} from 'react';
import {StyleSheet, View, Text} from 'react-native';
import firebase from 'react-native-firebase';

class NotificationHandler extends Component {
  componentDidMount() {
    this.removeNotificationListener = firebase
      .notifications()
      .onNotification(notification => {
        console.log(notification);
        this.renderNotificationMessage(notification);
      });

    // Build a channel
    const channel = new firebase.notifications.Android.Channel(
      'test-channel',
      'Test Channel',
      firebase.notifications.Android.Importance.Max,
    ).setDescription('My apps test channel');

    // Create the channel
    firebase.notifications().android.createChannel(channel);

    firebase.messaging().subscribeToTopic("testTopic");
  }

  componentWillUnmount() {
    this.removeNotificationListener();
  }

  renderNotificationMessage = nObj => {
    const notification = new firebase.notifications.Notification()
      .setNotificationId(nObj._notificationId)
      .setTitle(nObj._title)
      .setBody(nObj._body)
      .setData({
        ...nObj._data,
      });
    notification
      .android.setChannelId('test-channel')
      .android.setSmallIcon('ic_launcher');
    firebase.notifications().displayNotification(notification);
  };
  render() {
    firebase
      .messaging()
      .hasPermission()
      .then(enabled => {
        if (enabled) {
        } else {
          try {
            firebase
              .messaging()
              .requestPermission()
              .then(() => console.log('Permission Granted'))
              .catch(error => console.log('Permission Error: ', error));
          } catch (error) {
            console.log('Permission Rejected: ', error);
          }
        }
      });
    return (
      <View>
        <Text>NotificationHandler</Text>
      </View>
    );
  }
}

export default NotificationHandler;

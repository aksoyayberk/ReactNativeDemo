import React from 'react'
import { View, Text, StyleSheet, Button} from 'react-native'

import RollMessagingServiceModule from './RollMessagingServiceModule'

const Header = props => {
    RollMessagingServiceModule.getCurrentFCMToken((token) => console.log(token))
    let configs = {
        "importance": RollMessagingServiceModule.IMPORTANCE_HIGH,
        "priority": RollMessagingServiceModule.PRIORITY_HIGH
    }
    return (
        <View style={styles.header}>
            <Text style={styles.headerTitle}>{props.title}</Text>
            <Button
          title="Press me"
          onPress={() => RollMessagingServiceModule.sendNotification("Title", "Text", configs)}
        />
        </View>
    )
}

const styles = StyleSheet.create({
    header: {
        width: '100%',
        height: 60, 
        paddingTop: 36,
        backgroundColor: '#f7287b',
        alignItems: 'center',
        justifyContent: 'center'
    },
    headerTitle: {
        color: 'black',
        fontSize: 18
    }
})

export default Header
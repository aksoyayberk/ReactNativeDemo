package com.demoapp.rollmessagingservice;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.demoapp.MainActivity;
import com.demoapp.R;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Module to be exported to React Native with all of the required
 * functions for the notification service
 */
public class RollMessagingServiceModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    // RMS stands for RollMessagingService
    private static final String TAG = "RMSModule";

    RollMessagingServiceModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    /**
     * Returns the string name of the NativeModule which represents this class in JavaScript
     *
     * @return String module_name (e.g. "RollMessagingServiceModule")
     */
    @Override
    public String getName() {
        return "RollMessagingServiceModule";
    }

    /**
     * Exposes the current Firebase Cloud Messaging (FCM) token to JavaScript via
     * the Callback function
     *
     * @param tokenCallback
     */
    @ReactMethod
    public void getCurrentFCMToken(Callback tokenCallback) {
        Log.d(TAG, "Fetching current FCM token...");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and callback
                        String msg = getReactApplicationContext().getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        tokenCallback.invoke(token);
                    }
                });
    }

    /**
     * Exposed function to subscribe the application to the provided topic
     *
     * @param topic
     */
    @ReactMethod
    public void subscribeToTopic(String topic) {
        Log.d(TAG, "Subscribing to " + topic + " topic");
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to " + topic;
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    /**
     * Exposed function to unsubscribe the application from the provided topic.
     * This does not stop FirebaseInstanceId's periodic sending of data started
     * by subscribeToTopic(String). To stop this, see deleteInstanceId()
     *
     * @param topic
     */
    @ReactMethod
    public void unsubscribeFromTopic(String topic) {
        Log.d(TAG, "Unsubscribing from " + topic + " topic");
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Unsubscribed from " + topic;
                        if (!task.isSuccessful()) {
                            msg = "Failed to unsubscribe";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    /**
     * Exposed function to check if the application requires user permission
     * to receive background notifications
     *
     * @param flagCallback
     */
    @ReactMethod
    public void isBackgroundRestricted(Callback flagCallback) {
        ActivityManager activityManager = (ActivityManager) getReactApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        boolean flag = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            flag = activityManager.isBackgroundRestricted();
        } else {
            Log.w(TAG, "Cannot check background restriction due to lower API " +
                    "version (API version 28 or higher is required!");
        }
        flagCallback.invoke(flag);
    }

    /**
     * Exposed function to display a pre-built RollNotification
     *
     * @param configs Custom attributes to be set in RollNotification class in order to
     *                create custom notifications. An example of configs object on JS side
     *                is as follows:
     *                let configs = {
     *                      importance: RollMessagingServiceModule.IMPORTANCE_HIGH,
     *                      priority: RollMessagingServiceModule.PRIORITY_HIGH,
     *                      autoCancel: true,
     *                      intent: [RollMessagingServiceModule.FLAG_ACTIVITY_NEW_TASK,
     *                              RollMessagingServiceModule.FLAG_ACTIVITY_CLEAR_TASK]
     *                }
     */
    @ReactMethod
    public void sendNotification(String contentTitle, String contentText, ReadableMap configs) {
        Log.d(TAG, "Notification is being built...");

        RollNotification.RollNotificationBuilder rollNotificationBuilder =
                new RollNotification.RollNotificationBuilder(contentTitle, contentText);

        Intent intent = new Intent(getReactApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // INTENT REQUEST CODE SHOULD BE TAKEN FROM THE USER IN configs OBJECT
        PendingIntent pendingIntent = PendingIntent.getActivity(getReactApplicationContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        rollNotificationBuilder.setPendingIntent(pendingIntent);

        // ---------------------------------------------------------
        // CHANNEL_ID has to be set programmatically but in a meaningful manner!!
        rollNotificationBuilder.setChannelId("TEST_NOTIFICATION_CHANNEL_LOCAL");
        // ---------------------------------------------------------

        Iterator<Map.Entry<String, Object>> iterator = configs.getEntryIterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> attribute = iterator.next();
            String key = attribute.getKey();
            Object value = attribute.getValue();

            // TODO(developer): Rest of the cases must be added to handle other properties of
            //                  the rollNotification (e.g. Intent)
            switch (key) {
                case "smallIcon":
                    rollNotificationBuilder.setSmallIcon((int) value);
                    break;
                case "priority":
                    rollNotificationBuilder.setPriority((int)((double)value));
                    break;
                case "importance":
                    rollNotificationBuilder.setImportance((int)((double)value));
                    break;
                case "defaults":
                    rollNotificationBuilder.setDefaults((int) value);
                    break;
                case "autoCancel":
                    rollNotificationBuilder.setAutoCancel((boolean) value);
                    break;
                case "channelId":
                    rollNotificationBuilder.setChannelId((String) value);
                    break;
                case "notificationId":
                    rollNotificationBuilder.setNotificationId((int) value);
                    break;
                default:
                    break;
            }
        }
        RollNotification rollNotification = rollNotificationBuilder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getReactApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(rollNotification.getNotificationId(), rollNotification.build(getReactApplicationContext()));
        Log.d(TAG, "Notification sent!");
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("FLAG_ACTIVITY_NEW_TASK", Intent.FLAG_ACTIVITY_NEW_TASK);
        constants.put("FLAG_ACTIVITY_CLEAR_TASK", Intent.FLAG_ACTIVITY_CLEAR_TASK);
        constants.put("FLAG_ACTIVITY_CLEAR_TOP", Intent.FLAG_ACTIVITY_CLEAR_TOP);
        constants.put("FLAG_ONE_SHOT", PendingIntent.FLAG_ONE_SHOT);
        constants.put("PRIORITY_DEFAULT", NotificationCompat.PRIORITY_DEFAULT);
        constants.put("IMPORTANCE_DEFAULT", NotificationManager.IMPORTANCE_DEFAULT);
        constants.put("PRIORITY_HIGH", NotificationCompat.PRIORITY_HIGH);
        constants.put("IMPORTANCE_HIGH", NotificationManager.IMPORTANCE_HIGH);
        return constants;
    }
}
    
package com.demoapp.rollmessagingservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.demoapp.MainActivity;
import com.demoapp.R;

public class RollNotification {
    private final String contentTitle;
    private final String contentText;
    private final int smallIcon;
    private final int priority;
    private final int importance;
    private final Intent intent;
    private final PendingIntent pendingIntent;
    private final int defaults;
    private final boolean autoCancel;
    private final int argb;
    private final int onMs;
    private final int offMs;
    private final String channelId;
    private final int notificationId;

    private RollNotification(RollNotificationBuilder builder) {
        this.contentTitle = builder.contentTitle;
        this.contentText = builder.contentText;
        this.smallIcon = builder.smallIcon;
        this.priority = builder.priority;
        this.importance = builder.importance;
        this.intent = builder.intent;
        this.pendingIntent = builder.pendingIntent;
        this.defaults = builder.defaults;
        this.autoCancel = builder.autoCancel;
        this.argb = builder.argb;
        this.onMs = builder.onMs;
        this.offMs = builder.offMs;
        this.channelId = builder.channelId;
        this.notificationId = builder.notificationId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public int getSmallIcon() {
        return smallIcon;
    }

    public int getPriority() {
        return priority;
    }

    public int getImportance() {
        return importance;
    }

    public Intent getIntent() {
        return intent;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public int getDefaults() {
        return defaults;
    }

    public boolean isAutoCancel() {
        return autoCancel;
    }

    public int getArgb() {
        return argb;
    }

    public int getOnMs() {
        return onMs;
    }

    public int getOffMs() {
        return offMs;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    @Override
    public String toString() {
        return "RollNotification{" +
                "contentTitle='" + contentTitle + '\'' +
                ", contentText='" + contentText + '\'' +
                ", smallIcon=" + smallIcon +
                ", priority=" + priority +
                ", importance=" + importance +
                ", intent=" + intent +
                ", pendingIntent=" + pendingIntent +
                ", defaults=" + defaults +
                ", autoCancel=" + autoCancel +
                ", argb=" + argb +
                ", onMs=" + onMs +
                ", offMs=" + offMs +
                ", channelId=" + channelId +
                ", notificationId=" + notificationId +
                '}';
    }

    public Notification build(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setPriority(priority)
                .setContentIntent(pendingIntent)
                .setDefaults(defaults)
                .setAutoCancel(autoCancel)
                .setLights(argb, onMs, offMs);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.default_channel_name);
            String description = context.getString(R.string.default_channel_description);
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        return builder.build();
    }

    public static class RollNotificationBuilder {
        private final String contentTitle;
        private final String contentText;
        private int smallIcon = R.drawable.default_notification_icon;
        private int priority = NotificationCompat.PRIORITY_DEFAULT;
        private int importance = NotificationManager.IMPORTANCE_DEFAULT;
        private Intent intent;
        private PendingIntent pendingIntent;
        private int defaults = Notification.DEFAULT_ALL;
        private boolean autoCancel = true;
        private int argb;
        private int onMs;
        private int offMs;
        private String channelId;
        private int notificationId = 0;

        public RollNotificationBuilder(String contentTitle, String contentText) {
            this.contentTitle = contentTitle;
            this.contentText = contentText;
        }

        public RollNotificationBuilder setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public RollNotificationBuilder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public RollNotificationBuilder setImportance(int importance) {
            this.importance = importance;
            return this;
        }

        public RollNotificationBuilder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        public RollNotificationBuilder setPendingIntent(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;
            return this;
        }

        public RollNotificationBuilder setDefaults(int defaults) {
            this.defaults = defaults;
            return this;
        }

        public RollNotificationBuilder setAutoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public RollNotificationBuilder setLights(int argb, int onMs, int offMs) {
            this.argb = argb;
            this.onMs = onMs;
            this.offMs = offMs;
            return this;
        }

        public RollNotificationBuilder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public RollNotificationBuilder setNotificationId(int notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public RollNotification build() {
            RollNotification rollNotification = new RollNotification(this);
            return rollNotification;
        }
    }

}

package com.example.to_dolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {
    private Intent mNotificationIntent;
    private PendingIntent mActivityIntent;

    private final long[] mVibratePattern = { 0, 300};
    private final int mNotificationID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra(ToDoItem.NAME);
        String description = intent.getStringExtra(ToDoItem.DESCRIPTION);
        long date = intent.getLongExtra(ToDoItem.DATE, 0);
        Date lineDate = new Date(date);
        int urgency = intent.getIntExtra(ToDoItem.URGENCY, 0);

        mNotificationIntent = new Intent(context, MainActivity.class);
        mActivityIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification.Builder notificationBuilder = new Notification.Builder(context).setTicker(name)
                                                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                                                    .setAutoCancel(true)
                                                    .setContentTitle(name)
                                                    .setContentText(description + "\n" + ToDoItem.format.format(lineDate))
                                                    .setContentIntent(mActivityIntent)
                                                    .setVibrate(mVibratePattern);

        if (Build.VERSION.SDK_INT >= 21) {
            switch (ToDoItem.Urgency.values()[urgency]) {
                case LOW:
                    notificationBuilder.setColor(0xFF48B427);
                    break;
                case MEDIUM:
                    notificationBuilder.setColor(0xFFEFFD0E);
                    break;
                case HIGH:
                    notificationBuilder.setColor(0xFFFF0000);
                    break;
            }
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(mNotificationID, notificationBuilder.build());
    }
}

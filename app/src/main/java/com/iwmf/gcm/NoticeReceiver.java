package com.iwmf.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iwmf.CheckinAlertReportaActivity;
import com.iwmf.R;

/**
 * <p> Receives push notification and display local notification with appropriate details. </p>
 */
@SuppressWarnings("ALL")
public class NoticeReceiver extends BroadcastReceiver {

    public static final int NOTIFY_ME_ID = 1;

    /*
     * @param context
     * @param message
     * @param isConfirmed
     * @param registrationId
     */
    private static void generateNotification(Context context, Bundle b, String message) {

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, CheckinAlertReportaActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtras(b);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(NOTIFY_ME_ID, notification);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        generateNotification(context, intent.getExtras(), intent.getExtras().getString("message"));
    }
}

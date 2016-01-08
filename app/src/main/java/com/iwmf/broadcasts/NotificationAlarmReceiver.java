package com.iwmf.broadcasts;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.iwmf.CheckinAlertReportaActivity;
import com.iwmf.HomeScreen;
import com.iwmf.R;
import com.iwmf.data.PendingMediaData;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.DBHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent and then starts the IntentService {@code SampleSchedulingService}
 * to do some work.
 */
@SuppressWarnings("ALL")
public class NotificationAlarmReceiver extends WakefulBroadcastReceiver {

    private static void generateNotification(Context context, int request_code, String message_) {

        String message = "";

        String title = context.getString(R.string.app_name);

        Intent mIntent;

        if (request_code == ConstantData.ALARM_MISSED_CHECKIN) {
            CryptoManager.getInstance(context).getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, true).apply();
            mIntent = new Intent(context, HomeScreen.class);
            mIntent.putExtra("isMissedCheckin", true);
            message = context.getString(R.string.missed_checkin_set_for, new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date()));

            try {
                Editor editor = CryptoManager.getInstance(context).getPrefs().edit();

                editor.remove(PARAMS.TAG_DESCRIPTION);
                editor.remove(PARAMS.TAG_STARTTIME);
                editor.remove(PARAMS.TAG_FREQUENCY);
                editor.remove(PARAMS.TAG_MESSAGE_EMAIL);
                editor.remove(PARAMS.TAG_RECEIVEPROMPT);
                editor.remove(PARAMS.TAG_MESSAGE_SOCIAL);
                editor.remove(PARAMS.TAG_LONGITUDE);
                editor.remove(PARAMS.TAG_MESSAGE_SMS);
                editor.remove(PARAMS.TAG_CHECKIN_ID);
                editor.remove(PARAMS.TAG_ENDTIME);
                editor.remove(PARAMS.TAG_STATUS);
                editor.remove(PARAMS.TAG_CONTACTLIST);
                editor.remove(PARAMS.TAG_LOCATION);
                editor.remove(PARAMS.TAG_LATITUDE);
                editor.remove(PARAMS.TAG_LASTCONFIRMTIME);

                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

            deleteAllMedia(context);

        } else {

            mIntent = new Intent(context, CheckinAlertReportaActivity.class);
            if (ConstantData.ALARM_REMINDER == request_code) {

                message = context.getString(R.string.confirm_checkin_10min);

            } else if (ConstantData.ALARM_FREQ == request_code) {

                message = context.getString(R.string.confirm_checkin_now);

            } else if (ConstantData.ALARM_ENDTIME == request_code) {

                message = context.getString(R.string.checkin_closed);
                deleteAllMedia(context);

            } else if (ConstantData.ALARM_APP_LOCK == request_code) {

                CryptoManager.getInstance(context).getPrefs().edit().putString("lockstatus", "1").apply();
                mIntent = new Intent(context, HomeScreen.class);
                message = message_;
                deleteAllMedia(context);

            } else if (ConstantData.ALARM_APP_UNLOCK == request_code) {

                CryptoManager.getInstance(context).getPrefs().edit().putString("lockstatus", "").apply();
                mIntent = new Intent(context, HomeScreen.class);
                message = message_;
            }
        }

        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.putExtra("request_code", request_code);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, request_code > 0 ? request_code : new Random().nextInt(), mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 101, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title).setSummaryText(message));
        mBuilder.setContentText(message);
        mBuilder.setContentTitle(title);
        mBuilder.setLargeIcon(icon);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setTicker(message);
        mBuilder.setSmallIcon(R.drawable.ic_n);

        try {
            mBuilder.setColor(context.getResources().getColor(R.color.orange_color));
        } catch (Exception ignored) {
        }

        mBuilder.setVibrate(new long[]{1000, 1000, 1000});
        mBuilder.setCategory(NotificationCompat.CATEGORY_ALARM);
        mBuilder.setLights(NotificationCompat.COLOR_DEFAULT, 3000, 3000);
        mNotificationManager.notify(1, mBuilder.build());

    }

    private static void deleteAllMedia(Context context) {
        try {

            if (ConstantData.DB == null || !ConstantData.DB.isOpen()) {
                ConstantData.DB = new DBHelper(context);
                ConstantData.DB.open();
            }

            ArrayList<PendingMediaData> list = ConstantData.DB.getPendingMediaList();

            if (list != null && list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {

                    ConstantData.DB.deleteMedia(list.get(i).getId());
                    File mFile = new File(list.get(i).getFilePath());
                    if (mFile.exists()) {
                        mFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        generateNotification(context, intent.getExtras().getInt("request_code", 0), intent.getExtras().getString("message", ""));

    }
}

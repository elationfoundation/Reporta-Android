package com.iwmf.broadcasts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.iwmf.IWMFApplication;

import java.util.Calendar;

/**
 * <p> AlarmBroadcast to handle alarm events at scheduled time.
 * Alarm can be set and canceled by request. </p>
 */
public class AlarmBroadcast extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        int requestCode = 0;
        if (intent.hasExtra("request_code")) {
            requestCode = intent.getIntExtra("request_code", 0);
        }

        Intent broadcast = new Intent(IWMFApplication.BROADCAST_ACTION_NOTIFICATION);
        broadcast.putExtra("request_code", requestCode);
        context.sendOrderedBroadcast(broadcast, null);
    }

    public void setAlarm(Context context, long trigerDuration, int request_code) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        cancelAlarm(context, request_code);

        Intent intent = new Intent(context, AlarmBroadcast.class);
        intent.putExtra("request_code", request_code);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(mCalendar.getTimeInMillis() + trigerDuration);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), alarmIntent);


        ComponentName receiver = new ComponentName(context, AppBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int request_code) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmMgr != null) {

            Intent intent = new Intent(context, AlarmBroadcast.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmMgr.cancel(alarmIntent);
        }

        ComponentName receiver = new ComponentName(context, AppBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}

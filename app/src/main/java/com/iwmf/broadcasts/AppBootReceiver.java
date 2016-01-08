package com.iwmf.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iwmf.http.PARAMS;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.Utils;

import java.util.Calendar;

/**
 * This BroadcastReceiver automatically (re)starts the alarm when the device is rebooted. This receiver is set to be disabled
 * (android:enabled="false") in the application's manifest file. When the user sets the alarm, the receiver is enabled. When the user cancels the
 * alarm, the receiver is disabled, so that rebooting the device will not trigger this receiver.
 */

// BEGIN_INCLUDE(autostart)

public class AppBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        CryptoManager prefManager = CryptoManager.getInstance(context);

        long endDateTemp = prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0);
        Calendar mCurrentCalendar = Calendar.getInstance();

        long currentTime = mCurrentCalendar.getTimeInMillis();

        if (prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0) > 0 && (endDateTemp == 0 || endDateTemp > currentTime)) {

            AlarmBroadcast mAlarmBroadcast = new AlarmBroadcast();

            int frequency = prefManager.getPrefs().getInt(PARAMS.TAG_FREQUENCY, 0);
            long startTime = prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0);
            long lastConfirmCheckinTime = prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, 0);

            if (startTime > lastConfirmCheckinTime) {
                lastConfirmCheckinTime = startTime;
            }

            /**
             * nextConfirmation is for calculate next confirmation time
             */
            prefManager.getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, false).apply();

            // checkin remider
            Utils.setCheckInReminder(startTime, endDateTemp, currentTime, lastConfirmCheckinTime, frequency, mAlarmBroadcast, context);


            // checkIn frequency
            Utils.setCheckInFrequency(startTime, endDateTemp, currentTime, lastConfirmCheckinTime, frequency, mAlarmBroadcast, context);


            // Miss checkin
            Utils.setMissedCheckIn(startTime, endDateTemp, currentTime, lastConfirmCheckinTime, frequency, mAlarmBroadcast, context);

            // End checkin
            Utils.setEndCheckIn(endDateTemp, currentTime, mAlarmBroadcast, context);

            // Pending Check In Reminder
            Utils.setPendingCheckIn(startTime, currentTime, mAlarmBroadcast, context);

        }
    }

}
// END_INCLUDE(autostart)

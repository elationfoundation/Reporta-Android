package com.iwmf.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iwmf.IWMFApplication;
import com.iwmf.gcm.GcmBroadcastReceiver;
import com.iwmf.utils.ConstantData;

/**
 * This {@code IntentService} does the actual handling of the GCM message. {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the service is finished, it calls {@code completeWakefulIntent()} to
 * release the wake lock.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {

        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle mBundle = intent.getExtras();

        if (mBundle != null && !mBundle.isEmpty()) {

            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                try {

                    String type = mBundle.getString("type");

                    Intent broadcast23 = new Intent(IWMFApplication.BROADCAST_ACTION_NOTIFICATION);


                    if (mBundle.containsKey("message")) {
                        broadcast23.putExtra("message", mBundle.getString("message"));
                    } else {
                        broadcast23.putExtra("message", "");
                    }

                    if (type != null && type.equals("2")) {

                        broadcast23.putExtra("request_code", ConstantData.ALARM_APP_UNLOCK);

                        sendOrderedBroadcast(broadcast23, null);

                    } else if (type != null && type.equals("3")) {

                        broadcast23.putExtra("request_code", ConstantData.ALARM_APP_LOCK);
                        sendOrderedBroadcast(broadcast23, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}
package com.iwmf.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iwmf.CheckinAlertReportaActivity;
import com.iwmf.HomeScreen;
import com.iwmf.IWMFApplication;
import com.iwmf.LoginActivity;
import com.iwmf.R;
import com.iwmf.broadcasts.AlarmBroadcast;
import com.iwmf.data.PendingMediaData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.DBHelper;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.RootUtil;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;
import com.iwmf.views.MyProgressDialog;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p> BaseAppCompatActivity that is  extended by every Activity in Reporta. Takes care of push in every activity. </p>
 */
@SuppressWarnings("ALL")
@SuppressLint("Registered")
public class BaseAppCompatActivity extends AppCompatActivity {

    private static final String TAG = BaseAppCompatActivity.class.getSimpleName();
    public SharedPreferences sharedpreferences = null;
    public CryptoManager prefManager = null;
    private AlertDialog dialogLock = null;
    private MyProgressDialog progressDialog = null;
    private AlertDialog.Builder dialogBuilder = null;
    BroadcastReceiver mPushReceiverBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            abortBroadcast();

            int requestCode = intent.getExtras().getInt("request_code", 0);
            Intent mIntent;

            if (requestCode == ConstantData.ALARM_MISSED_CHECKIN) {

                CryptoManager.getInstance(context).getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, true).apply();

                mIntent = new Intent(context, HomeScreen.class);
                mIntent.putExtra("isMissedCheckin", true);

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

                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mIntent.putExtra("request_code", requestCode);
                    startActivity(mIntent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == ConstantData.ALARM_APP_LOCK) {

                prefManager.getPrefs().edit().putString("lockstatus", "1").apply();

                performSOS(false, false);

            } else if (requestCode == ConstantData.ALARM_APP_UNLOCK) {

                dissmissLockDialog();

            } else {
                mIntent = new Intent(context, CheckinAlertReportaActivity.class);

                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mIntent.putExtra("request_code", requestCode);
                startActivity(mIntent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences("reportapref", Context.MODE_PRIVATE);
        prefManager = CryptoManager.getInstance(BaseAppCompatActivity.this);
    }

    private void registerPushReceiver() {

        IntentFilter mFilterPush = new IntentFilter();
        mFilterPush.addAction(IWMFApplication.BROADCAST_ACTION_NOTIFICATION);
        mFilterPush.setPriority(2);
        registerReceiver(mPushReceiverBroadcastReceiver, mFilterPush);

    }

    private void unRegisterPushReceiver() {

        unregisterReceiver(mPushReceiverBroadcastReceiver);
    }

    @Override
    protected void onStart() {

        super.onStart();
        registerPushReceiver();
        Utils.changeLocale(this);
    }

    @Override
    protected void onStop() {

        unRegisterPushReceiver();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    public void displayProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new MyProgressDialog(this);
        }
        progressDialog.show();
    }

    public void dismissProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void dismissProgressDialogOnUIthread() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void showMessage(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dismissProgressDialog();

                AlertDialog.Builder dialog = new AlertDialog.Builder(BaseAppCompatActivity.this);
                dialog.setTitle("");
                dialog.setMessage(message);
                dialog.setPositiveButton(getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    public void showMessageWithFinish(final String title, final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dismissProgressDialog();

                AlertDialog.Builder dialog = new AlertDialog.Builder(BaseAppCompatActivity.this);
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setCancelable(false);
                dialog.setPositiveButton(getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialog != null) dialog.dismiss();
                        finish();
                        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
                    }
                });
                dialog.show();
            }
        });
    }

    private void deleteAllMedia() {
        try {

            if (ConstantData.DB == null || !ConstantData.DB.isOpen()) {
                ConstantData.DB = new DBHelper(BaseAppCompatActivity.this);
                ConstantData.DB.open();
            }

            ArrayList<PendingMediaData> list = ConstantData.DB.getPendingMediaList();


            for (int i = 0; i < list.size(); i++) {

                ConstantData.DB.deleteMedia(list.get(i).getId());
                try {
                    File mFile = new File(list.get(i).getFilePath());
                    if (mFile.exists()) {
                        mFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performSOS(final boolean wantToFinishOnDismiss, final boolean isCallSOS) {

        try {


            displayProgressDialog();

            deleteAllMedia();


            // *****
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();

            AlarmBroadcast mAlarmBroadcast = new AlarmBroadcast();
            mAlarmBroadcast.cancelAlarm(getApplicationContext(), ConstantData.ALARM_ENDTIME);
            mAlarmBroadcast.cancelAlarm(getApplicationContext(), ConstantData.ALARM_FREQ);
            mAlarmBroadcast.cancelAlarm(getApplicationContext(), ConstantData.ALARM_REMINDER);
            mAlarmBroadcast.cancelAlarm(getApplicationContext(), ConstantData.ALARM_MISSED_CHECKIN);
            mAlarmBroadcast.cancelAlarm(getApplicationContext(), ConstantData.ALARM_PENDING_REMINDER);

            // ******

            try {

                Utils.clearAllNotifications(BaseAppCompatActivity.this);

                SharedPreferences.Editor editor = prefManager.getPrefs().edit();
                editor.putInt(PARAMS.KEY_SOS_ID, 0);
                editor.putBoolean(PARAMS.KEY_SOS_STATUS, true);


                // *****< Cancel active check-ins >*******


                editor.putInt(PARAMS.KEY_SOS_ID, 0);
                editor.putBoolean(PARAMS.KEY_SOS_STATUS, false);
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

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_SEND_SOS, RequestMethod.POST, RequestBuilder.getSendSOSRequest(ConstantData.LATITUDE, ConstantData.LONGITUDE));


            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {


                        if (!result.isEmpty()) {

                            JSONObject jObj = new JSONObject(result);

                            if (jObj.getInt("status") == 1) {

                                showAppLockdDialog(wantToFinishOnDismiss, isCallSOS);

                                final int sosId = jObj.getInt(PARAMS.KEY_SOS_ID);

                                Editor editor = prefManager.getPrefs().edit();
                                editor.putInt(PARAMS.KEY_SOS_ID, sosId);
                                editor.putBoolean(PARAMS.KEY_SOS_STATUS, true);
                                editor.apply();

                            } else if (jObj.has("status") && jObj.get("status").toString().equals("3")) {

                                showSessionExpired(jObj.get("message").toString());

                            } else if (jObj.has("message")) {

                                showMessage(jObj.getString("message"));

                            } else {

                                Toast.displayError(BaseAppCompatActivity.this, getString(R.string.try_later));
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                }
            });

            if (isCallSOS) {
                mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                showAppLockdDialog(wantToFinishOnDismiss, isCallSOS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RootUtil.showIfDeviceRooted(getString(R.string.rooting_detected_title), getString(R.string.rooting_detected_msg), BaseAppCompatActivity.this);

        boolean isMissedCheckin = false;

        String lockstatus = "";
        if (!ConstantData.FIXKEY.equals("")) {
            isMissedCheckin = prefManager.getPrefs().getBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, false);
            lockstatus = prefManager.getPrefs().getString("lockstatus", "");
        }

        if (isMissedCheckin || lockstatus.equals("1")) {
            performSOS(false, false);
        } else {
            dissmissLockDialog();
        }
    }

    public void dissmissLockDialog() {
        try {
            prefManager.getPrefs().edit().putString("lockstatus", "").apply();
            dismissProgressDialog();
            if (dialogLock != null) {
                dialogLock.dismiss();
            }

            dialogLock = null;

        } catch (Exception e) {
            e.printStackTrace();
            dialogLock = null;
        }
    }

    private void showAppLockdDialog(final boolean wantToFinishOnDismiss, final boolean isCallSOS) {

        dialogBuilder = null;
        dialogBuilder = new AlertDialog.Builder(BaseAppCompatActivity.this);
        dialogBuilder.setTitle(getString(R.string.reporta));
        dialogBuilder.setMessage(getString(R.string.app_locked));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(getString(R.string.login), new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                dialogLock.dismiss();
                displaySOSPopup(wantToFinishOnDismiss, isCallSOS);

            }
        });

        if (dialogLock != null) {
            try {
                dialogLock.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dialogLock = null;
        dialogLock = dialogBuilder.create();
        dialogLock.setCancelable(false);

        dialogLock.show();

    }

    public void unlockApp(final AlertDialog dialog, int sosId, String password, String passcode, final boolean wantToFinishOnDismiss, final boolean isSos) {

        try {

            displayProgressDialog();

            String checkin_id = prefManager.getPrefs().getString("cid", "");

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_UNLOCK_APP, RequestMethod.POST, RequestBuilder.getUnlockAppRequest(password, sosId, passcode, ConstantData.LATITUDE, ConstantData.LONGITUDE, checkin_id));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {


                        JSONObject jObj = new JSONObject(result);

                        if (jObj.has("status") && jObj.has("message")) {

                            if (jObj.get("status").toString().equals("1")) {

                                dialog.dismiss();
                                dialog.cancel();

                                dialogLock.dismiss();
                                dialogLock.cancel();
                                dialogLock = null;

                                Editor editor = prefManager.getPrefs().edit();

                                editor.putInt(PARAMS.KEY_SOS_ID, 0);
                                editor.putBoolean(PARAMS.KEY_SOS_STATUS, false);

                                if (!isSos) {
                                    // Miss checkin
                                    editor.putString("cid", "");
                                    editor.putString("lockstatus", "");
                                    editor.putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, false);
                                }

                                editor.apply();

                                Toast.displayMessage(BaseAppCompatActivity.this, jObj.getString("message"));

                                if (wantToFinishOnDismiss) {
                                    finish();
                                }
                            } else if (jObj.get("status").toString().equals("3")) {
                                showSessionExpired(jObj.get("message").toString());
                            } else {
                                Toast.displayMessage(BaseAppCompatActivity.this, jObj.getString("message"));
                            }
                        } else {
                            Toast.displayError(BaseAppCompatActivity.this, getString(R.string.try_later));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    Toast.displayError(BaseAppCompatActivity.this, getString(R.string.try_later));
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void showSessionExpired(String msg) {

        final AlertDialog dialog = new AlertDialog.Builder(BaseAppCompatActivity.this).setMessage(msg).setCancelable(false).setPositiveButton(android.R.string.ok, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();

                        Utils.clearAllNotifications(BaseAppCompatActivity.this);


                        sharedpreferences.edit().putBoolean(PARAMS.KEY_STAY_LOGGED_IN, false).apply();

                        removeCheckIn();

                        ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
                        ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));

                        Utils.changeLocale(ConstantData.LANGUAGE_CODE, BaseAppCompatActivity.this);

                        Intent mIntent = new Intent(BaseAppCompatActivity.this, LoginActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

                    }
                });
            }
        });
        dialog.show();

    }

    private void removeCheckIn() {

        try {
            Editor editor = prefManager.getPrefs().edit();

            editor.remove(PARAMS.TAG_HEADERTOKEN);
            editor.remove(PARAMS.TAG_DEVICETOKEN);

            ConstantData.HEADERTOKEN = "";
            ConstantData.REGISTRATIONID = "";

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
    }

    public void displaySOSPopup(final boolean wantToFinishOnDismiss, final boolean isSos) {

        LayoutInflater mInflater = LayoutInflater.from(BaseAppCompatActivity.this);
        @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.dialog_sos, null, false);

        final EditText edtPassword = (EditText) view.findViewById(R.id.edtPassword);
        final EditText edtPasscode = (EditText) view.findViewById(R.id.edtPasscode);

        dialogBuilder = null;
        dialogBuilder = new AlertDialog.Builder(BaseAppCompatActivity.this);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(view).setTitle(R.string.unlock_reporta);
        dialogBuilder.setPositiveButton(getString(R.string.ok), null);

        dialogBuilder.setNegativeButton(getString(R.string.cancel), null);

        if (dialogLock != null) {
            try {
                dialogLock.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dialogLock = null;

        dialogLock = dialogBuilder.create();

        dialogLock.setCancelable(false);

        dialogLock.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button buttonOK = dialogLock.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonOK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        String password = edtPassword.getText().toString().trim();
                        String passcode = edtPasscode.getText().toString().trim();

                        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passcode)) {
                            unlockApp(dialogLock, prefManager.getPrefs().getInt(PARAMS.KEY_SOS_ID, 0), password, passcode, wantToFinishOnDismiss, isSos);
                        }
                    }
                });

                Button buttonCancel = dialogLock.getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialogLock.dismiss();
                        showAppLockdDialog(wantToFinishOnDismiss, isSos);
                    }
                });
            }
        });

        dialogLock.show();

    }

    public void displayCancelCheckIn(final int checkInId) {

        LayoutInflater mInflater = LayoutInflater.from(BaseAppCompatActivity.this);
        @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.dialog_cancel_checkin, null, false);

        final EditText edtPassword = (EditText) view.findViewById(R.id.edtPassword);

        final AlertDialog dialog = new AlertDialog.Builder(BaseAppCompatActivity.this).setView(view).setTitle(R.string.closenow).setCancelable(true).setPositiveButton(android.R.string.ok, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String password = edtPassword.getText().toString().trim();

                        if (!TextUtils.isEmpty(password)) {

                            cancelCheckIn(dialog, checkInId, password);
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void cancelCheckIn(final AlertDialog dialog, int checkInId, String password) {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_UPDATE_CHECKIN, RequestMethod.POST, RequestBuilder.getUpdateCheckInRequest(getUTCTIME(new Date().getTime()), checkInId, 4, false, password));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        if (!result.isEmpty()) {

                            JSONObject jObj = new JSONObject(result);

                            Toast.displayError(BaseAppCompatActivity.this, jObj.getString("message"));

                            if (jObj.getInt("status") == 1) {

                                try {
                                    Utils.clearAllNotifications(BaseAppCompatActivity.this);
                                    Editor editor = prefManager.getPrefs().edit();

                                    editor.putInt(PARAMS.KEY_SOS_ID, 0);
                                    editor.putBoolean(PARAMS.KEY_SOS_STATUS, false);
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
                                    dismissProgressDialog();
                                }

                                dialog.dismiss();

                            } else if (jObj.has("status") && jObj.get("status").toString().equals("3")) {

                                showSessionExpired(jObj.get("message").toString());

                            } else if (jObj.has("message")) {

                                Toast.displayError(BaseAppCompatActivity.this, jObj.get("message").toString());

                            } else {

                                Toast.displayError(BaseAppCompatActivity.this, getString(R.string.try_later));
                            }

                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getUTCTIME(long date) {

        try {
            Date parsed = new Date(date);


            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sourceFormat.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iwmf.AddCheckinActivity;
import com.iwmf.HomeScreen;
import com.iwmf.MediaActivity;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.broadcasts.AlarmBroadcast;
import com.iwmf.data.AddCheckinData;
import com.iwmf.data.PendingMediaData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <p> Display alert checkin and take appropriate action.  </p>
 */
public class CheckinAlertReportaFragment extends BaseFragment implements OnClickListener {

    private final static int ADDMEDIA_REQUEST = -1;
    private static final String EXTRA_REQUEST_CODE = "request_code";
    private Button btnConfirmNow, btnAddmedia, btnEditCheckin, btnUpdateEndTime;
    private AddCheckinData addCheckinData = null;
    private AlarmBroadcast mAlarmBroadcast = null;
    private int requestCode = ConstantData.ALARM_REMINDER;

    public CheckinAlertReportaFragment() {

    }

    public static CheckinAlertReportaFragment getInstance(int requestId) {

        CheckinAlertReportaFragment mFragment = new CheckinAlertReportaFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt(EXTRA_REQUEST_CODE, requestId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_REQUEST_CODE, requestCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_reporta, container, false);

        mAlarmBroadcast = new AlarmBroadcast();

        if (savedInstanceState != null) {
            requestCode = savedInstanceState.getInt(EXTRA_REQUEST_CODE);
        } else if (getArguments() != null) {
            requestCode = getArguments().getInt(EXTRA_REQUEST_CODE, ConstantData.ALARM_REMINDER);
        }

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        TextView txtYourCheckin = (TextView) v.findViewById(R.id.txtYourCheckin);
        btnConfirmNow = (Button) v.findViewById(R.id.btnClosenow);
        btnAddmedia = (Button) v.findViewById(R.id.btnAddmedia);
        btnEditCheckin = (Button) v.findViewById(R.id.btnEditCheckin);
        btnUpdateEndTime = (Button) v.findViewById(R.id.btnUpdateEndTime);

        btnConfirmNow.setOnClickListener(this);
        btnAddmedia.setOnClickListener(this);
        btnEditCheckin.setOnClickListener(this);
        btnUpdateEndTime.setOnClickListener(this);

        addCheckinData = new AddCheckinData();

        if (prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0) > 0) {

            getCheckIn();

            if (addCheckinData.getStarttime() > Calendar.getInstance().getTimeInMillis()) {
                txtYourCheckin.setText(getString(R.string.check_start_message) + " " + getFormatedTime(new Date(addCheckinData.getStarttime())));

            } else {
                Calendar calendar = Calendar.getInstance();

                calendar.setTimeInMillis(addCheckinData.getLastConfirmTime());
                calendar.add(Calendar.MINUTE, addCheckinData.getFrequency());

                if (calendar.getTimeInMillis() > addCheckinData.getEndtime() && addCheckinData.getEndtime() > 0) {
                    calendar.setTimeInMillis(addCheckinData.getEndtime());
                    txtYourCheckin.setText(getString(R.string.checkin_close_at) + " " + getFormatedTime(calendar.getTime()));
                } else {
                    txtYourCheckin.setText(getString(R.string.your_next_conf) + " " + getFormatedTime(calendar.getTime()));
                }
            }
        }

        if (requestCode == ConstantData.ALARM_ENDTIME) {
            txtYourCheckin.setText(R.string.checkin_closed);
            btnConfirmNow.setVisibility(View.GONE);
            btnAddmedia.setEnabled(false);
            btnEditCheckin.setEnabled(false);
            btnUpdateEndTime.setText(R.string.ok);
        } else if (requestCode == ConstantData.ALARM_PENDING_REMINDER) {
            btnConfirmNow.setText(R.string.start_now);
            btnUpdateEndTime.setText(R.string.cancel_checkin);
            // TODO check here if we needs add media functionality and edit check in functionality for pending Check In
        }
    }

    private String getFormatedTime(Date startDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        if (simpleDateFormat.format(startDate).equals(simpleDateFormat.format(new Date()))) {
            simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            return simpleDateFormat.format(startDate);
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            return simpleDateFormat.format(startDate);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnConfirmNow) {
            updateCheckRequest(requestCode == ConstantData.ALARM_PENDING_REMINDER ? 1 : 2);

        } else if (v == btnAddmedia) {

            Intent intent = new Intent(getActivity(), MediaActivity.class);
            startActivityForResult(intent, ADDMEDIA_REQUEST);

        } else if (v == btnEditCheckin) {

            Intent mIntent = new Intent(getActivity(), AddCheckinActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mIntent);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.no_anim);

        } else if (v == btnUpdateEndTime) {

            if (requestCode == ConstantData.ALARM_ENDTIME) {
                // OK button clicked
                Utils.clearAllNotifications(getActivity());
                removeCheckIn();
                Intent mIntent = new Intent(getActivity(), HomeScreen.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(mIntent);
                getActivity().finish();
                return;
            }
            ((BaseAppCompatActivity) getActivity()).displayCancelCheckIn(addCheckinData.getCheckin_id());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADDMEDIA_REQUEST) {
                PendingMediaData mediaData = (PendingMediaData) (data.getExtras().getSerializable("data"));
                assert mediaData != null;
                ConstantData.DB.insertPendingMedia(mediaData.getRef_id(), mediaData.getFilePath(), mediaData.getMediaType(), mediaData.getCreatedDate(), mediaData.getTableId(), mediaData.getLatitude(), mediaData.getLongitude(), mediaData.getAttemptCount());
            }
        }
    }

    private void getCheckIn() {

        try {
            prefManager.getPrefs().getBoolean(PARAMS.KEY_SOS_STATUS, false);

            addCheckinData.setDescription(prefManager.getPrefs().getString(PARAMS.TAG_DESCRIPTION, ""));
            addCheckinData.setStarttime(prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0));
            addCheckinData.setFrequency(prefManager.getPrefs().getInt(PARAMS.TAG_FREQUENCY, 0));
            addCheckinData.setMessage_email(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_EMAIL, ""));
            addCheckinData.setReceiveprompt(prefManager.getPrefs().getString(PARAMS.TAG_RECEIVEPROMPT, "3"));
            addCheckinData.setMessage_social(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_SOCIAL, ""));
            addCheckinData.setLongitude(Double.parseDouble(prefManager.getPrefs().getString(PARAMS.TAG_LONGITUDE, "0")));
            addCheckinData.setMessage_sms(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_SMS, ""));
            addCheckinData.setCheckin_id(prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0));
            addCheckinData.setEndtime(prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0));
            addCheckinData.setStaus(prefManager.getPrefs().getInt(PARAMS.TAG_STATUS, 0));
            addCheckinData.setContactlist(prefManager.getPrefs().getString(PARAMS.TAG_CONTACTLIST, ""));
            addCheckinData.setLocation(prefManager.getPrefs().getString(PARAMS.TAG_LOCATION, ""));
            addCheckinData.setLatitude(Double.parseDouble(prefManager.getPrefs().getString(PARAMS.TAG_LATITUDE, "0")));
            addCheckinData.setLastConfirmTime(prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0)));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateCheckRequest(final int status) {

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_UPDATE_CHECKIN, RequestMethod.POST, RequestBuilder.getUpdateCheckInRequest(getUTCTIME(new Date().getTime()), addCheckinData.getCheckin_id(), status, false, ""));
        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                try {

                    dismissProgressDialog();

                    JSONObject jsonObject = new JSONObject(result);

                    ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", jsonObject.getString("message"));


                    if (jsonObject.get("status").toString().equals("1")) {

                        Calendar mCalendar = Calendar.getInstance();
                        mCalendar.set(Calendar.SECOND, 0);
                        mCalendar.set(Calendar.MILLISECOND, 0);

                        addCheckinData.setLastConfirmTime(mCalendar.getTimeInMillis());

                        Editor editor = prefManager.getPrefs().edit();
                        if (status == 1) {
                            // Start Now for Pending check-in
                            editor.putLong(PARAMS.TAG_STARTTIME, addCheckinData.getLastConfirmTime());

                        }
                        editor.putLong(PARAMS.TAG_LASTCONFIRMTIME, addCheckinData.getLastConfirmTime());
                        editor.putLong(PARAMS.TAG_TIME, addCheckinData.getLastConfirmTime());
                        editor.apply();

                        addCheckinData = new AddCheckinData();
                        getCheckIn();

                        createCheckInNotification(addCheckinData.getLastConfirmTime(), addCheckinData.getFrequency());
                        endCheckinNotification(addCheckinData.getEndtime());

                    } else if (jsonObject.get("status").toString().equals("3")) {

                        ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                    } else {

                        ((BaseAppCompatActivity) getActivity()).performSOS(false, false);

                        removeCheckIn();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

                dismissProgressDialog();
            }
        });
        displayProgressDialog();
        mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeCheckIn() {

        try {
            Editor editor = prefManager.getPrefs().edit();

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

    private void createCheckInNotification(long startTime, int frequency) {

        Utils.clearAllNotifications(getActivity());

        try {
            Calendar mCurrentCalendar = Calendar.getInstance();
            mCurrentCalendar.set(Calendar.SECOND, 0);
            mCurrentCalendar.set(Calendar.MILLISECOND, 0);
            long currentTime = mCurrentCalendar.getTimeInMillis();
            long endTime = addCheckinData.getEndtime();
            if (endTime == 0) {
                endTime = Long.MAX_VALUE;
            }

            if (frequency >= 30) {
                // checkin remider
                long startOffsetReminder = 0;
                if (startTime > currentTime) {
                    startOffsetReminder = startTime - currentTime;
                }
                long trigerDurationReminder = startOffsetReminder + ((frequency - 10) * ConstantData.MINUTE_IN_MILISEC);

                Calendar mTempCalendar = Calendar.getInstance();
                mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDurationReminder);

                if (mTempCalendar.getTimeInMillis() < endTime) {
                    mAlarmBroadcast.setAlarm(getActivity(), trigerDurationReminder, ConstantData.ALARM_REMINDER);
                }
            }

            // CheckIn freq
            long startOffsetFreq = 0;
            if (startTime > currentTime) {
                startOffsetFreq = startTime - currentTime;
            }

            long trigerDurationFreq = startOffsetFreq + ((frequency - 2) * ConstantData.MINUTE_IN_MILISEC);

            Calendar mTempCalendar = Calendar.getInstance();
            mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDurationFreq);

            if (mTempCalendar.getTimeInMillis() < endTime) {
                mAlarmBroadcast.setAlarm(getActivity(), trigerDurationFreq, ConstantData.ALARM_FREQ);
            }

            // Missed CheckIn
            long trigerDurationMissed = startOffsetFreq + ((frequency + 1) * ConstantData.MINUTE_IN_MILISEC);

            mTempCalendar = Calendar.getInstance();
            mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDurationMissed);

            if (mTempCalendar.getTimeInMillis() < endTime) {
                mAlarmBroadcast.setAlarm(getActivity(), trigerDurationMissed, ConstantData.ALARM_MISSED_CHECKIN);
            }

            // Pending Check In Reminder
            Utils.setPendingCheckIn(startTime, currentTime, mAlarmBroadcast, getActivity());
//            if (startTime > currentTime) {
//                // 5 minutes before start time for pending check in
//                long startOffsetReminder = startTime - currentTime;
//                if (startOffsetReminder >= 30 * ConstantData.MINUTE_IN_MILISEC) {
//                    startOffsetReminder -= 10 * ConstantData.MINUTE_IN_MILISEC;
//                    mTempCalendar = Calendar.getInstance();
//                    mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + startOffsetReminder);
//                    mAlarmBroadcast.setAlarm(getActivity(), startOffsetReminder, ConstantData.ALARM_PENDING_REMINDER);
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endCheckinNotification(long endtime) {

        try {

            if (addCheckinData.getEndtime() > addCheckinData.getStarttime()) {

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);

                // end checkin
                long startOffsetEndtime = 0;
                if (endtime > mCalendar.getTimeInMillis()) {
                    startOffsetEndtime = endtime - mCalendar.getTimeInMillis();
                }

                mAlarmBroadcast.cancelAlarm(getActivity(), ConstantData.ALARM_ENDTIME);
                mAlarmBroadcast.setAlarm(getActivity(), startOffsetEndtime, ConstantData.ALARM_ENDTIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.iwmf;

import android.os.Bundle;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.data.AddCheckinData;
import com.iwmf.fragments.CheckinAlertReportaFragment;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.ConstantData;

/**
 * <p> Checkin generated alert here.
 * User can edit the checkin and confirm for it through CheckinAlertReportaFragment.  </p>
 */
public class CheckinAlertReportaActivity extends BaseAppCompatActivity {

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;
    private AddCheckinData addCheckinData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        addCheckinData = new AddCheckinData();

        getCheckIn();

        if (savedInstanceState == null) {

            int request_code = 0;
            if (getIntent().hasExtra("request_code")) {
                request_code = getIntent().getIntExtra("request_code", 0);
            }

            if (addCheckinData.getCheckin_id() == 0) {
                finish();
                return;
            }

            if (ConstantData.ALARM_REMINDER == request_code || ConstantData.ALARM_FREQ == request_code || ConstantData.ALARM_ENDTIME == request_code || ConstantData.ALARM_PENDING_REMINDER == request_code) {

                getSupportFragmentManager().beginTransaction().replace(FRAGMENT_CONTAINER, CheckinAlertReportaFragment.getInstance(request_code)).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {

        // super.onBackPressed();
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
            addCheckinData.setLastConfirmTime(prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, 0));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}

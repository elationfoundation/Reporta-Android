package com.iwmf.fragments;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.CryptoManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <p> CheckinConfirmationFragment confirm checkin and take action. </p>
 */
public class CheckinConfirmationFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_checkin_confirmation, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {
        CryptoManager prefManager = CryptoManager.getInstance(getActivity());
        int frequency = prefManager.getPrefs().getInt(PARAMS.TAG_FREQUENCY, 0);
        long lastConfirmCheckinTime = prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, 0);

        Calendar calendar = Calendar.getInstance();
        if (lastConfirmCheckinTime == 0) {
            lastConfirmCheckinTime = prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0);
        }

        calendar.setTimeInMillis(lastConfirmCheckinTime);


        calendar.add(Calendar.MINUTE, frequency);


        TextView lblCheckinConfirmation = (TextView) v.findViewById(R.id.lblCheckinConfirmation);
        lblCheckinConfirmation.setText(getString(R.string.missed_checkin_set_for, getFormatedTime(calendar.getTime())));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        removeCheckIn();
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

    private void removeCheckIn() {

        try {
            Editor editor = CryptoManager.getInstance(getActivity()).getPrefs().edit();

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
}

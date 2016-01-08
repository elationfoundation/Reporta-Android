package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.utils.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * <p> Display checkin time to select for alert on missed checkin. </p>
 */
public class CheckinEndTimeFragment extends BaseFragment {

    private TimePicker timePicker_endtime;
    private long dateVal;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            dateVal = savedInstanceState.getLong("date");
            flag = savedInstanceState.getBoolean("flag");
        } else if (getArguments() != null) {
            dateVal = getArguments().getLong("date");
            flag = getArguments().getBoolean("flag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_checkin_endtime, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        timePicker_endtime = (TimePicker) v.findViewById(R.id.timePicker_endtime);
        Button btnCustomHoursDone = (Button) v.findViewById(R.id.btnCustomHoursDone);
        TextView lblEndTimeHeader = (TextView) v.findViewById(R.id.lblEndTimeHeader);
        if (flag) {
            lblEndTimeHeader.setText(getString(R.string.start_time));
        } else {
            lblEndTimeHeader.setText(getString(R.string.end_time));
        }
        if (dateVal > 0) {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(dateVal);
            timePicker_endtime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            timePicker_endtime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        }

        btnCustomHoursDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int hr = Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY);
                int mn = Calendar.getInstance(Locale.getDefault()).get(Calendar.MINUTE);

                int pHr = timePicker_endtime.getCurrentHour();
                int pMn = timePicker_endtime.getCurrentMinute();

                if (pHr > hr || (pHr == hr && pMn > mn)) {

                    Intent intent = new Intent();

                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.HOUR_OF_DAY, timePicker_endtime.getCurrentHour());
                    mCalendar.set(Calendar.MINUTE, timePicker_endtime.getCurrentMinute());
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MILLISECOND, 0);

                    intent.putExtra("date", "" + mCalendar.getTimeInMillis());
                    getActivity().setResult(Activity.RESULT_OK, intent);

                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);

                } else {

                    Toast.displayError(getActivity(), "Endtime must be greate than current time.");
                }
            }
        });

    }
}

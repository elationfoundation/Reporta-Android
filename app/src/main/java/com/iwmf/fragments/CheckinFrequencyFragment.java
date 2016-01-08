package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.iwmf.CheckinCustomHoursActvity;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.utils.Toast;

/**
 * <p> Define checkin frequency to perform checkin within time.
 * Available frequency time : 15 min, 30 min, Hourly and every 3 hours. </p>
 */
public class CheckinFrequencyFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

    private final static int INTENT_CUSTOM = 1;
    private CheckBox rbtNotifications, rbtSMS, rbtEmail;
    private TextView lblCustom;
    private RadioGroup rdgFrequency;
    private int frequency, custom;
    private String receiveprompt = "1";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            custom = savedInstanceState.getInt("custom");
            frequency = savedInstanceState.getInt("frequency");
            receiveprompt = savedInstanceState.getString("receiveprompt");
        } else if (getArguments() != null) {
            custom = getArguments().getInt("custom");
            frequency = getArguments().getInt("frequency");
            receiveprompt = getArguments().getString("receiveprompt");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_checkin_frequency, container, false);

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        RadioButton rbtFifteenMinute = (RadioButton) v.findViewById(R.id.rbtFifteenMinute);
        RadioButton rbtThirtyMinute = (RadioButton) v.findViewById(R.id.rbtThirtyMinute);
        RadioButton rbtHourly = (RadioButton) v.findViewById(R.id.rbtHourly);
        RadioButton rbtThreeHour = (RadioButton) v.findViewById(R.id.rbtThreeHour);

        rbtFifteenMinute.setOnClickListener(this);
        rbtThirtyMinute.setOnClickListener(this);
        rbtHourly.setOnClickListener(this);
        rbtThreeHour.setOnClickListener(this);

        rbtNotifications = (CheckBox) v.findViewById(R.id.rbtNotifications);
        rbtSMS = (CheckBox) v.findViewById(R.id.rbtSMS);
        rbtEmail = (CheckBox) v.findViewById(R.id.rbtEmail);
        lblCustom = (TextView) v.findViewById(R.id.lblCustom);
        Button btnFrequencyDone = (Button) v.findViewById(R.id.btnFrequencyDone);
        rdgFrequency = (RadioGroup) v.findViewById(R.id.rdgFrequency);

        btnFrequencyDone.setOnClickListener(this);
        lblCustom.setOnClickListener(this);

        if (custom > 0) {
            setCustomText(custom);
            setUnCheckedAllFrequency();
        } else {
            if (frequency == 15) {
                rbtFifteenMinute.setChecked(true);
            } else if (frequency == 30 || frequency == 0) {
                rbtThirtyMinute.setChecked(true);
            } else if (frequency == 60) {
                rbtHourly.setChecked(true);
            } else {
                rbtThreeHour.setChecked(true);
            }
        }

        rbtEmail.setChecked(receiveprompt.contains("1"));

        rbtSMS.setChecked(receiveprompt.contains("2"));

        rbtNotifications.setChecked((receiveprompt.contains("3") || receiveprompt.contains("0")));

    }

    private void setCustomText(int custom) {

        String str = "";
        int min = custom % 60;
        int hours = custom / 60;

        if (hours != 0) {
            str = "" + hours + " " + getString(R.string.hours) + " ";
        }
        if (min != 0) {
            str += "" + min + " " + getString(R.string.minute);
        }
        lblCustom.setText(str);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lblCustom:
                Intent intent = new Intent(getActivity(), CheckinCustomHoursActvity.class);
                intent.putExtra("custom", custom);
                startActivityForResult(intent, INTENT_CUSTOM);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.no_anim);
                break;
            case R.id.btnFrequencyDone:
                intent = new Intent();
                intent.putExtra("frequency", frequency);
                intent.putExtra("custom", custom);

                receiveprompt = "";
                if (rbtEmail.isChecked()) {
                    if (receiveprompt.isEmpty()) {
                        receiveprompt = "1";
                    } else {
                        receiveprompt += ",1";
                    }
                }

                if (rbtSMS.isChecked()) {
                    if (receiveprompt.isEmpty()) {
                        receiveprompt = "2";
                    } else {
                        receiveprompt += ",2";
                    }
                }

                if (rbtNotifications.isChecked()) {
                    if (receiveprompt.isEmpty()) {
                        receiveprompt = "3";
                    } else {
                        receiveprompt += ",3";
                    }
                }

                if (receiveprompt.isEmpty()) {
                    Toast.displayError(getActivity(), getString(R.string.please_select_receive_promts));
                    break;
                }

                intent.putExtra("receiveprompt", receiveprompt);

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);

                break;

            case R.id.rbtFifteenMinute:
                frequency = 15;
                custom = 0;
                lblCustom.setText(R.string.create_custom);
                break;
            case R.id.rbtThirtyMinute:
                custom = 0;
                lblCustom.setText(R.string.create_custom);
                frequency = 30;
                break;
            case R.id.rbtHourly:
                custom = 0;
                lblCustom.setText(R.string.create_custom);
                frequency = 60;
                break;
            case R.id.rbtThreeHour:
                custom = 0;
                lblCustom.setText(R.string.create_custom);
                frequency = 180;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_CUSTOM) {
                custom = data.getExtras().getInt("custom");
                if (custom != 0) {
                    frequency = data.getExtras().getInt("custom");
                    setCustomText(custom);
                    setUnCheckedAllFrequency();
                }
            }
        }

    }

    private void setUnCheckedAllFrequency() {

        rdgFrequency.clearCheck();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (group == rdgFrequency) {
            switch (checkedId) {
                case R.id.rbtFifteenMinute:
                    frequency = 15;
                    break;
                case R.id.rbtThirtyMinute:
                    frequency = 30;
                    break;
                case R.id.rbtHourly:
                    frequency = 60;
                    break;
                case R.id.rbtThreeHour:
                    frequency = 180;
                    break;
                default:
                    break;
            }
            custom = 0;
            lblCustom.setText(R.string.create_custom);

        }
    }
}

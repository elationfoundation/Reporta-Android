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
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * <p> Custom Time picker is displayed to select time in hours and minutes. </p>
 */
public class CheckinCustomHoursFragment extends BaseFragment implements OnClickListener {

    private NumberPicker npHours, npMinute;
    private int custom;
    private Button btnCustomHoursDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            custom = savedInstanceState.getInt("custom");
        } else if (getArguments() != null) {
            custom = getArguments().getInt("custom");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_custom_hours, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        npHours = (NumberPicker) v.findViewById(R.id.npHours);
        npMinute = (NumberPicker) v.findViewById(R.id.npMinute);
        btnCustomHoursDone = (Button) v.findViewById(R.id.btnCustomHoursDone);
        btnCustomHoursDone.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        String[] valuesHour = new String[25];
        String[] valuesMinute = new String[4];
        // String[] valuesMinute = new String[13];

        for (int ij = 0; ij < valuesHour.length; ij++) {

            valuesHour[ij] = ((NumberFormat.getNumberInstance(Locale.getDefault()).format(ij)) + " " + getString(R.string.hours));
        }

        for (int ik = 0; ik < valuesMinute.length; ik++) {

            valuesMinute[ik] = ((NumberFormat.getNumberInstance(Locale.getDefault()).format((ik * 15))) + " " + getString(R.string.minute));
            // valuesMinute[ik] = (ik * 5) + " " + getString(R.string.minute);
        }

        npHours.setMinValue(0);
        npHours.setMaxValue(valuesHour.length - 1);

        npMinute.setMinValue(0);
        npMinute.setMaxValue(valuesMinute.length - 1);

        npHours.setDisplayedValues(valuesHour);
        npMinute.setDisplayedValues(valuesMinute);

        if (custom != 0) {

            npHours.setValue(custom / 60);
            npMinute.setValue((custom % 60) / 15);
            // npMinute.setValue((custom % 60) / 5);

        } else {

            npHours.setValue(0);
            npMinute.setValue(1);
        }

        npHours.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (newVal == 24) {
                    npMinute.setValue(0);
                } else if (newVal == 0 && npMinute.getValue() == 0) {
                    npMinute.setValue(1);
                }
            }
        });

        npMinute.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if (npHours.getValue() == 24) {
                    npMinute.setValue(0);
                } else if (newVal == 0 && npHours.getValue() == 0) {
                    npMinute.setValue(1);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == btnCustomHoursDone) {

            custom = ((npHours.getValue() * 60) + (npMinute.getValue() * 15));
            // custom = ((npHours.getValue() * 60) + (npMinute.getValue() * 5));


            Intent intent = new Intent();

            intent.putExtra("custom", custom);

            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

            getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
        }
    }
}

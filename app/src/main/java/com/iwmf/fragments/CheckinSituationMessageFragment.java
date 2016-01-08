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
import android.widget.EditText;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.utils.KeyboardUtils;

/**
 * <p> Describe situation you are reporting for in this activity.
 * Leave a message about current situation.  </p>
 */
public class CheckinSituationMessageFragment extends BaseFragment implements OnClickListener {

    private EditText edtDescribeSituationMessage;
    private Button btnSituationMessageDone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_checkin_situation, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        edtDescribeSituationMessage = (EditText) v.findViewById(R.id.edtDescribeSituationMessage);
        btnSituationMessageDone = (Button) v.findViewById(R.id.btnSituationMessageDone);
        btnSituationMessageDone.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        edtDescribeSituationMessage.setText(getArguments().getString("value"));
        edtDescribeSituationMessage.setSelection(edtDescribeSituationMessage.getText().toString().length());
    }

    @Override
    public void onClick(View v) {

        if (v == btnSituationMessageDone) {
            onDoneClick();
        }
    }

    public void onDoneClick() {

        KeyboardUtils.hideKeyboard(edtDescribeSituationMessage);
        Intent intent = new Intent();
        intent.putExtra("value", edtDescribeSituationMessage.getText().toString());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}

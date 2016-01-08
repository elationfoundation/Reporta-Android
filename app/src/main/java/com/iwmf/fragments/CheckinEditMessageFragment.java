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
 * <p> Edit and save checkin message. </p>
 */
public class CheckinEditMessageFragment extends BaseFragment implements OnClickListener {

    private EditText edtEmailsuggestedmessage, edtSMSmessage, edtSocialMediamessage;
    private Button btnDone;
    private String email, sms, social;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            email = savedInstanceState.getString("email");
            sms = savedInstanceState.getString("sms");
            social = savedInstanceState.getString("social");
        } else if (getArguments() != null) {
            email = getArguments().getString("email");
            sms = getArguments().getString("sms");
            social = getArguments().getString("social");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_checkin_edit_messge, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        edtEmailsuggestedmessage = (EditText) v.findViewById(R.id.edtEmailsuggestedmessage);
        edtSMSmessage = (EditText) v.findViewById(R.id.edtSMSmessage);
        edtSocialMediamessage = (EditText) v.findViewById(R.id.edtSocialMediamessage);
        btnDone = (Button) v.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        edtEmailsuggestedmessage.setText(email);
        edtSMSmessage.setText(sms);
        edtSocialMediamessage.setText(social);
        edtEmailsuggestedmessage.setSelection(email.length());
    }

    @Override
    public void onClick(View v) {

        if (v == btnDone) {
            KeyboardUtils.hideKeyboard(edtEmailsuggestedmessage);
            Intent intent = new Intent();
            intent.putExtra("email", edtEmailsuggestedmessage.getText().toString());
            intent.putExtra("sms", edtSMSmessage.getText().toString());
            intent.putExtra("social", edtSocialMediamessage.getText().toString());
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
        }
    }

}

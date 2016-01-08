package com.iwmf.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

/**
 * <p> Displays user private circle. </p>
 */
public class Step3PrivateCircleFragment extends BaseFragment implements OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_signup_step3, container, false);

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        Button btnNext = (Button) v.findViewById(R.id.btnNext);
        RelativeLayout rltPrivateCircle = (RelativeLayout) v.findViewById(R.id.rltPrivateCircle);

        rltPrivateCircle.setOnClickListener(this);
        btnNext.setOnClickListener(this);

    }


    private void performNext() {

        if (AccountCreationFragment.jsonRegister != null) {

            if (AddCircleCreateUserFragment.mContacts.size() > 0) {

                boolean is = false;
                for (int i = 0; i < AddCircleCreateUserFragment.mContacts.size(); i++) {
                    if (AddCircleCreateUserFragment.mContacts.get(i).getSos_enabled().equals("2")) {
                        is = true;
                    }
                }

                if (is) {

                    try {

                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, new AccountTermAndConditionFragment()).addToBackStack(AccountTermAndConditionFragment.class.getSimpleName()).commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.displayError(getActivity(), getString(R.string.you_must_select));
                }

            } else {
                Toast.displayMessage(getActivity(), getString(R.string.add_atleast_one_contact));
            }
        }
    }

    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        switch (v.getId()) {

            case R.id.btnNext:

                performNext();

                break;
            case R.id.rltPrivateCircle:

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, new InviteContactsListFragment()).addToBackStack(InviteContactsListFragment.class.getSimpleName()).commit();

                break;
        }
    }


}
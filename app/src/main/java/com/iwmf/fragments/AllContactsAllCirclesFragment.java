package com.iwmf.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iwmf.ContactsActivity;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.utils.SelectedScreen;

/**
 * <p> Display list of all circle and all contact.
 * User can choose from the circle. </p>
 */
public class AllContactsAllCirclesFragment extends BaseFragment implements OnClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_all_contacts_circles, container, false);

        ((ContactsActivity) getActivity()).restoreActionBar(true);

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        TextView lblPrivateCircle = (TextView) v.findViewById(R.id.lblPrivateCircle);
        TextView lblPublicCircle = (TextView) v.findViewById(R.id.lblPublicCircle);
        TextView lblSocialCircle = (TextView) v.findViewById(R.id.lblSocialCircle);
        TextView lblMyContacts = (TextView) v.findViewById(R.id.lblMyContacts);

        lblPrivateCircle.setOnClickListener(this);
        lblPublicCircle.setOnClickListener(this);
        lblSocialCircle.setOnClickListener(this);
        lblMyContacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lblMyContacts:

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, new AllContactsFragment()).addToBackStack(AllContactsFragment.class.getSimpleName()).commit();
                break;

            case R.id.lblPrivateCircle:

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, AddCircleFragment.getInstance(SelectedScreen.PRIVATE, true, "")).addToBackStack(AddCircleFragment.class.getSimpleName()).commit();
                break;

            case R.id.lblPublicCircle:

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, AddCircleFragment.getInstance(SelectedScreen.PUBLIC, true, "")).addToBackStack(AddCircleFragment.class.getSimpleName()).commit();
                break;

            case R.id.lblSocialCircle:

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, SocialCircleFragment.getInstance(true, 0), SocialCircleFragment.class.getSimpleName()).addToBackStack(SocialCircleFragment.class.getSimpleName()).commit();
                break;
        }
    }
}

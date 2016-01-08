package com.iwmf.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.iwmf.R;
import com.iwmf.adapters.InviteContactsAdapter;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Contacts;
import com.iwmf.views.stickylistheaders.StickyListHeadersListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * <p> Invite people from contact.
 * Displays the list of all contacts. </p>
 */
public class InviteContactsListFragment extends BaseFragment {

    private EditText edt_search = null;
    private InviteContactsAdapter mInviteContactsAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_invite_contactlist, container, false);

        mappingWidgets(mView);

        return mView;
    }


    private void mappingWidgets(View v) {

        StickyListHeadersListView listView = (StickyListHeadersListView) v.findViewById(R.id.listView);

        Button btnDone = (Button) v.findViewById(R.id.btnDone);
        edt_search = (EditText) v.findViewById(R.id.edt_search);

        listView.setEmptyView(v.findViewById(R.id.txtNoDataFound));

        if (AddCircleCreateUserFragment.mContacts == null) {
            AddCircleCreateUserFragment.mContacts = new ArrayList<>();
        }

        if (AddCircleCreateUserFragment.mContacts.size() > 0) {

            Collections.sort(AddCircleCreateUserFragment.mContacts, new Comparator<Contacts>() {

                @Override
                public int compare(Contacts o1, Contacts o2) {

                    return o1.getLastname().compareTo(o2.getLastname());
                }
            });

            mInviteContactsAdapter = new InviteContactsAdapter(AddCircleCreateUserFragment.mContacts, getActivity());
            listView.setAdapter(mInviteContactsAdapter);
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                String text = edt_search.getText().toString().toLowerCase(Locale.getDefault());

                mInviteContactsAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });
    }

}
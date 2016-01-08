package com.iwmf.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.adapters.AddressBookAdapter;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.AddressBookContactData;
import com.iwmf.data.Associated_circles;
import com.iwmf.data.Contacts;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Utils;
import com.iwmf.views.stickylistheaders.StickyListHeadersListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * <p> Display contacts from address book to select. </p>
 */
public class ContactAddressBookFragment extends BaseFragment implements OnClickListener {

    private ArrayList<AddressBookContactData> mAddressBookContactDatas = null;

    private Contacts mContacts = null;

    private AddressBookAdapter mTestBaseAdapter = null;
    private EditText edt_search = null;
    private StickyListHeadersListView listView = null;

    private SelectedScreen mSelectedScreen = null;

    public static ContactAddressBookFragment getInstance(SelectedScreen mScreen, Contacts mContacts) {

        ContactAddressBookFragment mFragment = new ContactAddressBookFragment();

        mFragment.mContacts = mContacts;
        mFragment.mSelectedScreen = mScreen;

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dismissProgressDialog();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_contact_address_book, container, false);

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        listView = (StickyListHeadersListView) v.findViewById(R.id.listView);
        listView.setEmptyView(v.findViewById(R.id.txtNoDataFound));
        TextView txtCancel = (TextView) v.findViewById(R.id.txtCancel);
        TextView lblPrivateCircle = (TextView) v.findViewById(R.id.lblPrivateCircle);

        edt_search = (EditText) v.findViewById(R.id.edt_search);

        txtCancel.setOnClickListener(this);
        lblPrivateCircle.setOnClickListener(this);

        readContacts();

        if (mAddressBookContactDatas == null) {
            mAddressBookContactDatas = new ArrayList<>();
        }

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

                findContactFromContactId(mAddressBookContactDatas.get(position).getId());
            }
        });

    }

    private void readContacts() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                ContentResolver cr = getActivity().getContentResolver();

                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                mAddressBookContactDatas = new ArrayList<>();

                try {
                    if (cur != null && cur.getCount() > 0) {
                        cur.moveToFirst();
                        while (cur.moveToNext()) {

                            AddressBookContactData mAddressBookContactData = new AddressBookContactData();

                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String names = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String numbers = "";
                            String emails = "";

                            mAddressBookContactData.setId(id);
                            mAddressBookContactData.setName(names);
                            mAddressBookContactData.setNumber(numbers);
                            mAddressBookContactData.setEmail(emails);

                            mAddressBookContactDatas.add(mAddressBookContactData);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cur != null) {
                        cur.close();
                    }
                }

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (mAddressBookContactDatas == null) {
                            mAddressBookContactDatas = new ArrayList<>();
                        }

                        if (mAddressBookContactDatas.size() > 0) {


                            Collections.sort(mAddressBookContactDatas, new Comparator<AddressBookContactData>() {

                                @Override
                                public int compare(AddressBookContactData o1, AddressBookContactData o2) {

                                    int i = 0;
                                    try {

                                        i = o1.getName().compareTo(o2.getName());

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return i;
                                }
                            });

                            mTestBaseAdapter = new AddressBookAdapter(mAddressBookContactDatas, getActivity());
                            listView.setAdapter(mTestBaseAdapter);

                            edt_search.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void afterTextChanged(Editable arg0) {

                                    String text = edt_search.getText().toString().toLowerCase(Locale.getDefault());
                                    mTestBaseAdapter.filter(text);
                                }

                                @Override
                                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                                }

                                @Override
                                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                                }
                            });
                        }
                        dismissProgressDialog();
                    }
                });
            }
        }).start();
    }

    private void findContactFromContactId(String id) {

        if (Integer.parseInt(id) > 0) {

            if (mContacts == null) {
                mContacts = new Contacts();
            }

            ContentResolver cr = getActivity().getContentResolver();

            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = ?", new String[]{id}, null);

            try {
                if (cur != null && cur.getCount() > 0) {

                    cur.moveToFirst();

                    String names = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String numbers = "";
                    String emails = "";

                    Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (phoneCur != null && phoneCur.moveToNext()) {
                        numbers = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneCur.close();
                    }

                    Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);
                    if (emailCursor != null && emailCursor.moveToNext()) {
                        emails = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        emailCursor.close();
                    }

                    mContacts.setContact_id("");
                    if (mContacts.getAssociated_circles() != null && mContacts.getAssociated_circles().size() > 0) {
                        mContacts.setAssociated_id(mContacts.getAssociated_circles().get(0).getContactlist_id());
                    } else {
                        mContacts.setAssociated_circles(new ArrayList<Associated_circles>());
                        mContacts.setAssociated_id("");
                    }

                    mContacts.setSos_enabled("0");
                    mContacts.setMobile(numbers);
                    mContacts.setEmails(emails);

                    String[] name = names.split(" ");
                    for (int i = 0; i < name.length; i++) {
                        if (i == 0) {
                            mContacts.setFirstname(name[i].trim());

                        } else {
                            mContacts.setLastname((mContacts.getLastname() + " " + name[i]).trim());
                        }
                    }

                    Fragment mFragment = ContactAddManualFragment.getInstance(mContacts, mSelectedScreen, -1);
                    mFragment.setTargetFragment(getTargetFragment(), mSelectedScreen == SelectedScreen.REGISTER ? 2 : 1);

                    getActivity().getSupportFragmentManager().popBackStack(ContactAddressBookFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddManualFragment.class.getSimpleName()).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cur != null) {
                    cur.close();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        switch (v.getId()) {
            case R.id.txtCancel:
                getActivity().onBackPressed();
                break;
        }
    }
}

package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Associated_circles;
import com.iwmf.data.Contacts;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * <p> User can add new circle here. </p>
 */
@SuppressWarnings("ALL")
public class AddCircleCreateUserFragment extends BaseFragment implements OnClickListener {

    public static ArrayList<Contacts> mContacts = null;

    private static String CIRCLE_NAME = "";

    private LinearLayout lnrContactsList = null;
    private TextView txtNoDataFound = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_add_circle_create_user, container, false);

        CIRCLE_NAME = getString(R.string.private_circle_name);

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        TextView lblCircle = (TextView) v.findViewById(R.id.lblCircle);
        TextView txtActiveCircle = (TextView) v.findViewById(R.id.txtActiveCircle);
        TextView lblActiveCircle = (TextView) v.findViewById(R.id.lblActiveCircle);
        TextView txtAddManually = (TextView) v.findViewById(R.id.txtAddManually);
        TextView lblAddManually = (TextView) v.findViewById(R.id.lblAddManually);
        TextView txtAddressbook = (TextView) v.findViewById(R.id.txtAddressbook);
        TextView lblAddressbook = (TextView) v.findViewById(R.id.lblAddressbook);
        txtNoDataFound = (TextView) v.findViewById(R.id.txtNoDataFound);

        lnrContactsList = (LinearLayout) v.findViewById(R.id.lnrContactsList);

        Button btnNext = (Button) v.findViewById(R.id.btnNext);

        RelativeLayout rltActiveCircle = (RelativeLayout) v.findViewById(R.id.rltActiveCircle);
        RelativeLayout rltAddManually = (RelativeLayout) v.findViewById(R.id.rltAddManually);
        RelativeLayout rltAddressbook = (RelativeLayout) v.findViewById(R.id.rltAddressbook);

        rltAddManually.setOnClickListener(this);
        rltAddressbook.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        addContactsToView();

    }

    private void addContactsToView() {

        try {

            if (mContacts == null) {
                mContacts = new ArrayList<>();
            }

            lnrContactsList.removeAllViews();

            if (mContacts.size() > 0) {

                txtNoDataFound.setVisibility(View.GONE);

                Collections.sort(mContacts, new Comparator<Contacts>() {

                    @Override
                    public int compare(Contacts o1, Contacts o2) {

                        return o1.getLastname().compareTo(o2.getLastname());
                    }
                });

                for (int i = 0; i < mContacts.size(); i++) {

                    @SuppressLint("InflateParams") View v = LayoutInflater.from(getActivity()).inflate(R.layout.row_contact_names_with_arrow, null, false);

                    v.setTag(i);

                    TextView txtName = (TextView) v.findViewById(R.id.txtName);
                    ImageView imgvLock = (ImageView) v.findViewById(R.id.imgvLock);

                    try {

                        txtName.setText(mContacts.get(i).getFirstname() + " " + mContacts.get(i).getLastname());

                        if (mContacts.get(i).getSos_enabled().equals("1")) {
                            imgvLock.setImageResource(R.drawable.unlock);
                        } else if (mContacts.get(i).getSos_enabled().equals("2")) {
                            imgvLock.setImageResource(R.drawable.lock_pending);
                        } else {
                            imgvLock.setImageResource(0);
                        }

                        v.setOnLongClickListener(new OnLongClickListener() {

                            @Override
                            public boolean onLongClick(final View view) {

                                final int pos = (Integer) view.getTag();

                                if (mContacts.size() > 1) {

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                    dialog.setMessage(getString(R.string.delete_contact_msg));
                                    dialog.setPositiveButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            mContacts.remove(pos);

                                            addContactsToView();

                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.setNegativeButton(getString(R.string.no), new android.content.DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }

                                return false;
                            }
                        });

                        v.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                int pos = (Integer) view.getTag();

                                ArrayList<Associated_circles> mAssociated_circles = new ArrayList<>();

                                Associated_circles mAss = new Associated_circles();

                                mAss.setCircle("1");

                                mAss.setContactlist_id("");
                                mAss.setListname(CIRCLE_NAME);

                                mAssociated_circles.add(mAss);

                                mContacts.get(pos).setAssociated_circles(mAssociated_circles);

                                mContacts.get(pos).setContact_type("2");

                                ContactAddManualFragment mFragment = ContactAddManualFragment.getInstance(mContacts.get(pos), SelectedScreen.REGISTER, pos);
                                mFragment.setTargetFragment(AddCircleCreateUserFragment.this, 2);

                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddManualFragment.class.getSimpleName()).commit();

                            }
                        });

                        lnrContactsList.addView(v);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                txtNoDataFound.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performNext() {

        if (AccountCreationFragment.jsonRegister != null) {

            if (mContacts.size() > 0) {

                try {

                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_LIST_ID, null);
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_CIRCLE, "1");
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_LISTNAME, CIRCLE_NAME);
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_DELETE_CONTACT, "");
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_DEFAULTSTATUS, "1");

                    JsonArray j = new GsonBuilder().create().toJsonTree(mContacts).getAsJsonArray();

                    JSONArray js = new JSONArray(j.toString());

                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_CONTACTS, js);

                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, new Step3PrivateCircleFragment()).addToBackStack(Step3PrivateCircleFragment.class.getSimpleName()).commit();

                } catch (Exception e) {
                    e.printStackTrace();
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

            case R.id.rltAddressbook:

                Contacts mContactsTemp = new Contacts();

                ArrayList<Associated_circles> mAssociated_circles = new ArrayList<>();

                Associated_circles mAss = new Associated_circles();

                mAss.setCircle("1");

                mAss.setContactlist_id("");
                mAss.setListname(CIRCLE_NAME);

                mAssociated_circles.add(mAss);

                mContactsTemp.setSos_enabled("0");
                mContactsTemp.setAssociated_circles(mAssociated_circles);

                Fragment mFragment = ContactAddressBookFragment.getInstance(SelectedScreen.REGISTER, mContactsTemp);
                mFragment.setTargetFragment(AddCircleCreateUserFragment.this, 2);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddressBookFragment.class.getSimpleName()).commit();

                break;

            case R.id.rltAddManually:

                mContactsTemp = new Contacts();

                mAssociated_circles = new ArrayList<>();

                mAss = new Associated_circles();

                mAss.setCircle("1");

                mAss.setContactlist_id("");
                mAss.setListname(CIRCLE_NAME);

                mAssociated_circles.add(mAss);

                mContactsTemp.setSos_enabled("0");
                mContactsTemp.setAssociated_circles(mAssociated_circles);

                mFragment = ContactAddManualFragment.getInstance(mContactsTemp, SelectedScreen.REGISTER, -1);
                mFragment.setTargetFragment(AddCircleCreateUserFragment.this, 2);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddManualFragment.class.getSimpleName()).commit();

                break;

            case R.id.btnNext:

                performNext();

                break;

            case R.id.imgvInfo:

                showMessage(getString(R.string.about_friend_unlock_title), getString(R.string.youmustenableapplock));

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            addContactsToView();
        }
    }
}
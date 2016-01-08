package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Associated_circles;
import com.iwmf.data.Contacts;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p> User can manually add new contact to his/her circle. </p>
 */
@SuppressWarnings("ALL")
public class ContactAddManualFragment extends BaseFragment implements OnClickListener {

    public static Contacts mContacts = null;
    private TextView txtEnableLock = null;
    private EditText edtFirstName = null, edtLastName = null, edtTemp = null;
    private LinearLayout lnrPhone = null, lnrEmail = null, lnrCircles = null;
    private ImageView imgvLock = null;
    private CheckBox chkItem = null;
    private View mViewTemp = null;
    private int intEmail = 0, intPhone = 0;
    private HashMap<String, View> mHashMapPhone = null;
    private HashMap<String, View> mHashMapEmail = null;
    private HashMap<String, View> mHashMapAssociated = null;
    private SelectedScreen mSelectedScreen = null;
    private int selectedIndex = -1;

    public static ContactAddManualFragment getInstance(Contacts mContacts, SelectedScreen mSelectedScreen, int selectedIndex) {

        ContactAddManualFragment mFragment = new ContactAddManualFragment();

        mFragment.mSelectedScreen = mSelectedScreen;

        mFragment.selectedIndex = selectedIndex;

        if (mContacts == null) {

            ContactAddManualFragment.mContacts = new Contacts();
            ContactAddManualFragment.mContacts.setAssociated_circles(new ArrayList<Associated_circles>());
            ContactAddManualFragment.mContacts.setSos_enabled("0");

        } else {

            ContactAddManualFragment.mContacts = mContacts;
        }

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_contact_add_manual, container, false);

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        TextView txtSave = (TextView) v.findViewById(R.id.txtSave);
        TextView txtCancel = (TextView) v.findViewById(R.id.txtCancel);
        TextView txtFullName = (TextView) v.findViewById(R.id.txtFullName);
        TextView txtAdd = (TextView) v.findViewById(R.id.txtAdd);
        txtEnableLock = (TextView) v.findViewById(R.id.txtEnableLock);

        edtFirstName = (EditText) v.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) v.findViewById(R.id.edtLastName);

        imgvLock = (ImageView) v.findViewById(R.id.imgvLock);
        ImageView imgvInfo = (ImageView) v.findViewById(R.id.imgvInfo);

        RelativeLayout rltSOS = (RelativeLayout) v.findViewById(R.id.rltSOS);
        RelativeLayout rltEmail = (RelativeLayout) v.findViewById(R.id.rltEmail);
        RelativeLayout rltPhone = (RelativeLayout) v.findViewById(R.id.rltPhone);

        lnrPhone = (LinearLayout) v.findViewById(R.id.lnrPhone);
        lnrEmail = (LinearLayout) v.findViewById(R.id.lnrEmail);
        lnrCircles = (LinearLayout) v.findViewById(R.id.lnrCircles);

        mHashMapPhone = new HashMap<>();
        mHashMapEmail = new HashMap<>();
        mHashMapAssociated = new HashMap<>();

        txtAdd.setOnClickListener(this);
        rltEmail.setOnClickListener(this);
        rltPhone.setOnClickListener(this);
        imgvLock.setOnClickListener(this);
        imgvInfo.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtSave.setOnClickListener(this);

        if (mSelectedScreen == SelectedScreen.REGISTER) {
            txtAdd.setVisibility(View.GONE);
        } else {
            txtAdd.setVisibility(View.VISIBLE);
        }

        txtFullName.setText(mContacts.getFirstname() + " " + mContacts.getLastname());
        edtFirstName.setText(mContacts.getFirstname());
        edtLastName.setText(mContacts.getLastname());

        if ((mContacts.getContact_type().equals("") && mSelectedScreen == SelectedScreen.PUBLIC) || mContacts.getContact_type().equals("2")) {
            rltSOS.setVisibility(View.GONE);

        } else {

            rltSOS.setVisibility(View.VISIBLE);
        }

        switch (mContacts.getSos_enabled()) {
            case "0":

                imgvLock.setImageResource(R.drawable.unlock);
                txtEnableLock.setText(getString(R.string.enable_friend_unlock));

                break;
            case "1":

                imgvLock.setImageResource(R.drawable.lock);
                txtEnableLock.setText(getString(R.string.disable_friend_unlock));

                break;
            default:

                imgvLock.setImageResource(R.drawable.lock_pending);
                txtEnableLock.setText(getString(R.string.pending_friend_unlock));
                break;
        }

        if (!Utils.isEmpty(mContacts.getMobile())) {
            if (mContacts.getMobile().contains(",")) {

                String[] phones = mContacts.getMobile().split(",");

                for (String phone : phones) {

                    addEdtRow("phone", phone);
                }
            } else {
                addEdtRow("phone", mContacts.getMobile());
            }
        }

        if (!Utils.isEmpty(mContacts.getEmails())) {
            if (mContacts.getEmails().contains(",")) {

                String[] emails = mContacts.getEmails().split(",");

                for (String email : emails) {

                    addEdtRow("email", email);
                }
            } else {
                addEdtRow("email", mContacts.getEmails());
            }
        }

        addCircleRows(mContacts.getAssociated_circles());
    }

    @SuppressLint("InflateParams")
    private void addCircleRows(ArrayList<Associated_circles> mAssociated_circles) {

        if (lnrCircles != null) {
            lnrCircles.removeAllViews();
        }

        if (mAssociated_circles == null) {
            mAssociated_circles = new ArrayList<>();
        }

        for (int i = 0; i < mAssociated_circles.size(); i++) {

            mViewTemp = getActivity().getLayoutInflater().inflate(R.layout.row_contact_list_names_with_arrow, null, false);

            chkItem = (CheckBox) mViewTemp.findViewById(R.id.chkItem);
            TextView txtName = (TextView) mViewTemp.findViewById(R.id.txtName);
            TextView txtCircle = (TextView) mViewTemp.findViewById(R.id.txtCircle);

            mViewTemp.setTag(mAssociated_circles.get(i).getContactlist_id());
            txtName.setTag(mAssociated_circles.get(i).getContactlist_id());
            txtCircle.setTag(mAssociated_circles.get(i).getContactlist_id());
            chkItem.setTag(mAssociated_circles.get(i).getContactlist_id());

            txtName.setText(mAssociated_circles.get(i).getListname());
            txtCircle.setText(mAssociated_circles.get(i).getCircle().equals("1") ? getString(R.string.private_) : getString(R.string.public_));

            lnrCircles.addView(mViewTemp);

            mHashMapAssociated.put(mAssociated_circles.get(i).getContactlist_id(), mViewTemp);

            chkItem.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    chkItem.setChecked(true);

                    if (mSelectedScreen != SelectedScreen.REGISTER) {

                        lnrCircles.removeView(mHashMapAssociated.get(v.getTag().toString()));
                        mHashMapAssociated.remove(v.getTag().toString());
                    }

                }
            });
        }
    }

    @SuppressLint("InflateParams")
    private View addEdtRow(String who, String text) {

        if (who.equals("phone")) {
            mViewTemp = getActivity().getLayoutInflater().inflate(R.layout.row_phone_edt, null, false);
        } else {
            mViewTemp = getActivity().getLayoutInflater().inflate(R.layout.row_email_edt, null, false);
        }

        edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);
        ImageView imgvRemove = (ImageView) mViewTemp.findViewById(R.id.imgvRemove);

        edtTemp.setText(text);

        if (who.equals("phone")) {

            intPhone++;
            mViewTemp.setTag(who + intPhone);
            edtTemp.setTag(who + intPhone);
            imgvRemove.setTag(who + intPhone);

            mHashMapPhone.put(who + intPhone, mViewTemp);
            lnrPhone.addView(mViewTemp);

        } else {

            intEmail++;
            mViewTemp.setTag(who + intEmail);
            edtTemp.setTag(who + intEmail);
            imgvRemove.setTag(who + intEmail);

            mHashMapEmail.put(who + intEmail, mViewTemp);
            lnrEmail.addView(mViewTemp);

        }

        imgvRemove.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getTag().toString().startsWith("phone")) {

                    lnrPhone.removeView(mHashMapPhone.get(v.getTag().toString()));
                    mHashMapPhone.remove(v.getTag().toString());

                } else if (v.getTag().toString().startsWith("email")) {

                    lnrEmail.removeView(mHashMapEmail.get(v.getTag().toString()));
                    mHashMapEmail.remove(v.getTag().toString());
                }
            }
        });

        return mViewTemp;
    }

    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        switch (v.getId()) {
            case R.id.txtAdd:

                String associated_id = "";

                for (String key : mHashMapAssociated.keySet()) {
                    mViewTemp = mHashMapAssociated.get(key);
                    if (associated_id.isEmpty()) {
                        associated_id = mViewTemp.getTag().toString();
                    } else {
                        associated_id += "," + mViewTemp.getTag().toString();
                    }
                }

                Fragment mFragment = CircleListFragment.getInstance(mContacts.getContact_id(), associated_id);
                mFragment.setTargetFragment(this, SelectedScreen.REGISTER == mSelectedScreen ? 2 : 1);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(CircleListFragment.class.getSimpleName()).commit();

                break;
            case R.id.rltEmail:

                addEdtRow("email", "");

                break;
            case R.id.rltPhone:

                addEdtRow("phone", "");

                break;
            case R.id.imgvLock:

                if (mContacts.getSos_enabled().equals("1")) {

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            dismissProgressDialog();

                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                            dialog.setMessage(getString(R.string.are_you_sure));
                            dialog.setPositiveButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    imgvLock.setImageResource(R.drawable.unlock);
                                    txtEnableLock.setText(getString(R.string.enable_friend_unlock));
                                    mContacts.setSos_enabled("0");

                                    dialog.dismiss();
                                }
                            });

                            dialog.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });

                } else if (mContacts.getSos_enabled().equals("2") || mContacts.getSos_enabled().equals("1")) {
                    imgvLock.setImageResource(R.drawable.unlock);
                    txtEnableLock.setText(getString(R.string.enable_friend_unlock));
                    mContacts.setSos_enabled("0");
                } else {
                    imgvLock.setImageResource(R.drawable.lock_pending);
                    txtEnableLock.setText(getString(R.string.pending_friend_unlock));
                    mContacts.setSos_enabled("2");
                }

                break;
            case R.id.imgvInfo:

                showMessage(getString(R.string.about_friend_unlock_title), getString(R.string.youmustenableapplock));

                break;
            case R.id.txtCancel:

                getActivity().onBackPressed();

                break;
            case R.id.txtSave:

                if (checkValidation()) {

                    if (SelectedScreen.REGISTER == mSelectedScreen) {

                        String numbers = "";
                        String emails = "";

                        for (String key : mHashMapPhone.keySet()) {
                            mViewTemp = mHashMapPhone.get(key);
                            edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);
                            if (numbers.isEmpty()) {
                                numbers = edtTemp.getText().toString();
                            } else {
                                numbers += "," + edtTemp.getText().toString();
                            }
                        }

                        for (String key : mHashMapEmail.keySet()) {
                            mViewTemp = mHashMapEmail.get(key);
                            edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);
                            if (emails.isEmpty()) {
                                emails = edtTemp.getText().toString();
                            } else {
                                emails += "," + edtTemp.getText().toString();
                            }
                        }

                        Contacts mContactsTemp = new Contacts();

                        mContactsTemp.setAssociated_circles(mContactsTemp.getAssociated_circles());
                        mContactsTemp.setAssociated_id("");
                        mContactsTemp.setContact_id("");
                        mContactsTemp.setEmails(emails);
                        mContactsTemp.setMobile(numbers);
                        mContactsTemp.setFirstname(edtFirstName.getText().toString());
                        mContactsTemp.setLastname(edtLastName.getText().toString());
                        mContactsTemp.setSos_enabled(mContacts.getSos_enabled());

                        if (AddCircleCreateUserFragment.mContacts == null) {
                            AddCircleCreateUserFragment.mContacts = new ArrayList<>();
                        }

                        String name1 = edtFirstName.getText().toString() + " " + edtLastName.getText().toString();
                        String name2 = edtLastName.getText().toString() + " " + edtFirstName.getText().toString();

                        String name11;
                        String name22;

                        for (int i = 0; i < AddCircleCreateUserFragment.mContacts.size(); i++) {

                            if (i != selectedIndex) {

                                name11 = AddCircleCreateUserFragment.mContacts.get(i).getFirstname() + " " + AddCircleCreateUserFragment.mContacts.get(i).getLastname();
                                name22 = AddCircleCreateUserFragment.mContacts.get(i).getLastname() + " " + AddCircleCreateUserFragment.mContacts.get(i).getFirstname();

                                if (name1.equalsIgnoreCase(name11) || name1.equalsIgnoreCase(name22) || name2.equalsIgnoreCase(name11) || name2.equalsIgnoreCase(name22)) {

                                    Toast.displayError(getActivity(), getString(R.string.duplicate_name));
                                    return;

                                } else if (AddCircleCreateUserFragment.mContacts.get(i).getEmails().contains(emails)) {

                                    Toast.displayError(getActivity(), getString(R.string.duplicate_email));
                                    return;

                                } else if (AddCircleCreateUserFragment.mContacts.get(i).getMobile().contains(numbers)) {

                                    Toast.displayError(getActivity(), getString(R.string.duplicate_number));
                                    return;
                                }
                            }
                        }

                        AddCircleCreateUserFragment.mContacts.add(mContactsTemp);

                        if (selectedIndex > -1) {
                            AddCircleCreateUserFragment.mContacts.remove(selectedIndex);
                        }

                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(2, Activity.RESULT_OK, null);
                        }

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                        dialog.setTitle(getString(R.string.contactadded));

                        dialog.setMessage(getString(R.string.addanother));
                        dialog.setPositiveButton(getString(R.string.add), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getActivity().onBackPressed();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton(getString(R.string.no), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                performNextStep3();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    } else {
                        performDone();
                    }

                }

                break;

        }
    }

    private void performNextStep3() {

        if (AccountCreationFragment.jsonRegister != null) {

            if (AddCircleCreateUserFragment.mContacts.size() > 0) {

                try {

                    String CIRCLE_NAME = getString(R.string.private_circle_name);

                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_LIST_ID, null);
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_CIRCLE, "1");
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_LISTNAME, CIRCLE_NAME);
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_DELETE_CONTACT, "");
                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_DEFAULTSTATUS, "1");

                    JsonArray j = new GsonBuilder().create().toJsonTree(AddCircleCreateUserFragment.mContacts).getAsJsonArray();

                    JSONArray js = new JSONArray(j.toString());

                    AccountCreationFragment.jsonRegister.put(PARAMS.TAG_CONTACTS, js);


                    getActivity().onBackPressed();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            addCircleRows(mContacts.getAssociated_circles());
        }
    }

    private boolean checkValidation() {

        if (edtFirstName.getText().toString().trim().length() == 0) {

            edtFirstName.setError(getString(R.string.first_name_mandatory));
            edtFirstName.requestFocus();

            return false;

        } else if (edtLastName.getText().toString().trim().length() == 0) {

            edtLastName.setError(getString(R.string.last_name_mandatory));
            edtLastName.requestFocus();

            return false;

        } else if (mHashMapEmail.size() == 0) {

            Toast.displayError(getActivity(), getString(R.string.please_enter_email));
            return false;

        } else if (mHashMapPhone.size() == 0) {

            Toast.displayError(getActivity(), getString(R.string.please_enter_phone));
            return false;

        } else if (!checkEmailValidation()) {

            return false;

        } else if (!checkPhoneValidation()) {

            return false;
        } else if (mHashMapAssociated.size() <= 0) {
            Toast.displayError(getActivity(), getString(R.string.please_select_partof_circle));
            return false;
        }

        return true;
    }

    private boolean checkEmailValidation() {

        for (String key : mHashMapEmail.keySet()) {
            mViewTemp = mHashMapEmail.get(key);
            edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);

            if (!Utils.isValidEmail(edtTemp.getText().toString())) {
                edtTemp.setError(getString(R.string.enter_valid_email));
                edtTemp.requestFocus();
                return false;
            }
        }

        return true;
    }

    private boolean checkPhoneValidation() {

        for (String key : mHashMapPhone.keySet()) {
            mViewTemp = mHashMapPhone.get(key);
            edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);

            if (!android.util.Patterns.PHONE.matcher(edtTemp.getText().toString()).matches()) {
                edtTemp.setError(getString(R.string.invalid_phone));
                edtTemp.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void performDone() {

        displayProgressDialog();

        String numbers = "";
        String emails = "";
        String associated_id = "";

        for (String key : mHashMapPhone.keySet()) {
            mViewTemp = mHashMapPhone.get(key);
            edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);
            if (numbers.isEmpty()) {
                numbers = edtTemp.getText().toString();
            } else {
                numbers += "," + edtTemp.getText().toString();
            }
        }

        for (String key : mHashMapEmail.keySet()) {
            mViewTemp = mHashMapEmail.get(key);
            edtTemp = (EditText) mViewTemp.findViewById(R.id.edt);
            if (emails.isEmpty()) {
                emails = edtTemp.getText().toString();
            } else {
                emails += "," + edtTemp.getText().toString();
            }
        }

        for (String key : mHashMapAssociated.keySet()) {
            mViewTemp = mHashMapAssociated.get(key);
            if (associated_id.isEmpty()) {
                associated_id = mViewTemp.getTag().toString();
            } else {
                associated_id += "," + mViewTemp.getTag().toString();
            }
        }

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CREATESINGLECONTACT, RequestMethod.POST, RequestBuilder.getCreateSingleContactRequest(mContacts.getContact_id(), edtFirstName.getText().toString().trim(), edtLastName.getText().toString().trim(), numbers, emails, mContacts.getSos_enabled(), associated_id));

        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                dismissProgressDialog();

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("status") && jsonObject.has("message")) {

                        if (jsonObject.get("status").toString().equals("1")) {

                            Toast.displayMessage(getActivity(), jsonObject.getString("message"));

                            if (getTargetFragment() != null) {
                                getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                            }

                            getActivity().onBackPressed();


                        } else if (jsonObject.get("status").toString().equals("3")) {

                            ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                        } else if (jsonObject.get("status").toString().equals("2")) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                            if (getTargetFragment() != null) {
                                getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                            }

                            dialog.setMessage(jsonObject.getString("message"));

                            dialog.setPositiveButton(getString(R.string.addnow), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });

                            dialog.setNegativeButton((getString(R.string.cancel)), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    getActivity().onBackPressed();
                                }
                            });

                            dialog.show();

                        } else {

                            Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
                        }
                    } else {
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                }
            }

            @Override
            public void onError(String error) {

                dismissProgressDialog();
                Toast.displayError(getActivity(), getString(R.string.try_later));

            }
        });

        mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

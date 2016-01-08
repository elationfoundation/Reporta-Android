package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Logindata;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p> User can edit profile here also it has an option to change user password.
 * Saves user updated profile. </p>
 */
public class ProfileEditFragment extends BaseFragment implements OnClickListener {

    private final static int INTENT_SELECT_JOB_TITLE = 2;
    private final static int INTENT_SELECT_COUNTRY_ORIGIN = 3;
    private final static int INTENT_SELECT_COUNTRY_WORKING = 4;
    private CryptoManager prefManager = null;
    private Logindata mLogindata = null;
    private EditText edtEmail, edtFirstName, edtLastName, edtPhone, edtPassword, edtRePassword, edtAffiliation;
    private CheckBox chkFreelancer;
    private TextView lblChoose, txtFullname, lblAfghanistan, lblAfghanistan2;
    private String Username;
    private String selectjobtitle = "";
    private String selectcountryorigin = "";
    private String selectcountryworking = "";
    private String selectcountryorigin_code = "";
    private String selectcountryworking_code = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        prefManager = CryptoManager.getInstance(getActivity());
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        Button btnUpdate = (Button) v.findViewById(R.id.btnUpdate);
        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);

        RelativeLayout rlt1Passowrd = (RelativeLayout) v.findViewById(R.id.rlt1Passowrd);

        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        txtFullname = (TextView) v.findViewById(R.id.txtFullname);
        edtFirstName = (EditText) v.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) v.findViewById(R.id.edtLastName);
        edtPhone = (EditText) v.findViewById(R.id.edtPhone);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        edtRePassword = (EditText) v.findViewById(R.id.edtRePassword);
        edtAffiliation = (EditText) v.findViewById(R.id.edtAffiliation);
        chkFreelancer = (CheckBox) v.findViewById(R.id.chkFreelancer);
        lblAfghanistan = (TextView) v.findViewById(R.id.lblAfghanistan);
        lblAfghanistan2 = (TextView) v.findViewById(R.id.lblAfghanistan2);
        lblChoose = (TextView) v.findViewById(R.id.lblChoose);

        rlt1Passowrd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        chkFreelancer.setOnClickListener(this);
        lblChoose.setOnClickListener(this);
        lblAfghanistan.setOnClickListener(this);
        lblAfghanistan2.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        try {
            mLogindata = new Gson().fromJson(CryptoManager.getInstance(getActivity()).getPrefs().getString(PARAMS.KEY_PROFILE_DATA, ""), Logindata.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mLogindata != null) {

            txtFullname.setText(mLogindata.getFirstname() + " " + mLogindata.getLastname());
            edtFirstName.setText(mLogindata.getFirstname());
            edtLastName.setText(mLogindata.getLastname());
            edtEmail.setText(mLogindata.getEmail());
            edtPhone.setText(mLogindata.getPhone());
            lblChoose.setText(mLogindata.getJobtitle());
            edtAffiliation.setText(mLogindata.getAffiliation_id());
            Username = mLogindata.getUsername();
            String check = mLogindata.getFreelancer();

            if (check.equals("1")) {

                chkFreelancer.setChecked(true);

            } else {

                chkFreelancer.setChecked(false);
            }

            InputStream mInputStream;
            mInputStream = getInputStream();

            JSONArray mJsonArray = null;

            try {

                String str = IOUtils.toString(mInputStream, "utf-8");
                mJsonArray = new JSONArray(str);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            String countryOrigin = "", countryWorking = "";

            if (mJsonArray == null) {
                mJsonArray = new JSONArray();
            }

            for (int i = 0; i < mJsonArray.length(); i++) {
                try {

                    if (mLogindata.getOrigin_country().equalsIgnoreCase(mJsonArray.getJSONObject(i).getString("Code")) || mLogindata.getOrigin_country().equalsIgnoreCase(mJsonArray.getJSONObject(i).getString("Name"))) {

                        countryOrigin = mJsonArray.getJSONObject(i).getString("Name");
                        selectcountryorigin_code = mJsonArray.getJSONObject(i).getString("Code");

                    } else if (mLogindata.getWorking_country().equalsIgnoreCase(mJsonArray.getJSONObject(i).getString("Code")) || mLogindata.getWorking_country().equalsIgnoreCase(mJsonArray.getJSONObject(i).getString("Name"))) {

                        countryWorking = mJsonArray.getJSONObject(i).getString("Name");
                        selectcountryworking_code = mJsonArray.getJSONObject(i).getString("Code");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            lblAfghanistan.setText(countryOrigin);
            selectcountryorigin = countryOrigin;
            lblAfghanistan2.setText(countryWorking);
            selectcountryworking = countryWorking;

        }

    }

    private InputStream getInputStream() {

        if (ConstantData.LANGUAGE_CODE.equals(Language_code.EN.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_en);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.AR.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_ar);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.FR.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_fr);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.IW.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_iw);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.ES.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_es);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.TR.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_tr);

        } else {

            return getResources().openRawResource(R.raw.countrylist_json_new_en);
        }
    }

    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
        switch (v.getId()) {

            case R.id.lblChoose:

                Bundle bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectjobtitle));
                bundle.putString("selectjobtitle", selectjobtitle);

                SelectAccountDataFragment fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(ProfileEditFragment.this, INTENT_SELECT_JOB_TITLE);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;
            case R.id.lblAfghanistan:

                bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectcountryorigin));
                bundle.putString("selectcountryorigin", selectcountryorigin);

                fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(ProfileEditFragment.this, INTENT_SELECT_COUNTRY_ORIGIN);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;
            case R.id.lblAfghanistan2:

                bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectcountryworking));
                bundle.putString("selectcountryworking", selectcountryworking);

                fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(ProfileEditFragment.this, INTENT_SELECT_COUNTRY_WORKING);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;
            case R.id.btnUpdate:

                if (validation()) {
                    getUpdate();
                }

                break;
            case R.id.btnCancel:

                getActivity().onBackPressed();

                break;

            case R.id.rlt1Passowrd:

                showUpdatePasswordDialog();

                break;
        }
    }

    private void showUpdatePasswordDialog() {

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.change_password, null, false);

        final EditText edtCurrent = (EditText) view.findViewById(R.id.edtCurrent);
        final EditText edtNew = (EditText) view.findViewById(R.id.edtNew);
        final EditText edtConfirm = (EditText) view.findViewById(R.id.edtConfirm);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.change_password).setCancelable(false).setNegativeButton(R.string.cancel, null).setPositiveButton(android.R.string.ok, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (checkPasswordValidation(edtCurrent, edtNew, edtConfirm)) {

                            dialog.dismiss();

                            updatePassword(edtCurrent.getText().toString().trim(), edtConfirm.getText().toString().trim());
                        }
                    }
                });
            }
        });
        dialog.show();

    }

    private void updatePassword(String oldPassword, String newPassword) {
        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_RESETPASSWORD, RequestMethod.POST, RequestBuilder.getResetPasswordRequest(oldPassword, newPassword));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();


                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                showMessage("", jsonObject.get("message").toString());
                            }
                        } else {

                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }

                    } catch (Exception e) {
                        dismissProgressDialog();
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

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private boolean checkPasswordValidation(EditText edtCurrent, EditText edtNew, EditText edtConfirm) {
        if (edtCurrent.getText().toString().trim().length() <= 0) {

            edtCurrent.setError(getString(R.string.invalid_password));
            edtCurrent.requestFocus();

            return false;

        } else if (!Utils.isValidStrongPassword(edtNew.getText().toString().trim(), ConstantData.LANGUAGE_CODE, mLogindata.getUsername())) {

            edtNew.setError(getString(R.string.invalid_password));
            edtNew.requestFocus();

            return false;

        } else if (!(edtNew.getText().toString().trim().equals(edtConfirm.getText().toString().trim()))) {

            edtNew.setError(getString(R.string.password_notmatch));
            edtNew.requestFocus();

            return false;
        }

        return true;
    }

    private boolean validation() {

        if (Utils.isEmpty(edtFirstName.getText().toString().trim())) {
            edtFirstName.setError(getString(R.string.invalid_firstname));
            edtFirstName.requestFocus();
            return false;

        } else if (Utils.isEmpty(edtLastName.getText().toString().trim())) {
            edtLastName.setError(getString(R.string.invalid_lastname));
            edtLastName.requestFocus();
            return false;

        } else if (!Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            edtEmail.setError(getString(R.string.enter_valid_email));
            edtEmail.requestFocus();
            return false;

        } else if (Utils.isEmpty(edtPhone.getText().toString().trim()) || !android.util.Patterns.PHONE.matcher(edtPhone.getText().toString().trim()).matches()) {
            edtPhone.setError(getString(R.string.invalid_phone));
            edtPhone.requestFocus();
            return false;

        } else {

            return checkPassword();

        }

    }

    private boolean checkPassword() {

        if (edtPassword.getText().toString().trim().length() > 0) {
            if (!Utils.isValidPassword(edtPassword.getText().toString())) {
                edtPassword.setError(getString(R.string.invalid_password));
                edtPassword.requestFocus();
                return false;

            } else if (Utils.isEmpty(edtRePassword.getText().toString())) {
                edtRePassword.setError(getString(R.string.invalid_password));
                edtRePassword.requestFocus();
                return false;

            } else if (!(edtPassword.getText().toString().trim().equals(edtRePassword.getText().toString().trim()))) {
                edtRePassword.setError(getString(R.string.password_notmatch));
                edtRePassword.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void getUpdate() {

        try {
            displayProgressDialog();
            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CREATEUSER, RequestMethod.POST, RequestBuilder.getCreateUserRequest(Username, edtPassword.getText().toString(), edtEmail.getText().toString(), edtFirstName.getText().toString(), edtLastName.getText().toString(), ConstantData.LANGUAGE, ConstantData.LANGUAGE_CODE, edtPhone.getText().toString(), lblChoose.getText().toString(), edtAffiliation.getText().toString(), chkFreelancer.isChecked() ? "1" : "0", selectcountryorigin_code, selectcountryworking_code, ConstantData.REGISTRATIONID, ConstantData.DEVICE_TYPE_ANDROID, mLogindata.getGender(), mLogindata.getGender_type()));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                Toast.displayMessage(getActivity(), jsonObject.get("message").toString());

                                ConstantData.LANGUAGE_CODE = jsonObject.getJSONObject("data").getString(PARAMS.TAG_LANGUAGE_CODE);
                                ConstantData.LANGUAGE = jsonObject.getJSONObject("data").getString(PARAMS.TAG_LANGUAGE);

                                Utils.changeLocale(ConstantData.LANGUAGE_CODE, getActivity());

                                SharedPreferences.Editor editor = prefManager.getPrefs().edit();
                                editor.putString(PARAMS.KEY_PROFILE_DATA, jsonObject.get("data").toString());

                                editor.apply();

                                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);
                                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE, ConstantData.LANGUAGE).apply();
                                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, ConstantData.LANGUAGE_CODE).apply();

                                if (getTargetFragment() != null) {
                                    getTargetFragment().onActivityResult(4, Activity.RESULT_OK, null);
                                }

                                getFragmentManager().popBackStack();

                            } else if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
                            }

                        } else {
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }

                        dismissProgressDialog();

                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_SELECT_JOB_TITLE && resultCode == Activity.RESULT_OK) {

            selectjobtitle = data.getStringExtra("selectjobtitle");
            lblChoose.setText(selectjobtitle);

        } else if (requestCode == INTENT_SELECT_COUNTRY_ORIGIN && resultCode == Activity.RESULT_OK) {

            selectcountryorigin = data.getStringExtra("selectcountryorigin");

            selectcountryorigin_code = data.getStringExtra("selectcountryorigin_code");

            lblAfghanistan.setText(selectcountryorigin);

        } else if (requestCode == INTENT_SELECT_COUNTRY_WORKING && resultCode == Activity.RESULT_OK) {

            selectcountryworking = data.getStringExtra("selectcountryworking");

            selectcountryworking_code = data.getStringExtra("selectcountryworking_code");

            lblAfghanistan2.setText(selectcountryworking);
        }
    }
}
package com.iwmf.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.iwmf.AccountCreationActivity;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <p> Fragment for creating a new user account.
 * This handles all user inputs and account creation process. Also validation and handles server side responses.
 * </p>
 */
@SuppressWarnings("ALL")
public class AccountCreationFragment extends BaseFragment implements OnClickListener {

    private final static int INTENT_SELECT_LANGUAGE = 1;
    private final static int INTENT_SELECT_JOB_TITLE = 2;
    private final static int INTENT_SELECT_COUNTRY_ORIGIN = 3;
    private final static int INTENT_SELECT_COUNTRY_WORKING = 4;
    public static JSONObject jsonRegister = null;
    private SharedPreferences sharedpreferences = null;
    private EditText edtUserName, edtEmail, edtReEnterEmail, edtFirstName, edtLastName, edtPhone, edtPassword, edtRePassword, edtAffiliation, edtPlzDesc;
    private CheckBox chkFreelancer = null, chkMale = null, chkFemale = null, chkOther = null;
    private TextView lblEnglish;
    private TextView lblChoose;
    private TextView lblAfghanistan;
    private TextView lblAfghanistan2;
    private String selectjobtitle = "";
    private String selectcountryorigin = "";
    private String selectcountryworking = "";
    private String selectcountryorigin_code = "";
    private String selectcountryworking_code = "";
    private String selectdata = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);

        AddCircleCreateUserFragment.mContacts = null;
        AddCircleCreateUserFragment.mContacts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_account_creation, container, false);
        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        edtUserName = (EditText) v.findViewById(R.id.edtUserName);
        edtEmail = (EditText) v.findViewById(R.id.edtEmail);
        edtReEnterEmail = (EditText) v.findViewById(R.id.edtReEnterEmail);
        edtFirstName = (EditText) v.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) v.findViewById(R.id.edtLastName);
        edtPhone = (EditText) v.findViewById(R.id.edtPhone);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        edtRePassword = (EditText) v.findViewById(R.id.edtRePassword);
        edtAffiliation = (EditText) v.findViewById(R.id.edtAffiliation);
        edtPlzDesc = (EditText) v.findViewById(R.id.edtPlzDesc);

        Button btnNext = (Button) v.findViewById(R.id.btnNext);

        chkFreelancer = (CheckBox) v.findViewById(R.id.chkFreelancer);
        chkMale = (CheckBox) v.findViewById(R.id.chkMale);
        chkFemale = (CheckBox) v.findViewById(R.id.chkFemale);
        chkOther = (CheckBox) v.findViewById(R.id.chkOther);

        TextView lblProfesionalDetails = (TextView) v.findViewById(R.id.lblProfesionalDetails);
        TextView lblSelectLanguage = (TextView) v.findViewById(R.id.lblSelectLanguage);
        lblEnglish = (TextView) v.findViewById(R.id.lblEnglish);
        TextView lblHeaderJobTitle = (TextView) v.findViewById(R.id.lblHeaderJobTitle);
        lblChoose = (TextView) v.findViewById(R.id.lblChoose);
        TextView lblCountryofOrigin = (TextView) v.findViewById(R.id.lblCountryofOrigin);
        lblAfghanistan = (TextView) v.findViewById(R.id.lblAfghanistan);
        lblAfghanistan2 = (TextView) v.findViewById(R.id.lblAfghanistan2);
        TextView lblCountryWhereWorking = (TextView) v.findViewById(R.id.lblCountryWhereWorking);
        TextView lblPassword = (TextView) v.findViewById(R.id.lblPassword);
        TextView lblPassHint = (TextView) v.findViewById(R.id.lblPassHint);

        chkMale.setOnClickListener(this);
        chkFemale.setOnClickListener(this);
        chkOther.setOnClickListener(this);

        lblEnglish.setOnClickListener(this);
        lblChoose.setOnClickListener(this);
        lblAfghanistan.setOnClickListener(this);
        lblAfghanistan2.setOnClickListener(this);
        btnNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Bundle bundle;
        Fragment fragment;

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        switch (v.getId()) {

            case R.id.lblEnglish:

                bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectlanguage));
                bundle.putString("selectdata", selectdata);

                fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(AccountCreationFragment.this, INTENT_SELECT_LANGUAGE);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;

            case R.id.lblChoose:

                bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectjobtitle));
                bundle.putString("selectjobtitle", selectjobtitle);

                fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(AccountCreationFragment.this, INTENT_SELECT_JOB_TITLE);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;

            case R.id.lblAfghanistan:

                bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectcountryorigin));
                bundle.putString("selectcountryorigin", selectcountryorigin);

                fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(AccountCreationFragment.this, INTENT_SELECT_COUNTRY_ORIGIN);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;

            case R.id.lblAfghanistan2:

                bundle = new Bundle();
                bundle.putString("titleid", getString(R.string.selectcountryworking));
                bundle.putString("selectcountryworking", selectcountryworking);

                fragment = new SelectAccountDataFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(AccountCreationFragment.this, INTENT_SELECT_COUNTRY_WORKING);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, fragment).addToBackStack(SelectAccountDataFragment.class.getSimpleName()).commit();

                break;

            case R.id.btnNext:


                if (validation()) {
                    isUserNameAndEmailExist();
                }

                break;

            case R.id.chkMale:

                chkMale.setChecked(true);
                chkFemale.setChecked(false);
                chkOther.setChecked(false);
                edtPlzDesc.setError(null);
                edtPlzDesc.setText("");
                edtPlzDesc.setEnabled(false);

                break;

            case R.id.chkFemale:

                chkMale.setChecked(false);
                chkFemale.setChecked(true);
                chkOther.setChecked(false);
                edtPlzDesc.setError(null);
                edtPlzDesc.setText("");
                edtPlzDesc.setEnabled(false);

                break;

            case R.id.chkOther:

                chkMale.setChecked(false);
                chkFemale.setChecked(false);
                chkOther.setChecked(true);
                edtPlzDesc.setEnabled(true);
                edtPlzDesc.requestFocus();

                break;
        }

        getActivity().overridePendingTransition(R.anim.enter, R.anim.no_anim);
    }

    private void isUserNameAndEmailExist() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CHECKUSERNAMEEMAIL, RequestMethod.POST, RequestBuilder.getCheckExistUserNameAndEmailRequest(edtUserName.getText().toString(), edtEmail.getText().toString(), ConstantData.LANGUAGE_CODE));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                jsonRegister = new JSONObject();

                                try {

                                    jsonRegister.put(PARAMS.TAG_USERNAME, edtUserName.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_PASSWORD, edtPassword.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_EMAIL, edtEmail.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_FIRSTNAME, edtFirstName.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_LASTNAME, edtLastName.getText().toString().trim());

                                    jsonRegister.put(PARAMS.TAG_LANGUAGE_CODE, sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name()));
                                    jsonRegister.put(PARAMS.TAG_LANGUAGE, sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english)));

                                    jsonRegister.put(PARAMS.TAG_PHONE, edtPhone.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_JOBTITLE, lblChoose.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_AFFILIATION_ID, edtAffiliation.getText().toString().trim());
                                    jsonRegister.put(PARAMS.TAG_FREELANCER, chkFreelancer.isChecked() ? "1" : "0");
                                    jsonRegister.put(PARAMS.TAG_ORIGIN_COUNTRY, selectcountryorigin_code.trim());
                                    jsonRegister.put(PARAMS.TAG_WORKING_COUNTRY, selectcountryworking_code.trim());
                                    jsonRegister.put(PARAMS.TAG_GENDER_TYPE, chkMale.isChecked() ? "1" : chkFemale.isChecked() ? "2" : "3");
                                    jsonRegister.put(PARAMS.TAG_GENDER, chkMale.isChecked() ? getString(R.string.male) : chkFemale.isChecked() ? getString(R.string.female) : edtPlzDesc.getText().toString());
                                    jsonRegister.put(PARAMS.TAG_DEVICETOKEN, ConstantData.REGISTRATIONID);
                                    jsonRegister.put(PARAMS.TAG_DEVICETYPE, ConstantData.DEVICE_TYPE_ANDROID);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, new AddCircleCreateUserFragment()).addToBackStack(AddCircleCreateUserFragment.class.getSimpleName()).commit();

                            } else {

                                showMessage("", jsonObject.get("message").toString());
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

        } catch (Exception e) {
            dismissProgressDialog();
        }
    }

    private boolean validation() {

        if (Utils.isEmpty(edtUserName.getText().toString()) || edtUserName.getText().toString().trim().contains(" ")) {

            edtUserName.setError(getString(R.string.please_enter_username));
            edtUserName.requestFocus();
            return false;

        } else if (!Utils.isValidUsername(edtUserName.getText().toString())) {

            edtUserName.setError(getString(R.string.invalid_username));
            edtUserName.requestFocus();
            return false;

        } else if (!Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            edtEmail.setError(getString(R.string.enter_valid_email));
            edtEmail.requestFocus();
            return false;

        } else if (!Utils.isValidEmail(edtReEnterEmail.getText().toString().trim())) {
            edtReEnterEmail.setError(getString(R.string.enter_valid_email));
            edtReEnterEmail.requestFocus();
            return false;

        } else if (!(edtEmail.getText().toString().trim().equals(edtReEnterEmail.getText().toString().trim()))) {
            edtReEnterEmail.setError(getString(R.string.email_notmatch));
            edtReEnterEmail.requestFocus();
            return false;

        } else if (Utils.isEmpty(edtFirstName.getText().toString())) {
            edtFirstName.setError(getString(R.string.invalid_firstname));
            edtFirstName.requestFocus();
            return false;

        } else if (Utils.isEmpty(edtLastName.getText().toString())) {
            edtLastName.setError(getString(R.string.invalid_lastname));
            edtLastName.requestFocus();
            return false;

        } else if (Utils.isEmpty(edtPhone.getText().toString()) || !android.util.Patterns.PHONE.matcher(edtPhone.getText().toString()).matches()) {
            edtPhone.setError(getString(R.string.invalid_phone));
            edtPhone.requestFocus();
            return false;

        } else if (!chkMale.isChecked() && !chkFemale.isChecked() && !chkOther.isChecked()) {

            showMessage(getString(R.string.gender_is_required), getString(R.string.please_make_your));

            return false;

        } else if (Utils.isEmpty(lblChoose.getText().toString())) {

            Toast.displayError(getActivity(), getString(R.string.please_select_jobtitle));
            return false;

        } else if (Utils.isEmpty(lblAfghanistan.getText().toString())) {

            Toast.displayError(getActivity(), getString(R.string.Please_select_country_of_origin));
            return false;

        } else if (Utils.isEmpty(lblAfghanistan2.getText().toString())) {

            Toast.displayError(getActivity(), getString(R.string.Please_select_country_where_working));
            return false;

        } else if (!Utils.isValidStrongPassword(edtPassword.getText().toString(), ConstantData.LANGUAGE_CODE, edtUserName.getText().toString().trim())) {


            edtPassword.requestFocus();

            showMessage(getString(R.string.please_try_again), getString(R.string.password_invalid));

            return false;

        } else if (!(edtPassword.getText().toString().trim().equals(edtRePassword.getText().toString().trim()))) {

            edtRePassword.setError(getString(R.string.password_notmatch));
            edtRePassword.requestFocus();

            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        ((AccountCreationActivity) getActivity()).restoreActionBar(false, "");

        if (requestCode == INTENT_SELECT_LANGUAGE && resultCode == Activity.RESULT_OK) {

            selectdata = data.getStringExtra("selectlanguage");
            lblEnglish.setText(selectdata);

        } else if (requestCode == INTENT_SELECT_JOB_TITLE && resultCode == Activity.RESULT_OK) {

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

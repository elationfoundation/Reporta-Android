package com.iwmf.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.iwmf.HomeScreen;
import com.iwmf.R;
import com.iwmf.SelectLanguageActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.AESCrypt;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONObject;

/**
 * <p> Displays actual terms and conditions and get user agreement to continue.
 * </p>
 */
public class AccountTermAndConditionFragment extends BaseFragment implements OnClickListener {

    private final static int INTENT_SELECT_CHECKBOX = 1;
    private SharedPreferences sharedpreferences = null;
    private CheckBox chkConfrimMYReg, chkSendMeUpdates;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_account_termcondition, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        Button btnCompleteCreation = (Button) v.findViewById(R.id.btnCompleteCreation);
        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
        chkSendMeUpdates = (CheckBox) v.findViewById(R.id.chkSendMeUpdates);
        chkConfrimMYReg = (CheckBox) v.findViewById(R.id.chkConfrimMYReg);

        TextView txtSeeTermsofUse = (TextView) v.findViewById(R.id.txtSeeTermsofUse);

        txtSeeTermsofUse.setOnClickListener(this);
        btnCompleteCreation.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        switch (v.getId()) {

            case R.id.txtSeeTermsofUse:

                TermConditionDescriptionFragment mFragment = TermConditionDescriptionFragment.getInstance(chkConfrimMYReg.isChecked());
                mFragment.setTargetFragment(AccountTermAndConditionFragment.this, INTENT_SELECT_CHECKBOX);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(TermConditionDescriptionFragment.class.getSimpleName()).commit();

                break;

            case R.id.btnCompleteCreation:

                getCreateUser();

                break;
            case R.id.btnCancel:

                Intent mIntent = new Intent(getActivity(), SelectLanguageActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

                break;

        }
    }

    private void getCreateUser() {

        try {
            displayProgressDialog();

            AccountCreationFragment.jsonRegister.put(PARAMS.TAG_SENDMAIL, chkConfrimMYReg.isChecked() ? "1" : "0");
            AccountCreationFragment.jsonRegister.put(PARAMS.TAG_SENDMEUPDATES, chkSendMeUpdates.isChecked() ? "1" : "0");
            AccountCreationFragment.jsonRegister.put(PARAMS.TAG_ISCREATEUSER, "1");


            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CREATEUSER, RequestMethod.POST, AccountCreationFragment.jsonRegister.toString());

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        AccountCreationFragment.jsonRegister = null;
                        AccountCreationFragment.jsonRegister = new JSONObject();

                        AddCircleCreateUserFragment.mContacts = null;

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                ConstantData.LANGUAGE_CODE = jsonObject.getJSONObject("data").getString(PARAMS.TAG_LANGUAGE_CODE);
                                ConstantData.LANGUAGE = jsonObject.getJSONObject("data").getString(PARAMS.TAG_LANGUAGE);
                                ConstantData.HEADERTOKEN = jsonObject.getJSONObject("data").getString(PARAMS.TAG_HEADERTOKEN);
                                ConstantData.FIXKEY = jsonObject.getJSONObject("data").getString(PARAMS.TAG_AES_ENCRYPTION_KEY);


                                sharedpreferences.edit().putString(PARAMS.TAG_AES_ENCRYPTION_KEY, AESCrypt.encrypt(ConstantData.FIXKEY, true)).apply();
                                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE, ConstantData.LANGUAGE).apply();
                                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, ConstantData.LANGUAGE_CODE).apply();

                                Utils.changeLocale(ConstantData.LANGUAGE_CODE, getActivity());

                                Editor editor = prefManager.getPrefs().edit();
                                editor.putString(PARAMS.KEY_PROFILE_DATA, jsonObject.get("data").toString());
                                editor.putString(PARAMS.KEY_HEADERTOKEN, ConstantData.HEADERTOKEN);
                                editor.putString(PARAMS.TAG_USERNAME, jsonObject.getJSONObject("data").getString(PARAMS.TAG_USERNAME));
                                editor.apply();

                                Intent mIntent = new Intent(getActivity(), HomeScreen.class);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(mIntent);
                                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

                            } else {
                                Toast.displayError(getActivity(), jsonObject.get("message").toString());
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

        if (requestCode == INTENT_SELECT_CHECKBOX && resultCode == Activity.RESULT_OK) {
            chkConfrimMYReg.setChecked(true);
        }
    }

}
package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;

import com.iwmf.AccountCreationActivity;
import com.iwmf.HomeScreen;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.AESCrypt;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <p> Login page for user to enter into application.
 * It handles authentication and validation of user.
 * Also save user details on login successful. </p>
 */
public class LoginFragment extends BaseFragment implements OnClickListener {

    private EditText edtUsername = null, edtPassword = null;
    private CheckBox chkStayLoggedin = null;
    private String resetEmail = "";
    private SharedPreferences sharedpreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_login, container, false);

        Utils.isOnline(getActivity());

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        edtUsername = (EditText) v.findViewById(R.id.edtUsername);
        edtPassword = (EditText) v.findViewById(R.id.edtPassword);
        Button btnLogin = (Button) v.findViewById(R.id.btnLogin);
        TextView txtForgetPass = (TextView) v.findViewById(R.id.txtForgetPass);
        TextView txtCreateAcount = (TextView) v.findViewById(R.id.txtCreateAcount);
        chkStayLoggedin = (CheckBox) v.findViewById(R.id.chkStayLoggedin);

        edtUsername.setOnClickListener(this);
        edtPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        txtForgetPass.setOnClickListener(this);
        txtCreateAcount.setOnClickListener(this);
        chkStayLoggedin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLogin:


                if (Utils.isOnline(getActivity())) {


                    if (validation()) {
                        getLogin("0");
                    }
                }

                break;

            case R.id.txtForgetPass:


                if (Utils.isOnline(getActivity())) {

                    showForgotPasswordDialog();
                }

                break;

            case R.id.txtCreateAcount:

                if (Utils.isOnline(getActivity())) {

                    Intent intent = new Intent(getActivity(), AccountCreationActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.no_anim);
                }
                break;

        }
    }

    private void showForceLoginDialog(String msg) {

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setMessage(msg).setCancelable(false).setNegativeButton(R.string.cancel, null).setPositiveButton(android.R.string.yes, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();

                        getLogin("1");

                    }
                });
            }
        });
        dialog.show();
    }

    private void showForgotPasswordDialog() {

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.fragment_password_dialogbox, null, false);

        final EditText edtResetEmail = (EditText) view.findViewById(R.id.edtResetEmail);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.forget_password_dialog).setCancelable(true).setNegativeButton(R.string.cancel, null).setPositiveButton(android.R.string.ok, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        resetEmail = edtResetEmail.getText().toString().trim();

                        if (Utils.isValidEmail(resetEmail)) {
                            dialog.dismiss();
                            getForgotpassword();
                        } else {
                            edtResetEmail.setError(getString(R.string.enter_valid_email));
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private boolean validation() {

        if (Utils.isEmpty(edtUsername.getText().toString()) || edtUsername.getText().toString().trim().contains(" ")) {

            edtUsername.setError(getString(R.string.please_enter_username));
            edtUsername.requestFocus();
            return false;

            // } else if (!Utils.isValidUsername(edtUsername.getText().toString())) {
            //
            // edtUsername.setError(getString(R.string.invalid_username));
            // edtUsername.requestFocus();
            // return false;

        } else if (!Utils.isValidPassword(edtPassword.getText().toString())) {

            edtPassword.setError(getString(R.string.invalid_password));
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void getForgotpassword() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_FORGOTPASSWORD, RequestMethod.POST, RequestBuilder.getForgotpassword(resetEmail, ConstantData.LANGUAGE_CODE));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        Toast.displayError(getActivity(), jsonObject.get("message").toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
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

    private void getLogin(String forceLogin) {

        try {
            displayProgressDialog();
            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_LOGIN, RequestMethod.POST, RequestBuilder.getLoginRequest(edtUsername.getText().toString(), edtPassword.getText().toString(), ConstantData.LANGUAGE_CODE, forceLogin));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.has("status") && jsonObject.get("status").toString().equals("1")) {

                                ConstantData.FIXKEY = jsonObject.getJSONObject("data").getString(PARAMS.TAG_AES_ENCRYPTION_KEY);

                                prefManager.getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, false).apply();
                                Utils.clearAllNotifications(getActivity());

                                ConstantData.LANGUAGE_CODE = jsonObject.getJSONObject("data").getString(PARAMS.TAG_LANGUAGE_CODE);
                                ConstantData.LANGUAGE = jsonObject.getJSONObject("data").getString(PARAMS.TAG_LANGUAGE);
                                ConstantData.HEADERTOKEN = jsonObject.getJSONObject("data").getString(PARAMS.TAG_HEADERTOKEN);

                                sharedpreferences.edit().putString(PARAMS.TAG_AES_ENCRYPTION_KEY, AESCrypt.encrypt(ConstantData.FIXKEY, true)).apply();
                                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, ConstantData.LANGUAGE_CODE).apply();
                                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE, ConstantData.LANGUAGE).apply();
                                sharedpreferences.edit().putBoolean(PARAMS.KEY_STAY_LOGGED_IN, chkStayLoggedin.isChecked()).apply();


                                JSONArray mJsonArray = jsonObject.getJSONObject("data").getJSONArray("activecheckin");


                                if (mJsonArray != null && mJsonArray.length() > 0) {
                                    saveCheckIn(mJsonArray.getJSONObject(0));
                                } else {
                                    try {
                                        Editor editor = CryptoManager.getInstance(getActivity()).getPrefs().edit();

                                        editor.remove(PARAMS.TAG_DESCRIPTION);
                                        editor.remove(PARAMS.TAG_STARTTIME);
                                        editor.remove(PARAMS.TAG_FREQUENCY);
                                        editor.remove(PARAMS.TAG_MESSAGE_EMAIL);
                                        editor.remove(PARAMS.TAG_RECEIVEPROMPT);
                                        editor.remove(PARAMS.TAG_MESSAGE_SOCIAL);
                                        editor.remove(PARAMS.TAG_LONGITUDE);
                                        editor.remove(PARAMS.TAG_MESSAGE_SMS);
                                        editor.remove(PARAMS.TAG_CHECKIN_ID);
                                        editor.remove(PARAMS.TAG_ENDTIME);
                                        editor.remove(PARAMS.TAG_STATUS);
                                        editor.remove(PARAMS.TAG_CONTACTLIST);
                                        editor.remove(PARAMS.TAG_LOCATION);
                                        editor.remove(PARAMS.TAG_LATITUDE);
                                        editor.remove(PARAMS.TAG_LASTCONFIRMTIME);

                                        editor.apply();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                Utils.changeLocale(ConstantData.LANGUAGE_CODE, getActivity());

                                Editor editor = prefManager.getPrefs().edit();
                                editor.putString(PARAMS.KEY_PROFILE_DATA, jsonObject.get("data").toString());
                                editor.putString(PARAMS.KEY_HEADERTOKEN, ConstantData.HEADERTOKEN);
                                editor.putString("cid", jsonObject.getJSONObject("data").getString("checkin_id"));
                                editor.putString("lockstatus", jsonObject.getJSONObject("data").getString("lockstatus"));
                                editor.putString(PARAMS.TAG_USERNAME, jsonObject.getJSONObject("data").getString(PARAMS.TAG_USERNAME));
                                editor.apply();


                                Intent mIntent = new Intent(getActivity(), HomeScreen.class);
                                getActivity().startActivity(mIntent);
                                getActivity().finish();
                                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

                            } else if (jsonObject.has("status") && jsonObject.get("status").toString().equals("3")) {
                                showForceLoginDialog(jsonObject.get("message").toString());
                            } else {
                                Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
                            }

                        } else {
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
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

    private void saveCheckIn(JSONObject mJObject) {

        try {
            Editor editor = prefManager.getPrefs().edit();

            long startTime = getCurrentSystemTimeZoneLongTime(mJObject.getString("starttime"));
            long lastConfirmTime = getCurrentSystemTimeZoneLongTime(mJObject.getString("laststatustime"));
            if (lastConfirmTime < startTime) {
                lastConfirmTime = startTime;
            }

            editor.putInt(PARAMS.TAG_CHECKIN_ID, Integer.parseInt(mJObject.getString("id")));
            editor.putString(PARAMS.TAG_DESCRIPTION, mJObject.getString("description"));
            editor.putString(PARAMS.TAG_LOCATION, mJObject.getString("location"));
            editor.putString(PARAMS.TAG_LATITUDE, mJObject.getString("latitude"));
            editor.putString(PARAMS.TAG_LONGITUDE, mJObject.getString("longitude"));
            editor.putString(PARAMS.TAG_TIMEZONE_ID, mJObject.getString("timezone_id"));
            editor.putLong(PARAMS.TAG_STARTTIME, startTime);
            editor.putLong(PARAMS.TAG_ENDTIME, getCurrentSystemTimeZoneLongTime(mJObject.getString("endtime")));
            editor.putString(PARAMS.TAG_MESSAGE_SMS, mJObject.getString("message_sms"));
            editor.putString(PARAMS.TAG_MESSAGE_EMAIL, mJObject.getString("message_email"));
            editor.putString(PARAMS.TAG_MESSAGE_SOCIAL, mJObject.getString("message_social"));
            editor.putString(PARAMS.TAG_RECEIVEPROMPT, mJObject.getString("receiveprompt"));
            editor.putInt(PARAMS.TAG_FREQUENCY, Integer.parseInt(mJObject.getString("frequency")));
            editor.putInt(PARAMS.TAG_STATUS, Integer.parseInt(mJObject.getString("status")));
            editor.putLong(PARAMS.TAG_LASTCONFIRMTIME, lastConfirmTime);
            editor.putLong(PARAMS.TAG_NEXTCONFIRMATIONTIME, getCurrentSystemTimeZoneLongTime(mJObject.getString("nextconfirmationtime")));
            editor.putString(PARAMS.TAG_FB_TOKEN, mJObject.getString("fb_token"));
            editor.putString(PARAMS.TAG_TWITTER_TOKEN, mJObject.getString("twitter_token"));
            editor.putString(PARAMS.TAG_TWITTER_TOKEN_SECRET, mJObject.getString("twitter_token_secret"));
            editor.putString(PARAMS.TAG_CHECKIN_ENABLED, mJObject.getString("checkin_enabled"));
            editor.putLong(PARAMS.TAG_CREATED_ON, getCurrentSystemTimeZoneLongTime(mJObject.getString("created_on")));
            editor.putLong(PARAMS.TAG_UPDATED_ON, getCurrentSystemTimeZoneLongTime(mJObject.getString("updated_on")));
            editor.putString(PARAMS.TAG_DEVICETOKEN, mJObject.getString("devicetoken"));
            editor.putString(PARAMS.TAG_TYPE, mJObject.getString("type"));

            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

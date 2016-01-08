package com.iwmf.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iwmf.LoginActivity;
import com.iwmf.R;
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
import com.iwmf.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p> Displays user profile and also gives option to edit profile.
 * Also give sign out option to user. </p>
 */
public class ProfileFragment extends BaseFragment implements OnClickListener {

    private final static int INTENT_UPDATE_PROFILE = 4;
    private CryptoManager prefManager = null;
    private Logindata mLogindata = null;
    private CheckBox chkFreelancer;
    private TextView txtName;
    private TextView lblChoose;
    private TextView txtAffiliation;
    private TextView lblAfghanistan;
    private TextView lblAfghanistan2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_profile, container, false);

        prefManager = CryptoManager.getInstance(getActivity());

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        chkFreelancer = (CheckBox) v.findViewById(R.id.chkFreelancer);

        txtName = (TextView) v.findViewById(R.id.txtName);
        TextView txtEdit = (TextView) v.findViewById(R.id.txtEdit);
        lblChoose = (TextView) v.findViewById(R.id.lblChoose);
        TextView txtVer = (TextView) v.findViewById(R.id.txtVer);

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            txtVer.setText(packageInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtAffiliation = (TextView) v.findViewById(R.id.txtAffiliation);
        lblAfghanistan = (TextView) v.findViewById(R.id.lblAfghanistan);
        lblAfghanistan2 = (TextView) v.findViewById(R.id.lblAfghanistan2);
        RelativeLayout rltSignOut = (RelativeLayout) v.findViewById(R.id.rltSignOut);
        RelativeLayout rltLegal = (RelativeLayout) v.findViewById(R.id.rltLegal);

        txtName.setOnClickListener(this);
        txtEdit.setOnClickListener(this);
        lblChoose.setOnClickListener(this);
        txtAffiliation.setOnClickListener(this);
        lblAfghanistan.setOnClickListener(this);
        lblAfghanistan2.setOnClickListener(this);
        rltSignOut.setOnClickListener(this);
        rltLegal.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        profile_data();

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

    private void profile_data() {

        try {
            mLogindata = new Gson().fromJson(CryptoManager.getInstance(getActivity()).getPrefs().getString(PARAMS.KEY_PROFILE_DATA, ""), Logindata.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mLogindata != null) {

            txtName.setText(mLogindata.getFirstname() + " " + mLogindata.getLastname());
            lblChoose.setText(mLogindata.getJobtitle());

            txtAffiliation.setText(mLogindata.getAffiliation_id());

            String check = mLogindata.getFreelancer();
            if (check.equals("1")) {

                chkFreelancer.setChecked(true);

            } else {

                chkFreelancer.setChecked(false);
            }

            InputStream mInputStream = getInputStream();

            JSONArray mJsonArray = null;

            try {

                String str = IOUtils.toString(mInputStream, "utf-8");
                mJsonArray = new JSONArray(str);

            } catch (JSONException | IOException e) {
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
                    }

                    if (mLogindata.getWorking_country().equalsIgnoreCase(mJsonArray.getJSONObject(i).getString("Code")) || mLogindata.getWorking_country().equalsIgnoreCase(mJsonArray.getJSONObject(i).getString("Name"))) {

                        countryWorking = mJsonArray.getJSONObject(i).getString("Name");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            lblAfghanistan.setText(countryOrigin.trim());
            lblAfghanistan2.setText(countryWorking.trim());

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtEdit:

                Fragment mFragment = new ProfileEditFragment();
                mFragment.setTargetFragment(ProfileFragment.this, INTENT_UPDATE_PROFILE);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(null).commit();

                break;

            case R.id.rltSignOut:

                getSignout();

                break;

            case R.id.rltLegal:

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, TermConditionDescriptionFragment.getInstance(false)).addToBackStack(TermConditionDescriptionFragment.class.getSimpleName()).commit();

                break;
        }
    }


    private void getSignout() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_SIGNOUT, RequestMethod.POST, RequestBuilder.getSignout());

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();


                        Utils.clearAllNotifications(getActivity());

                        Editor chkeditor = prefManager.getPrefs().edit();
                        chkeditor.putBoolean(PARAMS.KEY_STAY_LOGGED_IN, false);
                        chkeditor.apply();

                        ConstantData.FIXKEY = "";

                        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);
                        sharedpreferences.edit().putString(PARAMS.TAG_AES_ENCRYPTION_KEY, ConstantData.FIXKEY).apply();

                        removeCheckIn();

                        finishAll();

                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgressDialog();
                        finishAll();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    finishAll();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void finishAll() {

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);

        ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
        ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));

        Utils.changeLocale(ConstantData.LANGUAGE_CODE, getActivity());

        Intent mIntent = new Intent(getActivity(), LoginActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_UPDATE_PROFILE && resultCode == Activity.RESULT_OK) {
            profile_data();
        }
    }

    private void removeCheckIn() {

        try {
            Editor editor = prefManager.getPrefs().edit();

            editor.remove(PARAMS.TAG_HEADERTOKEN);
            editor.remove(PARAMS.TAG_DEVICETOKEN);

            ConstantData.HEADERTOKEN = "";
            ConstantData.REGISTRATIONID = "";

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

}
package com.iwmf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.LoginFragment;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Utils;

/**
 * <p> Login page for user to enter into application.
 * It handles authentication and validation of user.
 * Also save user details on login successful. </p>
 */
@SuppressWarnings("ALL")
public class LoginActivity extends BaseAppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    protected GoogleCloudMessaging gcm;

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);


        SharedPreferences sharedpreferences = getSharedPreferences("reportapref", Context.MODE_PRIVATE);

        ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
        ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));
        Utils.changeLocale(ConstantData.LANGUAGE_CODE, this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        }

        if (TextUtils.isEmpty(getRegistrationId(this))) {
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                registerInBackground();
            }

        } else {
            ConstantData.REGISTRATIONID = getRegistrationId(this);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    private String getRegistrationId(Context context) {

        //final SharedPreferences prefs = prefManager.getPrefs();
        String registrationId = sharedpreferences.getString(PARAMS.KEY_REGISTRATION_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = sharedpreferences.getInt(PARAMS.KEY_APP_VERSION, Integer.MIN_VALUE);

        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
            } else {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle(R.string.device_not_supported);
                mBuilder.setMessage(R.string.pushnotification_will_not_work);
                mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                mBuilder.create().show();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {

        AsyncTask<Void, Void, Boolean> mAsyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }

                    try {
                        gcm.unregister();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        ConstantData.REGISTRATIONID = gcm.register(ConstantData.SENDER_ID);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    storeRegistrationId(LoginActivity.this, ConstantData.REGISTRATIONID);
                    return true;
                } catch (Exception ex) {

                    ex.printStackTrace();

                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {

                super.onPostExecute(result);
            }
        };
        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {

        // final SharedPreferences prefs = prefManager.getPrefs();
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PARAMS.KEY_REGISTRATION_ID, regId);
        editor.putInt(PARAMS.KEY_APP_VERSION, appVersion);
        editor.apply();
    }

}

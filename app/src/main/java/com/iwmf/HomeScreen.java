package com.iwmf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.broadcasts.AlarmBroadcast;
import com.iwmf.fragments.InfoDialogFragment;
import com.iwmf.http.PARAMS;
import com.iwmf.services.MediaUploadService;
import com.iwmf.utils.ConnectivityTools;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.DBHelper;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import java.util.Calendar;

/**
 * <p> Main Page of the application.
 * Register and handle all receivers.
 * Takes care of user missed checkin.
 * </p>
 */
@SuppressWarnings("ALL")
public class HomeScreen extends BaseAppCompatActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = HomeScreen.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //    private static final long UPDATE_INTERVAL = 10 * 1000;
//    private static final long FASTEST_INTERVAL = 10 * 1000;
    protected GoogleCloudMessaging gcm;
    protected GoogleApiClient mGoogleApiClient;
    private LinearLayout lnrPendingMedia = null;
    private TextView txtPendingMediaCount;

    private LocationRequest mLocationRequest;
    private LocationManager locationManager;

    private BroadcastReceiver uploadStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                if (intent.getAction().equals(ConstantData.ACTION_UPLOAD_DONE)) {

                    displayPendingMediaCount();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static int getAppVersion(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);

        setContentView(R.layout.fragment_home);

        try {

            if (ConstantData.DB == null || !ConstantData.DB.isOpen()) {
                ConstantData.DB = new DBHelper(this);
                ConstantData.DB.open();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        createLocationRequest();


        boolean sosStatus = prefManager.getPrefs().getBoolean(PARAMS.KEY_SOS_STATUS, false);
        final boolean isMissedCheckin = prefManager.getPrefs().getBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, false);
        final int sosId = prefManager.getPrefs().getInt(PARAMS.KEY_SOS_ID, 0);

        try {


            String lockstatus = prefManager.getPrefs().getString("lockstatus", "");


            if (lockstatus.equals("1")) {


                performSOS(false, false);

            } else {
                dissmissLockDialog();
                if (savedInstanceState == null) {
                    boolean isMissed = getIntent().getBooleanExtra("isMissedCheckin", false);
                    if (!isMissed) {
                        reActivateCheckIn();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.imbSOS).setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                performSOS(false, true);

                return false;
            }
        });

        if ((sosStatus && sosId > 0) || isMissedCheckin) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreen.this);
            dialog.setTitle(getString(R.string.reporta));
            dialog.setMessage(getString(R.string.app_locked));
            dialog.setCancelable(false);
            dialog.setPositiveButton(getString(R.string.login), new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    displaySOSPopup(false, !isMissedCheckin);
                }
            });
            dialog.show();

        } else {

            if (ConnectivityTools.isNetworkAvailable(HomeScreen.this) && ConstantData.DB.getPendingMediaCount() > 0) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(HomeScreen.this);
                dialog.setTitle(getString(R.string.media_uploads));
                dialog.setMessage(getString(R.string.media_uploads_details));
                dialog.setCancelable(false);
                dialog.setPositiveButton(getString(R.string.upload_now), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(HomeScreen.this, MediaUploadService.class);
                        startService(intent);
                    }
                });
                dialog.setNegativeButton(getString(R.string.upload_later), null);
                dialog.show();
            }
        }

        init();
    }

    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(5000);

        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void reActivateCheckIn() {

        long endDateTemp = prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0);

        Calendar mCurrentCalendar = Calendar.getInstance();

        long currentTime = mCurrentCalendar.getTimeInMillis();

        if (prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0) > 0 && (endDateTemp == 0 || endDateTemp > currentTime)) {

            AlarmBroadcast mAlarmBroadcast = new AlarmBroadcast();

            int frequency = prefManager.getPrefs().getInt(PARAMS.TAG_FREQUENCY, 0);
            long startTime = prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0);
            long lastConfirmCheckinTime = prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, 0);

            if (startTime > lastConfirmCheckinTime) {
                lastConfirmCheckinTime = startTime;
            }

            /**
             * nextConfirmation is for calculate next confirmation time
             */
            prefManager.getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, false).apply();

            // checkin remider
            Utils.setCheckInReminder(startTime, endDateTemp, currentTime, lastConfirmCheckinTime, frequency, mAlarmBroadcast, this);

            // checkIn frequency
            Utils.setCheckInFrequency(startTime, endDateTemp, currentTime, lastConfirmCheckinTime, frequency, mAlarmBroadcast, this);

            // Miss checkin
            Utils.setMissedCheckIn(startTime, endDateTemp, currentTime, lastConfirmCheckinTime, frequency, mAlarmBroadcast, this);

            // End checkin
            Utils.setEndCheckIn(endDateTemp, currentTime, mAlarmBroadcast, this);

            // Pending Check In Reminder
            Utils.setPendingCheckIn(startTime, currentTime, mAlarmBroadcast, this);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        registerGPSListener();
    }

    @Override
    protected void onStop() {

        stopPeriodicUpdates();
        super.onStop();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }

        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(ConstantData.ACTION_UPLOAD_DONE);
        registerReceiver(uploadStatusReceiver, filter);

        displayPendingMediaCount();
    }

    @Override
    public void onDestroy() {

        if (locationManager != null) {
            locationManager = null;
        }

        if (ConstantData.DB != null) {
            ConstantData.DB.close();
        }

        stopPeriodicUpdates();

        unregisterReceiver(uploadStatusReceiver);

        super.onDestroy();
    }

    public void registerGPSListener() {

        if (servicesConnected()) {

            if (mGoogleApiClient != null) {
                if (!mGoogleApiClient.isConnected()) {
                    try {
                        mGoogleApiClient.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    startPeriodicUpdates();
                }
            }
        }
    }

    private void init() {

        ImageView imvAlert = (ImageView) findViewById(R.id.imvAlert);
        ImageView imvCheckin = (ImageView) findViewById(R.id.imvCheckin);
        ImageView imvContact = (ImageView) findViewById(R.id.imvContact);
        ImageView imvProfile = (ImageView) findViewById(R.id.imvProfile);
        txtPendingMediaCount = (TextView) findViewById(R.id.txtPendingMediaCount);
        TextView txtAboutReporta = (TextView) findViewById(R.id.txtAboutReporta);
        lnrPendingMedia = (LinearLayout) findViewById(R.id.lnrPendingMedia);

        imvAlert.setOnClickListener(this);
        imvCheckin.setOnClickListener(this);
        imvContact.setOnClickListener(this);
        imvProfile.setOnClickListener(this);
        txtAboutReporta.setOnClickListener(this);
        lnrPendingMedia.setOnClickListener(this);

        if (TextUtils.isEmpty(getRegistrationId(this))) {
            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                registerInBackground();
            }

        } else {
            ConstantData.REGISTRATIONID = getRegistrationId(this);
        }

    }

    private void displayPendingMediaCount() {

        try {

            int pendingMediaCount = ConstantData.DB.getPendingMediaCount();

            String strCount = "" + pendingMediaCount;

            lnrPendingMedia.setEnabled(true);

            if (pendingMediaCount > 100) {
                strCount = "99+";
            } else if (pendingMediaCount <= 0) {
                strCount = "0";
                lnrPendingMedia.setEnabled(false);
            }

            txtPendingMediaCount.setText(strCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void registerInBackground() {

        AsyncTask<Void, Void, Boolean> mAsyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(HomeScreen.this);
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

                    storeRegistrationId(HomeScreen.this, ConstantData.REGISTRATIONID);

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

        // final SharedPreferences prefs = sharedpreferences;
        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PARAMS.KEY_REGISTRATION_ID, regId);
        editor.putInt(PARAMS.KEY_APP_VERSION, appVersion);
        editor.apply();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imvCheckin:


                startActivity(new Intent(HomeScreen.this, AddCheckinActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.no_anim);

                break;

            case R.id.imvAlert:


                startActivity(new Intent(HomeScreen.this, AlertActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.no_anim);

                break;

            case R.id.imvContact:


                startActivity(new Intent(HomeScreen.this, ContactsActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.no_anim);

                break;

            case R.id.imvProfile:


                startActivity(new Intent(HomeScreen.this, ProfileActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.no_anim);

                break;

            case R.id.lnrPendingMedia:


                if (!MediaUploadService.MEDIA_IS_UPLOADING) {
                    startActivity(new Intent(HomeScreen.this, PendingMediaActivity.class));
                    overridePendingTransition(R.anim.abc_slide_in_top, R.anim.no_anim);
                } else {
                    Toast.displayError(HomeScreen.this, getString(R.string.media_uploading_running));
                }
                break;

            case R.id.txtAboutReporta:


                startActivity(new Intent(HomeScreen.this, AboutMeActivity.class));
                overridePendingTransition(R.anim.abc_slide_in_top, R.anim.no_anim);

                break;
        }
    }

    /**
     * In response to a request to stop updates, send a request to Location Services
     */
    // private void stopPeriodicUpdates() {
    // if (mLocationClient != null && mLocationClient.isConnected()) {
    // try {
    // mLocationClient.removeLocationUpdates(this);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }

    /**
     * In response to a request to start updates, send a request to Location Services
     */
    private void startPeriodicUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * In response to a request to stop updates, send a request to Location Services
     */
    private void stopPeriodicUpdates() {

        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGPSDisabledAlertToUser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage(R.string.gps_disable_msg).setCancelable(false).setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                try {
                    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.displayError(HomeScreen.this, R.string.location_disable_msg);
                }

            }
        });

        InfoDialogFragment mDialogFragment = new InfoDialogFragment();
        mDialogFragment.setCancelable(false);
        mDialogFragment.setDialog(alertDialogBuilder.create());
        mDialogFragment.show(HomeScreen.this.getSupportFragmentManager(), "");
    }

	/*
     * Called by Location Services if the attempt to Location Services fails.
	 */

    @Override
    public void onConnected(Bundle dataBundle) {

        // Display the connection status
        // If already requested, start periodic updates
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
         * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */

        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
                 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

            } catch (android.content.IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        } else {
            // If no resolution is available, display a dialog to the user with
            // the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            ConstantData.LATITUDE = location.getLatitude();
            ConstantData.LONGITUDE = location.getLongitude();

        }
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {

            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), TAG);
            }
            return false;
        }
    }

    public void unRegisterGPSListener() {

        if (mGoogleApiClient != null) {
            stopPeriodicUpdates();
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        // mLocationClient = null;
    }

    /**
     * Show a dialog returned by Google Play services for the connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), TAG);
        }
    }

    @Override
    public void onBackPressed() {

        // long endDateTemp = prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0);

        // if (prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0) > 0 && (endDateTemp == 0 || endDateTemp > new Date().getTime())) {
        // Toast.displayError(getApplicationContext(), getString(R.string.cannot_close_app));
        // } else {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.abc_fade_out);
        // }
    }

    @Override
    public void onConnectionSuspended(int arg0) {

        // TODO Auto-generated method stub
    }

    /**
     * Define a DialogFragment to display the error dialog generated in showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {

            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {

            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return mDialog;
        }
    }

}

package com.iwmf;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.AESCrypt;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Utils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * <p> IWMFApplication is Main Application component.
 * It handles Twitter authentication and also instantiate Fabric for analytics. </p>
 */
public class IWMFApplication extends Application {

    public static final String BROADCAST_ACTION_NOTIFICATION = "com.iwmf.notification.BROADCAST_NOTIFICATION";

    @Override
    public void onCreate() {

        super.onCreate();

        try {

            TwitterAuthConfig authConfig = new TwitterAuthConfig(AESCrypt.decrypt(getString(R.string.tw_key)), AESCrypt.decrypt(getString(R.string.tw_secret)));

            Fabric.with(this, new Twitter(authConfig));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            FacebookSdk.sdkInitialize(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        updateConfig();
    }

    private void updateConfig() {

        try {

            SharedPreferences sharedpreferences = getSharedPreferences("reportapref", Context.MODE_PRIVATE);

            ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
            ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));
            ConstantData.FIXKEY = sharedpreferences.getString(PARAMS.TAG_AES_ENCRYPTION_KEY, "");
            ConstantData.FIXKEY = AESCrypt.decrypt(ConstantData.FIXKEY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // ConstantData.LANGUAGE_CODE = CryptoManager.getInstance(this.getApplicationContext()).getPrefs().getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
            // ConstantData.LANGUAGE = CryptoManager.getInstance(this.getApplicationContext()).getPrefs().getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));
            ConstantData.HEADERTOKEN = CryptoManager.getInstance(this.getApplicationContext()).getPrefs().getString(PARAMS.KEY_HEADERTOKEN, "");

            Utils.changeLocale(ConstantData.LANGUAGE_CODE, this.getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        updateConfig();
    }
}

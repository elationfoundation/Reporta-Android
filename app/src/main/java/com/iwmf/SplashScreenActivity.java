package com.iwmf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.iwmf.http.PARAMS;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.RootUtil;
import com.iwmf.utils.Utils;

/**
 * <p> Appication splash screen.
 * Checks if the user is coming for the
 * Also save user details on login successful. </p>
 */
@SuppressWarnings("ALL")
public class SplashScreenActivity extends AppCompatActivity {

    private Handler mHandler = null;
    private SharedPreferences sharedpreferences = null;

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            boolean is = sharedpreferences.getBoolean(PARAMS.KEY_IS_VERY_FIRST_TIME, true);

            if (is) {

                Intent mIntent = new Intent(SplashScreenActivity.this, SelectLanguageActivity.class);
                startActivity(mIntent);

            } else {

                is = sharedpreferences.getBoolean(PARAMS.KEY_STAY_LOGGED_IN, false);

                ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
                ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));
                Utils.changeLocale(ConstantData.LANGUAGE_CODE, SplashScreenActivity.this);

                if (is) {

                    Intent mIntent = new Intent(SplashScreenActivity.this, HomeScreen.class);
                    startActivity(mIntent);

                } else {

                    Intent mIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(mIntent);

                }
            }

            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_splash_screen);

        sharedpreferences = getSharedPreferences("reportapref", Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!RootUtil.isDeviceRooted()) {
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, (5 * 1000));
        } else {
            RootUtil.showIfDeviceRooted(getString(R.string.rooting_detected_title), getString(R.string.rooting_detected_msg), this);
        }
    }

    @Override
    protected void onDestroy() {

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }

}

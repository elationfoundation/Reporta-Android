package com.iwmf.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.iwmf.http.PARAMS;

/**
 * <p> Used for encryption of shared preferences. </p>
 */
public class CryptoManager {

    private static CryptoManager instance = null;
    private SharedPreferences prefs = null;

    private CryptoManager(Context context) {

        prefs = new ObscuredSharedPreferences(context, context.getSharedPreferences(PARAMS.IWMFREPORTA_SHAREDPREF, Context.MODE_PRIVATE));
    }

    public static CryptoManager getInstance(Context context) {

        if (instance == null) instance = new CryptoManager(context);
        return instance;
    }

    public SharedPreferences getPrefs() {

        return prefs;
    }

}

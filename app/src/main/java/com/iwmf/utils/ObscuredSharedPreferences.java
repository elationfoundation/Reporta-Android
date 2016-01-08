package com.iwmf.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * <p> Provide Encryption/Decryption to shared preferences using AESCrypt. </p>
 */
@SuppressWarnings("ALL")
public class ObscuredSharedPreferences implements SharedPreferences {

    protected SharedPreferences delegate;
    protected Context context;

    public ObscuredSharedPreferences(Context context, SharedPreferences delegate) {
        this.delegate = delegate;

        this.context = context;
    }

    public Editor edit() {
        return new Editor();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            final String v = delegate.getString(key, null);
            return v != null ? Boolean.parseBoolean(decrypt(v)) : defValue;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            final String v = delegate.getString(key, null);
            return v != null ? Float.parseFloat(decrypt(v)) : defValue;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            final String v = delegate.getString(key, null);
            return v != null ? Integer.parseInt(decrypt(v)) : defValue;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public long getLong(String key, long defValue) {

        try {
            final String v = delegate.getString(key, null);
            return v != null ? Long.parseLong(decrypt(v)) : defValue;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getString(String key, String defValue) {
        try {
            final String v = delegate.getString(key, null);
            return v != null ? decrypt(v) : defValue;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean contains(String s) {
        return delegate.contains(s);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    protected String encrypt(String value) {

        try {

            String str;

            if (value != null && value.length() > 0 && ConstantData.FIXKEY.length() > 0) {
                str = AESCrypt.encrypt(value, ConstantData.FIXKEY);
            } else {
                str = value;
            }

            return str;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String decrypt(String value) {

        try {

            String str;

            if (value != null && value.length() > 0 && ConstantData.FIXKEY.length() > 0) {
                str = AESCrypt.decrypt(value, ConstantData.FIXKEY);
            } else {
                str = value;
            }

            return str;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getStringSet(String arg0, Set<String> arg1) {
        return null;
    }

    public class Editor implements SharedPreferences.Editor {

        protected SharedPreferences.Editor delegate;

        public Editor() {
            this.delegate = ObscuredSharedPreferences.this.delegate.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            delegate.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            delegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            delegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            delegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            delegate.putString(key, encrypt(value));
            return this;
        }

        @Override
        public void apply() {
            delegate.apply();
        }

        @Override
        public Editor clear() {
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return delegate.commit();
        }

        @Override
        public Editor remove(String s) {
            delegate.remove(s);
            return this;
        }

        @Override
        public android.content.SharedPreferences.Editor putStringSet(String arg0, Set<String> arg1) {
            return null;
        }
    }

}
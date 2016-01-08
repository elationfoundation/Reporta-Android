package com.iwmf.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.iwmf.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@SuppressWarnings("ALL")
public class RootUtil {

    // TODO : Comment should removed before app submit to play store
    public static boolean isDeviceRooted() {
        // return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
        return false;
    }

    public static void showIfDeviceRooted(final String title, final String message, final Activity mActivity) {

        try {

            if (mActivity == null) return;

            boolean isRootAvailable = isDeviceRooted();

            if (!isRootAvailable) return;

            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);

                    dialog.setTitle(title);
                    dialog.setMessage(message);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton(mActivity.getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            mActivity.finish();
                        }
                    });
                    dialog.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}

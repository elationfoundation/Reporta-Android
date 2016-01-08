package com.iwmf.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.iwmf.R;
import com.iwmf.utils.CryptoManager;
import com.iwmf.utils.Utils;
import com.iwmf.views.MyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p> BaseFragment that is  extended by every fragment in Reporta.
 * Used for formatting and getting time in UTC format quickly and displaying popup in fragments. </p>
 */
public class BaseFragment extends Fragment {

    protected CryptoManager prefManager = null;
    private MyProgressDialog progressDialog = null;

    protected void displayProgressDialog() {

        try {
            if (progressDialog == null) {
                progressDialog = new MyProgressDialog(getActivity());
            }
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.changeLocale(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        prefManager = CryptoManager.getInstance(getActivity());
    }

    protected void dismissProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void dismissProgressDialogOnUIthread() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    protected void showMessage(final String title, final String message) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dismissProgressDialog();

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                if (title != null && title.trim().length() > 0) {
                    dialog.setTitle(title);
                }

                dialog.setMessage(message);
                dialog.setPositiveButton(getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    protected String getUTCTIME(long date) {

        if (date == 0) return "";

        try {
            Date parsed = new Date(date);


            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            return sourceFormat.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected long getCurrentSystemTimeZoneLongTime(String date) {

        if (Utils.isEmpty(date) || "0000-00-00 00:00:00".equals(date)) return 0;
        try {

            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));


            Date parsed = sourceFormat.parse(date);


            sourceFormat.setTimeZone(TimeZone.getDefault());
            String localDate = sourceFormat.format(parsed);


            parsed = sourceFormat.parse(localDate);


            return parsed.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}

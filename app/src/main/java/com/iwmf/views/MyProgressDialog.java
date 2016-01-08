package com.iwmf.views;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ProgressBar;

import com.iwmf.R;

/**
 * MyProgressDialog class is used to show progress bar with transparent background using custom theme
 */

public class MyProgressDialog extends Dialog {

    public MyProgressDialog(Context context) {

        super(context, R.style.Theme_CustomDialog);
        init();
    }

    // initialize progress dialog
    private void init() {

        try {
            if (super.isShowing()) {
                super.dismiss();
            }
            super.requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.addContentView(new ProgressBar(getContext()), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            super.setCancelable(false);
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // dismiss progress dialog
    public void dismiss() {

        try {
            if (this.isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

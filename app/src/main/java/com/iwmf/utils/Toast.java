package com.iwmf.utils;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.view.Gravity;

import com.iwmf.R;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * <p> Custom Toasts to display at the center of screen. </p>
 */
@SuppressWarnings("ALL")
public class Toast {

    public static void displayMessage(Context context, String message) {

        if (message != null && context != null) {
            android.widget.Toast toast = android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void displayMessage(Context context, int stringId) {

        if (context != null) {
            android.widget.Toast toast = android.widget.Toast.makeText(context, context.getString(stringId), android.widget.Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void displayError(Context context, String message) {

        if (message != null && context != null) {

            if (message.contains(SocketTimeoutException.class.getName()) || message.contains(UnknownHostException.class.getName()) || message.contains(NetworkErrorException.class.getName())) {
                message = context.getString(R.string.internet_connection_unavailable);
            }

            android.widget.Toast toast = android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void displayError(Context context, int stringId) {

        String message = context.getString(stringId);

        if (message.contains(SocketTimeoutException.class.getName()) || message.contains(UnknownHostException.class.getName()) || message.contains(NetworkErrorException.class.getName())) {
            message = context.getString(R.string.internet_connection_unavailable);
        }

        android.widget.Toast toast = android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}

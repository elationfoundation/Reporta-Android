package com.iwmf.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.iwmf.R;
import com.iwmf.broadcasts.AlarmBroadcast;
import com.iwmf.http.PARAMS;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> Basic class to provide quick utilities throughout the application. </p>
 */
@SuppressWarnings("ALL")
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isValidEmail(String email) {

        try {
            if (email != null && email.length() > 0) {

                String EMAIL_PATTERN = "^[\\p{L}_A-Za-z0-9-\\+]+(\\.[\\p{L}_A-Za-z0-9-]+)*@" + "[\\p{L}A-Za-z0-9-]+(\\.[\\p{L}A-Za-z0-9]+)*(\\.[\\p{L}A-Za-z]{2,})$";

                Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(email);

                return matcher.matches();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidUsername(String username) {


        try {
            String USERNAME_PATTERN = "^[\\p{L}a-z0-9_]{3,25}$";

            Pattern pattern = Pattern.compile(USERNAME_PATTERN);
            Matcher matcher = pattern.matcher(username);

            return matcher.matches();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void changeLocale(Context c) {

        try {
            if (c == null) return;
            SharedPreferences sharedpreferences = c.getSharedPreferences("reportapref", Context.MODE_PRIVATE);
            ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
            changeLocale(ConstantData.LANGUAGE_CODE, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String capitalize(final String line) {
        if (line.length() > 1) {
            return Character.toUpperCase(line.charAt(0)) + line.substring(1);
        } else {
            return line;
        }
    }

    public static void changeLocale(String language, Context mContext) {

        try {

            Locale mLocale = new Locale("en");

            if (language.equals(Language_code.AR.name())) {

                mLocale = new Locale("ar");

            } else if (language.equals(Language_code.FR.name())) {

                mLocale = new Locale("fr");

            } else if (language.equals(Language_code.IW.name())) {

                mLocale = new Locale("iw");

				/*
                 mLocale = new Locale("he");
				 Note that Java uses several deprecated two-letter codes. The Hebrew ("he") language code is rewritten as "iw", Indonesian ("id") as "in", 
				 and Yiddish ("yi") as "ji". This rewriting happens even if you construct your own Locale object, not just for instances
				 returned by the various lookup methods.				 
				 */

            } else if (language.equals(Language_code.ES.name())) {

                mLocale = new Locale("es");

            } else if (language.equals(Language_code.TR.name())) {

                mLocale = new Locale("tr");
            }

            Locale.setDefault(mLocale);
            Configuration mConfiguration = new Configuration();
            mConfiguration.locale = mLocale;
            mContext.getResources().updateConfiguration(mConfiguration, mContext.getResources().getDisplayMetrics());
            // Logger.getInstance().appendLog(TAG + " :: Locale =" + mLocale.getLanguage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // validating password with retype password
    public static boolean isValidPassword(String pass) {

        return pass != null && pass.length() > 2;
    }

    public static boolean isValidStrongPassword(String pass, String language, String username) {

        try {

            if (pass.toLowerCase(Locale.getDefault()).contains(username.toLowerCase(Locale.getDefault()))) {
                return false;
            }

            String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,/,?].*$)";
            String PASSWORD_PATTERN;

            if (language.equals(Language_code.EN.name())) {

                PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[" + specialChars + "])(?=\\S+$).{8,25}$";

            } else {

                PASSWORD_PATTERN = "^(?=.*[\\p{L}0-9])(?=.*[\\p{L}A-Z])(?=.*[" + specialChars + "])(?=\\S+$).{8,25}$";
            }

            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(pass);

            return matcher.matches();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void setPendingCheckIn(long startTime, long currentTime, AlarmBroadcast alarmBroadcast, Context mContext) {
        if (startTime > currentTime) {
            long startOffsetReminder = startTime - currentTime;
            if (startOffsetReminder >= 30 * ConstantData.MINUTE_IN_MILISEC) {
                startOffsetReminder -= 10 * ConstantData.MINUTE_IN_MILISEC;
                Calendar mTempCalendar = Calendar.getInstance();
                mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + startOffsetReminder);
                alarmBroadcast.setAlarm(mContext, startOffsetReminder, ConstantData.ALARM_PENDING_REMINDER);
            }
        }
    }

    public static void setEndCheckIn(long endDateTemp, long currentTime, AlarmBroadcast alarmBroadcast, Context mContext) {
        if (endDateTemp > 0 && endDateTemp > currentTime) {
            alarmBroadcast.setAlarm(mContext, endDateTemp - Calendar.getInstance().getTimeInMillis(), ConstantData.ALARM_ENDTIME);
            Calendar mTempCalendar = Calendar.getInstance();
            mTempCalendar.setTimeInMillis(endDateTemp);
        }
    }

    public static void setMissedCheckIn(long startTime, long endDateTemp, long currentTime, long lastConfirmCheckinTime, int frequency, AlarmBroadcast alarmBroadcast, Context mContext) {
        long startOffsetFreq = 0;

        if (startTime > currentTime) {
            // Future date
            startOffsetFreq = startTime - currentTime;
        }
        long trigerDuration = startOffsetFreq + ((frequency + 1) * ConstantData.MINUTE_IN_MILISEC);
        if (startTime < currentTime) {
            trigerDuration = trigerDuration - (currentTime - lastConfirmCheckinTime);
        }

        Calendar mTempCalendar = Calendar.getInstance();
        mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDuration);

        if (mTempCalendar.getTimeInMillis() < endDateTemp || endDateTemp == 0) {
            alarmBroadcast.setAlarm(mContext, trigerDuration, ConstantData.ALARM_MISSED_CHECKIN);
        }
    }

    public static void setCheckInReminder(long startTime, long endDateTemp, long currentTime, long lastConfirmCheckinTime, int frequency, AlarmBroadcast alarmBroadcast, Context mContext) {

        long frequencyInMilli = frequency * ConstantData.MINUTE_IN_MILISEC;

        if (frequency >= 30 && (endDateTemp == 0 || endDateTemp > lastConfirmCheckinTime + frequencyInMilli)) {

            long startOffsetReminder;

            if (startTime > currentTime) {

                // Future date
                startOffsetReminder = startTime - currentTime;

                long trigerDuration = startOffsetReminder + ((frequency - 10) * ConstantData.MINUTE_IN_MILISEC);

                Calendar mTempCalendar = Calendar.getInstance();
                mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDuration);

                alarmBroadcast.setAlarm(mContext, trigerDuration, ConstantData.ALARM_REMINDER);

            } else {
                /**
                 * TimeStamp till last confirm
                 */
                long diff = currentTime - lastConfirmCheckinTime;


                if (diff < frequencyInMilli) {

                    // CheckIn still not missed
                    if (frequencyInMilli - diff > 10 * ConstantData.MINUTE_IN_MILISEC) {

                        // Difference is more than 10 minutes
                        long trigerDuration = ((frequency - 10) * ConstantData.MINUTE_IN_MILISEC - diff);

                        Calendar mTempCalendar = Calendar.getInstance();
                        mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDuration);

                        if (mTempCalendar.getTimeInMillis() < endDateTemp || endDateTemp == 0) {
                            alarmBroadcast.setAlarm(mContext, trigerDuration, ConstantData.ALARM_REMINDER);
                        }
                    }

                } else {
                    // Miss checkIn
                    CryptoManager.getInstance(mContext).getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, true).apply();
                }
            }
        }
    }

    public static void setCheckInFrequency(long startTime, long endDateTemp, long currentTime, long lastConfirmCheckinTime, int frequency, AlarmBroadcast alarmBroadcast, Context mContext) {

        long startOffsetFreq;
        long trigerDuration = 0;
        long frequencyInMilli = frequency * ConstantData.MINUTE_IN_MILISEC;

        if (startTime > currentTime) {
            // Future date
            startOffsetFreq = startTime - currentTime;
            trigerDuration = startOffsetFreq + ((frequency - 2) * ConstantData.MINUTE_IN_MILISEC);
        } else {
            /**
             * TimeStamp till last confirm
             */
            long diff = currentTime - lastConfirmCheckinTime;

            if (diff < frequencyInMilli) {
                // CheckIn still not missed

                if (frequencyInMilli - diff > 2 * ConstantData.MINUTE_IN_MILISEC) {
                    // Difference is more than 5 minutes
                    trigerDuration = ((frequency - 2) * ConstantData.MINUTE_IN_MILISEC - diff);
                }

            } else {

                // Miss checkIn
                CryptoManager.getInstance(mContext).getPrefs().edit().putBoolean(PARAMS.KEY_IS_MISSED_CHECKIN, true).apply();
            }
        }

        if (trigerDuration > 0) {
            Calendar mTempCalendar = Calendar.getInstance();
            mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDuration);

            if (mTempCalendar.getTimeInMillis() < endDateTemp || endDateTemp == 0) {
                alarmBroadcast.setAlarm(mContext, trigerDuration, ConstantData.ALARM_FREQ);
            }
        }
    }

    public static boolean checkDigit(String email) {

        return email.matches("[0-9]+");
    }

    public static boolean isEmpty(String text) {

        return !(text != null && !text.equalsIgnoreCase("null") && !text.trim().equals(""));
    }


    public static boolean isOnline(Context mContext) {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        boolean is = (netInfo != null && netInfo.isConnectedOrConnecting());
        if (!is) {
            Toast.displayError(mContext, mContext.getString(R.string.internet_connection_unavailable));
        }

        return is;
    }

    public static void clearAllNotifications(Context c) {

        try {

            AlarmBroadcast mAlarmBroadcast = new AlarmBroadcast();
            mAlarmBroadcast.cancelAlarm(c, ConstantData.ALARM_REMINDER);
            mAlarmBroadcast.cancelAlarm(c, ConstantData.ALARM_FREQ);
            mAlarmBroadcast.cancelAlarm(c, ConstantData.ALARM_MISSED_CHECKIN);
            mAlarmBroadcast.cancelAlarm(c, ConstantData.ALARM_ENDTIME);
            mAlarmBroadcast.cancelAlarm(c, ConstantData.ALARM_PENDING_REMINDER);

            NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context c, View v) {

        if (v != null && v instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            v.clearFocus();
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void writeToFile(File myFile, String data, Context mContext) {

        FileWriter writer = null;

        try {

            writer = new FileWriter(myFile);
            writer.write(data);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String readFromFile(File filename) {

        BufferedReader reader = null;
        StringBuilder builder;
        String str = "";

        try {

            reader = new BufferedReader(new FileReader(filename));
            builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            str = builder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return str;
    }

    public static String getMediaAES(File file) {

        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            org.apache.commons.codec.binary.Base64 b = new org.apache.commons.codec.binary.Base64();
            bytes = b.encode(bytes);
            String fileDataStr = new String(bytes);

            return AESCrypt.encrypt(fileDataStr, ConstantData.FIXKEY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String setServerString(String str) {

        String myString;

        myString = str.replace("`", "%60");
        myString = myString.replace("~", "%7E");
        myString = myString.replace("^", "%5E");
        myString = myString.replace("+", "%2B");
        myString = myString.replace("-", "%2D");
        myString = myString.replace("_", "%5F");
        myString = myString.replace("*", "%2A");
        myString = myString.replace(",", "%2C");
        myString = myString.replace(".", "%2E");
        myString = myString.replace("\'", "%27");
        // myString = myString.replace("","%2F");
        myString = myString.replace(";", "%3B");
        myString = myString.replace("=", "%3D");
        myString = myString.replace("?", "%3F");
        myString = myString.replace("@", "%40");
        myString = myString.replace(" ", "%20");
        myString = myString.replace("\t", "%%%");
        myString = myString.replace("$", "%24");
        myString = myString.replace("#", "%23");
        myString = myString.replace("<", "%3C");
        myString = myString.replace(">", "%3E");
        myString = myString.replace("\n", "@@@");
        myString = myString.replace("(", "%28");
        myString = myString.replace(")", "%29");
        myString = myString.replace("{", "%7B");
        myString = myString.replace("}", "%7D");
        myString = myString.replace("[", "%5B");
        myString = myString.replace("]", "%5D");
        myString = myString.replace("!", "%21");
        myString = myString.replace("&", "%26");
        myString = myString.replace("\"", "%22");
        myString = myString.replace("\\", "%68");

        return myString;
    }

    public static String setClientString(String str) {

        String myString;

        myString = str.replace("%60", "`");
        myString = myString.replace("%7E", "~");
        myString = myString.replace("%5E", "^");
        myString = myString.replace("%2B", "+");
        myString = myString.replace("%2D", "-");
        myString = myString.replace("%5F", "_");
        myString = myString.replace("%2A", "*");
        myString = myString.replace("%2C", ",");
        myString = myString.replace("%2E", ".");
        myString = myString.replace("%27", "\'");
        myString = myString.replace("%2F", "");
        myString = myString.replace("%3B", ";");
        myString = myString.replace("%3D", "=");
        myString = myString.replace("%3F", "?");
        myString = myString.replace("%40", "@");
        myString = myString.replace("%20", " ");
        myString = myString.replace("%%%", "\t");
        myString = myString.replace("%24", "$");
        myString = myString.replace("%23", "#");
        myString = myString.replace("%3C", "<");
        myString = myString.replace("%3E", ">");
        myString = myString.replace("@@@", "\n");
        myString = myString.replace("%28", "(");
        myString = myString.replace("%29", ")");
        myString = myString.replace("%7B", "{");
        myString = myString.replace("%7D", "}");
        myString = myString.replace("%5B", "[");
        myString = myString.replace("%5D", "]");
        myString = myString.replace("%21", "!");
        myString = myString.replace("%26", "&");
        myString = myString.replace("%22", "\"");
        myString = myString.replace("%68", "\\");

        return myString;
    }
}

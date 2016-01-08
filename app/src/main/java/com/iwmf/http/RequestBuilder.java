package com.iwmf.http;

import com.iwmf.data.PendingMediaData;
import com.iwmf.utils.AESCrypt;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p> Contains all Web service APIs.
 * Also generate webservice requests to send to server. </p>
 */
@SuppressWarnings("ALL")
public class RequestBuilder extends PARAMS {

    public static final String SERVER_URL = "";

    public static final String ABOUT_REPORTA_URL = SERVER_URL + "aboutreporta";
    public static final String TERMS_OF_USE_URL = SERVER_URL + "termsandconditions";

    private static final String SERVER_URL_API = SERVER_URL + "/";

    public static final String WS_CREATECONTACTCIRCLE = SERVER_URL_API + "contact/createcontactcircle";
    public static final String WS_CREATESINGLECONTACT = SERVER_URL_API + "contact/createsinglecontact";
    public static final String WS_ALLCIRCLEWITHSTATUS = SERVER_URL_API + "contact/allcirclewithstatus";
    public static final String WS_DELETECONTACT = SERVER_URL_API + "contact/deletecontact";
    public static final String WS_ALLCIRCLEWITHCONTACTS = SERVER_URL_API + "contact/allcirclewithcontacts";
    public static final String WS_ALLCONTACTS = SERVER_URL_API + "contact/allcontacts";
    public static final String WS_ALLEXISTINGCONTACTS = SERVER_URL_API + "contact/allexistingcontacts";
    public static final String WS_UPDATECONTACTLIST = SERVER_URL_API + "contact/updatecontactlist";
    public static final String WS_CHANGEDEFAULTSTATUS = SERVER_URL_API + "contact/changedefaultstatus";
    public static final String WS_DELETECIRCLE = SERVER_URL_API + "contact/deletecircle";
    public static final String WS_SEND_ALERT = SERVER_URL_API + "checkin/sendalert";
    public static final String WS_SEND_SOS = SERVER_URL_API + "checkin/sos";
    public static final String WS_UNLOCK_APP = SERVER_URL_API + "checkin/unlockapp";
    public static final String WS_CREATE_CHECKIN = SERVER_URL_API + "checkin/createcheckin";
    public static final String WS_ADD_MEDIA = SERVER_URL_API + "media/addmedia";
    public static final String WS_UPDATE_CHECKIN = SERVER_URL_API + "checkin/updatecheckinstatus";
    public static final String WS_SIGNOUT = SERVER_URL_API + "user/signout";
    public static final String WS_FORGOTPASSWORD = SERVER_URL_API + "user/forgotpassword";
    public static final String WS_LOGIN = SERVER_URL_API + "user/login";
    public static final String WS_CREATEUSER = SERVER_URL_API + "user/createuser";
    public static final String WS_CHECKUSERNAMEEMAIL = SERVER_URL_API + "user/checkusernameemail";
    public static final String WS_RESETPASSWORD = SERVER_URL_API + "user/resetpassword";
    public static final String WS_SEND_MAIL_MEDIA = SERVER_URL_API + "checkin/sendmailwithmedia";
    private static final String TAG = RequestBuilder.class.getSimpleName();

    public static String getCheckExistUserNameAndEmailRequest(String username, String email, String language_code) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_USERNAME, username);
            jsonObject.put(TAG_EMAIL, email);
            jsonObject.put(TAG_LANGUAGE_CODE, language_code);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getLoginRequest(String username, String password, String language_code, String forceLogin) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_USERNAME, username);
            jsonObject.put(TAG_PASSWORD, password);
            jsonObject.put(TAG_LANGUAGE_CODE, language_code);
            jsonObject.put(TAG_DEVICETOKEN, ConstantData.REGISTRATIONID);
            jsonObject.put(TAG_DEVICETYPE, ConstantData.DEVICE_TYPE_ANDROID);

            jsonObject.put(TAG_FORCELOGIN, forceLogin);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getCreateSingleContactRequest(String contact_id, String firstname, String lastname, String mobile, String emails, String sos_enabled, String associated_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CONTACT_ID, contact_id);
            jsonObject.put(TAG_FIRSTNAME, firstname);
            jsonObject.put(TAG_LASTNAME, lastname);
            jsonObject.put(TAG_MOBILE, mobile);
            jsonObject.put(TAG_EMAILS, emails);
            jsonObject.put(TAG_SOS_ENABLED, sos_enabled);
            jsonObject.put(TAG_ASSOCIATED_ID, associated_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getCreateUserRequest(String username, String password, String email, String firstname, String lastname, String language, String language_code, String phone, String jobtitle, String affiliation_id, String chkbx_freelancer, String origin_country, String working_country, String devicetoken, String devicetype, String gender, String gender_type) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(TAG_USERNAME, username);
            if ("0".equals("1")) {
                jsonObject.put(TAG_PASSWORD, password);
            }
            jsonObject.put(TAG_EMAIL, email);
            jsonObject.put(TAG_FIRSTNAME, firstname);
            jsonObject.put(TAG_LASTNAME, lastname);
            jsonObject.put(TAG_LANGUAGE, language);
            jsonObject.put(TAG_LANGUAGE_CODE, language_code);
            jsonObject.put(TAG_PHONE, phone);
            jsonObject.put(TAG_JOBTITLE, jobtitle);
            jsonObject.put(TAG_GENDER, gender);
            jsonObject.put(TAG_GENDER_TYPE, gender_type);
            jsonObject.put(TAG_AFFILIATION_ID, affiliation_id);
            jsonObject.put(TAG_FREELANCER, chkbx_freelancer);
            jsonObject.put(TAG_ORIGIN_COUNTRY, origin_country);
            jsonObject.put(TAG_WORKING_COUNTRY, working_country);
            jsonObject.put(TAG_SENDMAIL, 1);
            jsonObject.put(TAG_DEVICETOKEN, ConstantData.REGISTRATIONID);
            jsonObject.put(TAG_ISCREATEUSER, "0");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getSignout() {

        JSONObject jsonObject = new JSONObject();
        //  creates blank json object with not key/value
        return jsonObject.toString();
    }

    public static String getForgotpassword(String email, String language_code) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_EMAIL, email);
            jsonObject.put(TAG_LANGUAGE_CODE, language_code);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getCreateCircleRequest(String list_id, String circle, String listname) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_LIST_ID, list_id);
            jsonObject.put(TAG_CIRCLE, circle);
            jsonObject.put(TAG_LISTNAME, listname);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getAllContactListRequest() {

        JSONObject jsonObject = new JSONObject();

        // creates blank json object with not key/value

        return jsonObject.toString();
    }

    public static String getAllCircleWithStatusRequest(String contact_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CONTACT_ID, contact_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getResetPasswordRequest(String oldpassword, String newPassword) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_OLDPASSWORD, oldpassword);
            jsonObject.put(TAG_NEWPASSWORD, newPassword);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getAllExistingContactsRequest(String contactlist_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CONTACTLIST_ID, contactlist_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getUpdateContactListRequest(String contactlist_id, String contact_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CONTACT_ID, contact_id);
            jsonObject.put(TAG_CONTACTLIST_ID, contactlist_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getAllCircleContactListRequest() {

        JSONObject jsonObject = new JSONObject();

        // creates blank json object with not key/value

        return jsonObject.toString();
    }

    public static String getCircleByCircleIdRequest(String circle) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CIRCLE, circle);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getDeleteContactlistRequest(String contactlist_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CONTACTLIST_ID, contactlist_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getDeleteContactRequest(String contact_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CONTACT_ID, contact_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getChangeDefaultStatusRequest(String list_id, String circle, String defaultstatus) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_LIST_ID, list_id);
            jsonObject.put(TAG_CIRCLE, circle);
            jsonObject.put(TAG_DEFAULTSTATUS, defaultstatus);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getSendAlertRequest(String situation, String description, String location, double latitude, double longitude, String contactList, String time, String fb_token, String twitter_token, String twitter_token_secret, int mediaCount) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_SITUATION, situation);
            jsonObject.put(TAG_DESCRIPTION, description);
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_TIME, time);
            jsonObject.put(TAG_LOCATION, location);
            jsonObject.put(TAG_LATITUDE, latitude);
            jsonObject.put(TAG_LONGITUDE, longitude);
            jsonObject.put(TAG_SAFETYCHECKIN, 0);
            jsonObject.put(TAG_ALERT_ID, 0);
            jsonObject.put(TAG_MEDIA_COUNT, mediaCount);
            jsonObject.put(TAG_CONTACTLIST, contactList);
            jsonObject.put(TAG_FB_TOKEN, fb_token);
            jsonObject.put(TAG_TWITTER_TOKEN, twitter_token);
            jsonObject.put(TAG_TWITTER_TOKEN_SECRET, twitter_token_secret);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getSendSOSRequest(double latitude, double longitude) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_TIME, getTime());
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_LATITUDE, latitude);
            jsonObject.put(TAG_LONGITUDE, longitude);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getUnlockAppRequest(String password, int sosId, String otp, double latitude, double longitude, String checkin_id) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_PASSWORD, password);
            jsonObject.put(TAG_SOS_ID, sosId);
            jsonObject.put(TAG_OTP, otp);
            jsonObject.put(TAG_TIME, getTime());
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_LATITUDE, latitude);
            jsonObject.put(TAG_LONGITUDE, longitude);
            jsonObject.put(TAG_CHECKIN_ID, checkin_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getCreateEditCheckInWhenConfirmRequest(String time, String description, String location, double latitude, String starttime, int frequency, String message_email, String receiveprompt, String message_social, String message_sms, double longitude, int checkin_id, String endtime, int status, String contactlist, String fb_token, String twitter_token, String twitter_token_secret, boolean ismadeanychanges) {

        JSONObject jsonObject = new JSONObject();

        try {


            jsonObject.put(TAG_CHECKIN_ID, checkin_id);
            jsonObject.put(TAG_DESCRIPTION, description);
            jsonObject.put(TAG_LATITUDE, latitude);
            jsonObject.put(TAG_LONGITUDE, longitude);
            jsonObject.put(TAG_STARTTIME, starttime);
            jsonObject.put(TAG_ENDTIME, endtime);
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_TIME, time);
            jsonObject.put(TAG_MESSAGE_SMS, message_sms);
            jsonObject.put(TAG_MESSAGE_EMAIL, message_email);
            jsonObject.put(TAG_MESSAGE_SOCIAL, message_social);
            jsonObject.put(TAG_RECEIVEPROMPT, receiveprompt);
            jsonObject.put(TAG_FREQUENCY, frequency);
            jsonObject.put(TAG_STATUS, status);
            jsonObject.put(TAG_FB_TOKEN, fb_token);
            jsonObject.put(TAG_TWITTER_TOKEN, twitter_token);
            jsonObject.put(TAG_TWITTER_TOKEN_SECRET, twitter_token_secret);
            jsonObject.put(TAG_CONTACTLIST, contactlist);
            jsonObject.put(TAG_LOCATION, location);
            jsonObject.put(TAG_DEVICETOKEN, ConstantData.REGISTRATIONID);
            jsonObject.put(TAG_DEVICETYPE, ConstantData.DEVICE_TYPE_ANDROID);
            jsonObject.put(TAG_TIME, time);
            jsonObject.put(TAG_LONGITUDE, ConstantData.LONGITUDE);
            jsonObject.put(TAG_CHECKIN_ID, checkin_id);
            jsonObject.put(TAG_STATUS, status);
            jsonObject.put(TAG_LATITUDE, ConstantData.LATITUDE);
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_PASSWORD, "");
            jsonObject.put(TAG_ISMADEANYCHANGES, ismadeanychanges ? "1" : "0");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getCreateCheckInRequest(String time, String description, String location, double latitude, String starttime, int frequency, String message_email, String receiveprompt, String message_social, String message_sms, double longitude, int checkin_id, String endtime, int status, String contactlist, String fb_token, String twitter_token, String twitter_token_secret) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_CHECKIN_ID, checkin_id);
            jsonObject.put(TAG_DESCRIPTION, description);
            jsonObject.put(TAG_LATITUDE, latitude);
            jsonObject.put(TAG_LONGITUDE, longitude);
            jsonObject.put(TAG_STARTTIME, starttime);
            jsonObject.put(TAG_ENDTIME, endtime);
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_TIME, time);
            jsonObject.put(TAG_MESSAGE_SMS, message_sms);
            jsonObject.put(TAG_MESSAGE_EMAIL, message_email);
            jsonObject.put(TAG_MESSAGE_SOCIAL, message_social);
            jsonObject.put(TAG_RECEIVEPROMPT, receiveprompt);
            jsonObject.put(TAG_FREQUENCY, frequency);
            jsonObject.put(TAG_STATUS, status);
            jsonObject.put(TAG_FB_TOKEN, fb_token);
            jsonObject.put(TAG_TWITTER_TOKEN, twitter_token);
            jsonObject.put(TAG_TWITTER_TOKEN_SECRET, twitter_token_secret);
            jsonObject.put(TAG_CONTACTLIST, contactlist);
            jsonObject.put(TAG_LOCATION, location);
            jsonObject.put(TAG_DEVICETOKEN, ConstantData.REGISTRATIONID);
            jsonObject.put(TAG_DEVICETYPE, ConstantData.DEVICE_TYPE_ANDROID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getMediaRequest(PendingMediaData data) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(TAG_TIME, getTime(data.getCreatedDate()));
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_LATITUDE, data.getLatitude());
            jsonObject.put(TAG_LONGITUDE, data.getLongitude());
            jsonObject.put(TAG_MEDIAFILE, getMediaBase64(data.getFilePath()));
            jsonObject.put(TAG_MEDIATYPE, data.getMediaType());
            jsonObject.put(TAG_TABLE_ID, data.getTableId());
            jsonObject.put(TAG_FOREIGN_ID, data.getRef_id());

            try {

                File mFile = new File(data.getFilePath());

                jsonObject.put(TAG_EXTENSION, mFile.getName().substring(mFile.getName().indexOf(".") + 1));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getSendMailWithMediaInRequest(int refId, int table_id) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(TAG_TABLE_ID, table_id);
            jsonObject.put(TAG_FOREIGN_ID, refId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public static String getUpdateCheckInRequest(String time, int checkin_id, int status, boolean ismadeanychanges, String password) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(TAG_TIME, time);
            jsonObject.put(TAG_LONGITUDE, ConstantData.LONGITUDE);
            jsonObject.put(TAG_CHECKIN_ID, checkin_id);
            jsonObject.put(TAG_STATUS, status);
            jsonObject.put(TAG_LATITUDE, ConstantData.LATITUDE);
            jsonObject.put(TAG_TIMEZONE_ID, getTimeZoneId());
            jsonObject.put(TAG_PASSWORD, password);
            jsonObject.put(TAG_ISMADEANYCHANGES, ismadeanychanges ? "1" : "0");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private static String getTimeZoneId() {

        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    private static String getTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private static String getTime(long date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    private static String getMediaBase64(String filePath) {

        try {
            File file = new File(filePath);

            String fileData = Utils.readFromFile(file);

            String base64;
            base64 = AESCrypt.decrypt(fileData, ConstantData.FIXKEY);
            return base64;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
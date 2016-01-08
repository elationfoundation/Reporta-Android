package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store data about checkin. </p>
 */
@SuppressWarnings("ALL")
public class AddCheckinData implements Serializable {

    private static final long serialVersionUID = 2732107655041053534L;

    private int checkin_id = 0;
    private String user_id = "";
    private String description = "";
    private String location = "";
    private double latitude = 0;
    private double longitude = 0;
    private long starttime = 0;
    private long endtime = 0;
    private String timezone_id = "";
    private long time = 0;
    private String message_sms = "";
    private String message_email = "";
    private String message_social = "";
    private String receiveprompt = "3";
    private int frequency = 30;
    private int staus;
    private String contactlist = "";
    private String fb_token = "";
    private String twitter_token = "";
    private String twitter_token_secret = "";
    private long lastConfirmTime = 0;
    private int custom_frequency = 0;
    private boolean startNow = false;

    public int getCheckin_id() {

        return checkin_id;
    }

    public void setCheckin_id(int checkin_id) {

        this.checkin_id = checkin_id;
    }

    public String getUser_id() {

        return user_id;
    }

    public void setUser_id(String user_id) {

        this.user_id = user_id;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {

        this.location = location;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public long getStarttime() {

        return starttime;
    }

    public void setStarttime(long starttime) {

        this.starttime = starttime;
    }

    public long getEndtime() {

        return endtime;
    }

    public void setEndtime(long endtime) {

        this.endtime = endtime;
    }

    public String getTimezone_id() {

        return timezone_id;
    }

    public void setTimezone_id(String timezone_id) {

        this.timezone_id = timezone_id;
    }

    public long getTime() {

        return time;
    }

    public void setTime(long time) {

        this.time = time;
    }

    public String getMessage_sms() {

        return message_sms;
    }

    public void setMessage_sms(String message_sms) {

        this.message_sms = message_sms;
    }

    public String getMessage_email() {

        return message_email;
    }

    public void setMessage_email(String message_email) {

        this.message_email = message_email;
    }

    public String getMessage_social() {

        return message_social;
    }

    public void setMessage_social(String message_social) {

        this.message_social = message_social;
    }

    public String getReceiveprompt() {

        return receiveprompt;
    }

    public void setReceiveprompt(String receiveprompt) {

        this.receiveprompt = receiveprompt;
    }

    public int getFrequency() {

        return frequency;
    }

    public void setFrequency(int frequency) {

        this.frequency = frequency;
    }

    public int getStaus() {

        return staus;
    }

    public void setStaus(int staus) {

        this.staus = staus;
    }

    public String getContactlist() {

        return contactlist;
    }

    public void setContactlist(String contactlist) {

        this.contactlist = contactlist;
    }

    public String getFb_token() {

        return fb_token;
    }

    public void setFb_token(String fb_token) {

        this.fb_token = fb_token;
    }

    public String getTwitter_token() {

        return twitter_token;
    }

    public void setTwitter_token(String twitter_token) {

        this.twitter_token = twitter_token;
    }

    public String getTwitter_token_secret() {

        return twitter_token_secret;
    }

    public void setTwitter_token_secret(String twitter_token_secret) {

        this.twitter_token_secret = twitter_token_secret;
    }

    public long getLastConfirmTime() {

        return lastConfirmTime;
    }

    public void setLastConfirmTime(long lastConfirmTime) {

        this.lastConfirmTime = lastConfirmTime;
    }

    public int getCustom_frequency() {

        return custom_frequency;
    }

    public void setCustom_frequency(int custom_frequency) {

        this.custom_frequency = custom_frequency;
    }

    public boolean isStartNow() {

        return startNow;
    }

    public void setStartNow(boolean startNow) {

        this.startNow = startNow;
    }

}

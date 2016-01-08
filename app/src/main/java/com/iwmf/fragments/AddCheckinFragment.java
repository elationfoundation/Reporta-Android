package com.iwmf.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iwmf.CheckinEditMessageActivity;
import com.iwmf.CheckinEndTimeActivity;
import com.iwmf.CheckinFrequencyActivity;
import com.iwmf.CheckinSituationMessageActivity;
import com.iwmf.MediaActivity;
import com.iwmf.PickLocationActivity;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.broadcasts.AlarmBroadcast;
import com.iwmf.data.AddCheckinData;
import com.iwmf.data.ContactListData;
import com.iwmf.data.PendingMediaData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.DBHelper;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <p> Add new checkin by specifying time.
 * User must check in within time otherwise app will be locked and his/her circle will be notified.
 * User can edit and delete checkin.
 * </p>
 */
@SuppressWarnings("ALL")
public class AddCheckinFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = AddCheckinFragment.class.getSimpleName();

    private final static int INTENT_ADD_MEDIA = 1;
    private final static int INTENT_PICK_LOCATION = 2;
    private final static int INTENT_ADD_SITUATION_MESSAGE = 3;
    private final static int INTENT_SET_END_TIME = 4;
    private final static int INTENT_EDIT_MESSAGE = 5;
    private final static int INTENT_FREQUENCY = 6;
    private final static int INTENT_SET_START_TIME = 7;
    private final static int INTENT_ADD_OTHER_CONTACT = 8;

    private final static int REQUEST_CODE_PRIVATE = 110;
    private final static int REQUEST_CODE_PUBLIC = 111;
    private final static int REQUEST_CODE_SOCIAL = 112;

    private String private_contactlist_id = "";
    private String public_contactlist_id = "";
    private String social_twitter_token_secret = "";
    private String social_twitter_token = "";
    private String social_facebook = "";

    private boolean isMadeAnyChanges = false;

    private AlarmBroadcast mAlarmBroadcast = null;

    // *****

    private LinearLayout lnrEditTop = null;
    private TextView lblStartTime = null, txtStartTime = null, txtChooseLocation = null, lblPrivate = null, txtEdit = null, lblPublic = null, txtPublic = null, lblSocial = null, txtSocial = null, lblCheckinFrequency = null, lblCheckinFreqOption = null, lblEditAlertMsg = null, lblDateTime = null, lblAddMedia = null;
    private EditText edtSituation = null;
    private RelativeLayout rltLocation = null, rltPrivate = null, rltCheckinFrequency = null, rlt_public, rltsocial, rltEditAlertMsg, rltSetTime, rltMedia;
    private Button btnStartNow = null, btnStartLater = null, btnConfirmNow = null, btnSave = null, btnCancel = null;

    // *****

    private AddCheckinData addCheckinData;
    private ArrayList<PendingMediaData> listPendingMedia = null;

    private double latitude = 0, longitude = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Logger.getInstance().appendLog(TAG + " :: onCreate");
        super.onCreate(savedInstanceState);

        mAlarmBroadcast = new AlarmBroadcast();

        try {

            if (ConstantData.DB == null || !ConstantData.DB.isOpen()) {
                ConstantData.DB = new DBHelper(getActivity());
                ConstantData.DB.open();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Logger.getInstance().appendLog(TAG + " :: onCreateView");
        View mView = inflater.inflate(R.layout.fragment_add_checkin, container, false);

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        lnrEditTop = (LinearLayout) v.findViewById(R.id.lnrEditTop);
        rlt_public = (RelativeLayout) v.findViewById(R.id.rlt_public);
        rltsocial = (RelativeLayout) v.findViewById(R.id.rltsocial);
        rltEditAlertMsg = (RelativeLayout) v.findViewById(R.id.rltEditAlertMsg);
        rltSetTime = (RelativeLayout) v.findViewById(R.id.rltSetTime);
        rltMedia = (RelativeLayout) v.findViewById(R.id.rltMedia);

        lblStartTime = (TextView) v.findViewById(R.id.lblStartTime);
        txtStartTime = (TextView) v.findViewById(R.id.txtStartTime);
        txtChooseLocation = (TextView) v.findViewById(R.id.txtChooseLocation);
        lblPrivate = (TextView) v.findViewById(R.id.lblPrivate);
        txtEdit = (TextView) v.findViewById(R.id.txtEdit);
        lblPublic = (TextView) v.findViewById(R.id.lblPublic);
        txtPublic = (TextView) v.findViewById(R.id.txtPublic);
        lblSocial = (TextView) v.findViewById(R.id.lblSocial);
        txtSocial = (TextView) v.findViewById(R.id.txtSocial);
        lblCheckinFrequency = (TextView) v.findViewById(R.id.lblCheckinFrequency);
        lblCheckinFreqOption = (TextView) v.findViewById(R.id.lblCheckinFreqOption);
        lblEditAlertMsg = (TextView) v.findViewById(R.id.lblEditAlertMsg);
        lblDateTime = (TextView) v.findViewById(R.id.lblDateTime);
        lblAddMedia = (TextView) v.findViewById(R.id.lblAddMedia);

        edtSituation = (EditText) v.findViewById(R.id.edtSituation);

        rltLocation = (RelativeLayout) v.findViewById(R.id.rltLocation);
        rltPrivate = (RelativeLayout) v.findViewById(R.id.rltPrivate);
        rltCheckinFrequency = (RelativeLayout) v.findViewById(R.id.rltCheckinFrequency);

        btnStartNow = (Button) v.findViewById(R.id.btnStartNow);
        btnStartLater = (Button) v.findViewById(R.id.btnStartLater);
        btnConfirmNow = (Button) v.findViewById(R.id.btnConfirmNow);
        btnSave = (Button) v.findViewById(R.id.btnSave);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);

        rlt_public.setOnClickListener(this);
        rltsocial.setOnClickListener(this);
        rltPrivate.setOnClickListener(this);

        edtSituation.setOnClickListener(this);
        rltLocation.setOnClickListener(this);
        btnStartNow.setOnClickListener(this);
        btnStartLater.setOnClickListener(this);
        rltMedia.setOnClickListener(this);
        rltEditAlertMsg.setOnClickListener(this);
        rltSetTime.setOnClickListener(this);
        rltCheckinFrequency.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirmNow.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        refreshCheckin();

    }

    private void refreshCheckin() {

        long endDateTemp = prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();

        if (addCheckinData == null) {
            addCheckinData = new AddCheckinData();
        }

        if (prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0) > 0 && (endDateTemp == 0 || endDateTemp > currentTime)) {
            getCheckIn();
            updateUI();
            lnrEditTop.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnConfirmNow.setVisibility(View.VISIBLE);
            btnStartLater.setVisibility(View.GONE);
            btnStartNow.setVisibility(View.GONE);

            if (addCheckinData.getEndtime() > 0) {
                lblDateTime.setText(getFormatedTime(new Date(addCheckinData.getEndtime())));
            }

            if (addCheckinData.getLastConfirmTime() > Calendar.getInstance().getTimeInMillis()) {
                lblStartTime.setText(getString(R.string.check_start_message));
                txtStartTime.setText(getFormatedTime(new Date(addCheckinData.getLastConfirmTime())));
                btnConfirmNow.setText(R.string.start_now);

            } else {
                lblStartTime.setText(getString(R.string.your_next_conf));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(addCheckinData.getLastConfirmTime());
                calendar.add(Calendar.MINUTE, addCheckinData.getFrequency());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() > endDateTemp && endDateTemp > 0) {
                    calendar.setTimeInMillis(endDateTemp);
                    lblStartTime.setText(getString(R.string.checkin_close_at));
                }

                txtStartTime.setText(getFormatedTime(calendar.getTime()));
            }

        } else {
            removeCheckIn();
            lnrEditTop.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnConfirmNow.setVisibility(View.GONE);
            btnStartLater.setVisibility(View.VISIBLE);
            btnStartNow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // Logger.getInstance().appendLog(TAG + " :: onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getContactList();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edtSituation:

                Intent intent = new Intent(getActivity(), CheckinSituationMessageActivity.class);
                intent.putExtra("value", edtSituation.getText().toString().trim());
                intent.putExtra("title", getString(R.string.check_in));
                startActivityForResult(intent, INTENT_ADD_SITUATION_MESSAGE);

                break;

            case R.id.rltLocation:

                intent = new Intent(getActivity(), PickLocationActivity.class);
                intent.putExtra("title", getString(R.string.check_in));
                intent.putExtra("address", txtChooseLocation.getText().toString().trim());
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, INTENT_PICK_LOCATION);

                break;

            case R.id.rltPrivate:

                Fragment mFragment = AddCircleFragment.getInstance(SelectedScreen.PRIVATE, false, private_contactlist_id);
                mFragment.setTargetFragment(this, REQUEST_CODE_PRIVATE);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddCircleFragment.class.getSimpleName()).commit();

                break;

            case R.id.rlt_public:

                mFragment = AddCircleFragment.getInstance(SelectedScreen.PUBLIC, false, public_contactlist_id);
                mFragment.setTargetFragment(this, REQUEST_CODE_PUBLIC);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddCircleFragment.class.getSimpleName()).commit();

                break;

            case R.id.rltsocial:

                int which = 0;
                if (!Utils.isEmpty(social_facebook) && Utils.isEmpty(social_twitter_token) && Utils.isEmpty(social_twitter_token_secret)) {
                    which = 1;
                } else if (Utils.isEmpty(social_facebook) && !Utils.isEmpty(social_twitter_token) && !Utils.isEmpty(social_twitter_token_secret)) {
                    which = 2;
                } else if (!Utils.isEmpty(social_facebook) && !Utils.isEmpty(social_twitter_token) && !Utils.isEmpty(social_twitter_token_secret)) {
                    which = 3;
                }

                mFragment = SocialCircleFragment.getInstance(false, which);
                mFragment.setTargetFragment(this, REQUEST_CODE_SOCIAL);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment, SocialCircleFragment.class.getSimpleName()).addToBackStack(SocialCircleFragment.class.getSimpleName()).commit();

                break;

            case R.id.btnStartNow:

                addCheckinData.setStaus(1);

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);

                addCheckinData.setTime(mCalendar.getTimeInMillis());
                addCheckinData.setStarttime(mCalendar.getTimeInMillis());
                addCheckinData.setLastConfirmTime(mCalendar.getTimeInMillis());
                addCheckinData.setStartNow(true);
                if (checkValidation()) {
                    createCheckIN();
                }

                break;

            case R.id.btnStartLater:

                intent = new Intent(getActivity(), CheckinEndTimeActivity.class);
                intent.putExtra("date", addCheckinData.getStarttime());
                intent.putExtra("flag", true);
                startActivityForResult(intent, INTENT_SET_START_TIME);

                break;

            case R.id.rltMedia:

                intent = new Intent(getActivity(), MediaActivity.class);
                startActivityForResult(intent, INTENT_ADD_MEDIA);

                break;

            case R.id.rltEditAlertMsg:

                intent = new Intent(getActivity(), CheckinEditMessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", addCheckinData.getMessage_email());
                bundle.putString("sms", addCheckinData.getMessage_sms());
                bundle.putString("social", addCheckinData.getMessage_social());
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, INTENT_EDIT_MESSAGE);

                break;

            case R.id.rltSetTime:

                intent = new Intent(getActivity(), CheckinEndTimeActivity.class);
                intent.putExtra("date", addCheckinData.getEndtime());
                intent.putExtra("flag", false);
                startActivityForResult(intent, INTENT_SET_END_TIME);

                break;

            case R.id.rltCheckinFrequency:

                intent = new Intent(getActivity(), CheckinFrequencyActivity.class);
                bundle = new Bundle();
                bundle.putInt("custom", addCheckinData.getCustom_frequency());
                bundle.putInt("frequency", addCheckinData.getFrequency());
                bundle.putString("receiveprompt", addCheckinData.getReceiveprompt());
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, INTENT_FREQUENCY);

                break;

            case INTENT_ADD_OTHER_CONTACT:
                // getContactList();
                break;

            case R.id.btnSave:

                if (checkValidation()) {

                    mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    addCheckinData.setTime(mCalendar.getTimeInMillis());
                    addCheckinData.setStartNow(false);

                    if (mCalendar.getTimeInMillis() < addCheckinData.getStarttime()) {
                        addCheckinData.setStaus(0);
                    } else {
                        addCheckinData.setStaus(2);
                    }

                    createCheckIN();
                }

                break;

            case R.id.btnCancel:

                ((BaseAppCompatActivity) getActivity()).displayCancelCheckIn(addCheckinData.getCheckin_id());

                deleteAllMedia();

                break;
            case R.id.btnConfirmNow:

                isMadeAnyChanges = checkIsMadeAnyChanges();

                if (isMadeAnyChanges) {

                    saveChangesDialog();


                } else {

                    updateCheckRequest();
                }

                break;
        }

        getActivity().overridePendingTransition(R.anim.enter, R.anim.no_anim);
    }

    private boolean checkIsMadeAnyChanges() {

        try {
            AddCheckinData mAddCheckinData = new AddCheckinData();

            prefManager.getPrefs().getBoolean(PARAMS.KEY_SOS_STATUS, false);

            mAddCheckinData.setDescription(prefManager.getPrefs().getString(PARAMS.TAG_DESCRIPTION, ""));
            mAddCheckinData.setStarttime(prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0));
            mAddCheckinData.setFrequency(prefManager.getPrefs().getInt(PARAMS.TAG_FREQUENCY, 0));
            mAddCheckinData.setMessage_email(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_EMAIL, ""));
            mAddCheckinData.setReceiveprompt(prefManager.getPrefs().getString(PARAMS.TAG_RECEIVEPROMPT, "3"));
            mAddCheckinData.setMessage_social(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_SOCIAL, ""));
            mAddCheckinData.setLongitude(Double.parseDouble(prefManager.getPrefs().getString(PARAMS.TAG_LONGITUDE, "0")));
            mAddCheckinData.setMessage_sms(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_SMS, ""));
            mAddCheckinData.setCheckin_id(prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0));
            mAddCheckinData.setEndtime(prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0));
            mAddCheckinData.setStaus(prefManager.getPrefs().getInt(PARAMS.TAG_STATUS, 0));
            mAddCheckinData.setContactlist(prefManager.getPrefs().getString(PARAMS.TAG_CONTACTLIST, ""));
            mAddCheckinData.setLocation(prefManager.getPrefs().getString(PARAMS.TAG_LOCATION, ""));
            mAddCheckinData.setLatitude(Double.parseDouble(prefManager.getPrefs().getString(PARAMS.TAG_LATITUDE, "0")));
            mAddCheckinData.setLastConfirmTime(prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, 0));


            if (!mAddCheckinData.getDescription().equals(addCheckinData.getDescription())) {
                return true;
            } else if (mAddCheckinData.getLatitude() != addCheckinData.getLatitude()) {
                return true;
            } else if (mAddCheckinData.getLongitude() != addCheckinData.getLongitude()) {
                return true;
            } else if (!mAddCheckinData.getContactlist().equals(addCheckinData.getContactlist())) {
                return true;
            } else if (!mAddCheckinData.getFb_token().equals(addCheckinData.getFb_token())) {
                return true;
            } else if (!mAddCheckinData.getTwitter_token().equals(addCheckinData.getTwitter_token())) {
                return true;
            } else if (!mAddCheckinData.getTwitter_token_secret().equals(addCheckinData.getTwitter_token_secret())) {
                return true;
            } else if ((mAddCheckinData.getCustom_frequency() > 0 ? mAddCheckinData.getCustom_frequency() : mAddCheckinData.getFrequency()) != (addCheckinData.getCustom_frequency() > 0 ? addCheckinData.getCustom_frequency() : addCheckinData.getFrequency())) {
                return true;
            } else if (!mAddCheckinData.getMessage_sms().equals(addCheckinData.getMessage_sms())) {
                return true;
            } else if (!mAddCheckinData.getMessage_email().equals(addCheckinData.getMessage_email())) {
                return true;
            } else if (!mAddCheckinData.getMessage_social().equals(addCheckinData.getMessage_social())) {
                return true;
            } else if (!getUTCTIME(mAddCheckinData.getEndtime()).equals(getUTCTIME(addCheckinData.getEndtime()))) {
                return true;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void saveChangesDialog() {

        AlertDialog dialogLock;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(getString(R.string.reporta));
        dialogBuilder.setMessage(getString(R.string.save_checkin_changes_msg));
        dialogBuilder.setPositiveButton(getString(R.string.save), new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if (checkValidation()) {
                    updateCheckRequest();
                }
            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                updateCheckRequest();
            }
        });

        dialogLock = dialogBuilder.create();

        dialogLock.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Logger.getInstance().appendLog(TAG + " :: onActivityResult ResultCode=" + resultCode);
        // Logger.getInstance().appendLog(TAG + " :: onActivityResult Locale=" + Locale.getDefault());

        if (resultCode == Activity.RESULT_OK) {


            switch (requestCode) {
                case INTENT_ADD_MEDIA:


                    if (listPendingMedia == null) {
                        listPendingMedia = new ArrayList<>();
                    }

                    listPendingMedia.add((PendingMediaData) data.getExtras().getSerializable("data"));


                    if (listPendingMedia.size() > 0) {
                        lblAddMedia.setText(getString(R.string.edit));
                    }

                    break;

                case INTENT_PICK_LOCATION:

                    txtChooseLocation.setText(data.getStringExtra("address"));
                    latitude = data.getDoubleExtra("latitude", 0);
                    longitude = data.getDoubleExtra("longitude", 0);
                    addCheckinData.setLatitude(latitude);
                    addCheckinData.setLongitude(longitude);
                    addCheckinData.setLocation(data.getStringExtra("address"));

                    break;

                case INTENT_ADD_SITUATION_MESSAGE:

                    edtSituation.setText(data.getStringExtra("value"));
                    addCheckinData.setDescription(edtSituation.getText().toString());

                    break;

                case INTENT_EDIT_MESSAGE:

                    lblEditAlertMsg.setText(getString(R.string.edit));

                    addCheckinData.setMessage_email(data.getExtras().getString("email"));
                    addCheckinData.setMessage_sms(data.getExtras().getString("sms"));
                    addCheckinData.setMessage_social(data.getExtras().getString("social"));

                    break;

                case INTENT_SET_END_TIME:

                    lblDateTime.setText(getFormatedTime(new Date(Long.parseLong(data.getExtras().getString("date")))));

                    addCheckinData.setEndtime(Long.parseLong(data.getExtras().getString("date")));

                    break;

                case INTENT_FREQUENCY:

                    Bundle mTempBundle = data.getExtras();
                    addCheckinData.setCustom_frequency(mTempBundle.getInt("custom"));
                    addCheckinData.setFrequency(mTempBundle.getInt("frequency"));
                    addCheckinData.setReceiveprompt(mTempBundle.getString("receiveprompt"));


                    int frq;
                    if (addCheckinData.getCustom_frequency() > 0) {
                        frq = addCheckinData.getCustom_frequency();
                    } else {
                        frq = addCheckinData.getFrequency();
                    }

                    int hrs = frq / 60;
                    int mins = frq % 60;

                    if (mins == 0 && hrs > 0) {

                        lblCheckinFrequency.setText(hrs + " " + getString(R.string.hours));

                    } else if (mins > 0 && hrs == 0) {

                        lblCheckinFrequency.setText(mins + " " + getString(R.string.minute));

                    } else {

                        lblCheckinFrequency.setText(hrs + " " + getString(R.string.hours) + " " + mins + " " + getString(R.string.minute));
                    }

                    break;

                case INTENT_SET_START_TIME:

                    addCheckinData.setStarttime(Long.parseLong(data.getExtras().getString("date")));
                    addCheckinData.setLastConfirmTime(Long.parseLong(data.getExtras().getString("date")));
                    addCheckinData.setStaus(0);
                    addCheckinData.setTime(Calendar.getInstance().getTimeInMillis());
                    addCheckinData.setStartNow(true);

                    if (checkValidation()) {
                        createCheckIN();
                    }
                    break;

                case REQUEST_CODE_PRIVATE:

                    private_contactlist_id = data.getExtras().getString("private_contactlist_id");

                    String contactID = private_contactlist_id;
                    if (public_contactlist_id.length() > 0) {
                        contactID += "," + public_contactlist_id;
                    }

                    addCheckinData.setContactlist(contactID);

                    break;

                case REQUEST_CODE_PUBLIC:

                    public_contactlist_id = data.getExtras().getString("public_contactlist_id");

                    contactID = private_contactlist_id;
                    if (public_contactlist_id != null && public_contactlist_id.length() > 0) {
                        contactID += "," + public_contactlist_id;
                    }

                    addCheckinData.setContactlist(contactID);


                    if (Utils.isEmpty(public_contactlist_id)) {

                        lblPublic.setTextColor(Color.GRAY);
                        txtPublic.setText(getString(R.string.add));

                    } else {

                        lblPublic.setTextColor(Color.BLACK);
                        txtPublic.setText(getString(R.string.edit));
                    }

                    break;

                case REQUEST_CODE_SOCIAL:

                    social_facebook = data.getExtras().getString("social_facebook");
                    social_twitter_token = data.getExtras().getString("social_twitter_token");
                    social_twitter_token_secret = data.getExtras().getString("social_twitter_token_secret");

                    addCheckinData.setFb_token(social_facebook);
                    addCheckinData.setTwitter_token(social_twitter_token);
                    addCheckinData.setTwitter_token_secret(social_twitter_token_secret);

                    if (Utils.isEmpty(social_facebook) && Utils.isEmpty(social_twitter_token) && Utils.isEmpty(social_twitter_token_secret)) {

                        lblSocial.setTextColor(Color.GRAY);
                        txtSocial.setText(getString(R.string.add));

                    } else {

                        lblSocial.setTextColor(Color.BLACK);
                        txtSocial.setText(getString(R.string.edit));

                    }

                    break;
            }
        }
    }

    private String getFormatedTime(Date startDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        if (simpleDateFormat.format(startDate).equals(simpleDateFormat.format(new Date()))) {
            simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            return simpleDateFormat.format(startDate);
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            return simpleDateFormat.format(startDate);
        }
    }

    private void deleteAllMedia() {
        try {

            if (ConstantData.DB == null || !ConstantData.DB.isOpen()) {
                ConstantData.DB = new DBHelper(getActivity());
                ConstantData.DB.open();
            }

            ArrayList<PendingMediaData> list = ConstantData.DB.getPendingMediaList();

            for (int i = 0; i < list.size(); i++) {

                ConstantData.DB.deleteMedia(list.get(i).getId());
                try {
                    File mFile = new File(list.get(i).getFilePath());
                    if (mFile.exists()) {
                        mFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < listPendingMedia.size(); i++) {
                ConstantData.DB.deleteMedia(listPendingMedia.get(i).getId());
                try {
                    File mFile = new File(listPendingMedia.get(i).getFilePath());
                    if (mFile.exists()) {
                        mFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            removeCheckIn();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidation() {

        if (addCheckinData.getDescription().length() <= 0) {
            Toast.displayError(getActivity(), getString(R.string.enter_valid_description));
            return false;
        } else if (private_contactlist_id.length() <= 0) {
            Toast.displayError(getActivity(), getString(R.string.enter_valid_contact));
            return false;
        } else if (latitude == 0.0 || longitude == 0.0) {
            Toast.displayError(getActivity(), getString(R.string.choose_valid_location));
            return false;
        } else if (addCheckinData.getFrequency() <= 0) {
            Toast.displayError(getActivity(), getString(R.string.enter_valid_checkin));
            return false;
        } else if (addCheckinData.getEndtime() > 0) {
            if (addCheckinData.getEndtime() < (new Date().getTime() + (addCheckinData.getFrequency() * ConstantData.MINUTE_IN_MILISEC))) {
                Toast.displayError(getActivity(), getString(R.string.enter_valid_endtime));
                return false;
            }
        }
        return true;
    }

    private void getContactList() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ALLCIRCLEWITHCONTACTS, RequestMethod.POST, RequestBuilder.getAllCircleContactListRequest());

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("message") && jsonObject.has("status")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                AddCircleFragment.mContactListDatas = new Gson().fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<ContactListData>>() {
                                }.getType());

                                for (int i = 0; i < AddCircleFragment.mContactListDatas.size(); i++) {

                                    if (AddCircleFragment.mContactListDatas.get(i).getCircle().equals("1")) {

                                        if (AddCircleFragment.mContactListDatas.get(i).getDefaultstatus().equals("1")) {

                                            private_contactlist_id = AddCircleFragment.mContactListDatas.get(i).getContactlist_id();
                                        }

                                    } else {

                                        if (AddCircleFragment.mContactListDatas.get(i).getDefaultstatus().equals("1")) {

                                            public_contactlist_id = AddCircleFragment.mContactListDatas.get(i).getContactlist_id();
                                        }
                                    }
                                }

                                if (Utils.isEmpty(public_contactlist_id)) {

                                    lblPublic.setTextColor(Color.GRAY);
                                    txtPublic.setText(getString(R.string.add));

                                } else {

                                    lblPublic.setTextColor(Color.BLACK);
                                    txtPublic.setText(getString(R.string.edit));
                                }


                                String contactID = private_contactlist_id;
                                if (public_contactlist_id.length() > 0) {
                                    contactID += "," + public_contactlist_id;
                                }

                                addCheckinData.setContactlist(contactID);

                            } else if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
                            }
                        } else {

                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }
                    } catch (Exception e) {
                        dismissProgressDialog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void createCheckInNotification(long startTime, int frequency) {

        Utils.clearAllNotifications(getActivity());

        try {
            Calendar mCurrentCalendar = Calendar.getInstance();
            mCurrentCalendar.set(Calendar.SECOND, 0);
            mCurrentCalendar.set(Calendar.MILLISECOND, 0);
            long currentTime = mCurrentCalendar.getTimeInMillis();
            long endTime = addCheckinData.getEndtime();
            if (endTime == 0) {
                endTime = Long.MAX_VALUE;
            }

            if (frequency >= 30) {
                // checkin remider
                long startOffsetReminder = 0;
                if (startTime > currentTime) {
                    // Future time
                    startOffsetReminder = startTime - currentTime;
                }
                long trigerDurationReminder = startOffsetReminder + ((frequency - 10) * ConstantData.MINUTE_IN_MILISEC);

                Calendar mTempCalendar = Calendar.getInstance();
                mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDurationReminder);

                if (mTempCalendar.getTimeInMillis() < endTime) {
                    mAlarmBroadcast.setAlarm(getActivity(), trigerDurationReminder, ConstantData.ALARM_REMINDER);
                }
            }

            // CheckIn freq
            long startOffsetFreq = 0;
            if (startTime > currentTime) {
                startOffsetFreq = startTime - currentTime;
            }

            long trigerDurationFreq = startOffsetFreq + ((frequency - 2) * ConstantData.MINUTE_IN_MILISEC);

            Calendar mTempCalendar = Calendar.getInstance();
            mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDurationFreq);

            if (mTempCalendar.getTimeInMillis() < endTime) {
                mAlarmBroadcast.setAlarm(getActivity(), trigerDurationFreq, ConstantData.ALARM_FREQ);
            }

            // Missed CheckIn
            long trigerDurationMissed = startOffsetFreq + ((frequency + 1) * ConstantData.MINUTE_IN_MILISEC);

            mTempCalendar = Calendar.getInstance();
            mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + trigerDurationMissed);

            if (mTempCalendar.getTimeInMillis() < endTime) {
                mAlarmBroadcast.setAlarm(getActivity(), trigerDurationMissed, ConstantData.ALARM_MISSED_CHECKIN);
            }

            // Pending Check In Reminder
            Utils.setPendingCheckIn(startTime, currentTime, mAlarmBroadcast, getActivity());
//            if (startTime > currentTime) {
//                // 10 minutes before start time for pending check in
//                long startOffsetReminder = startTime - currentTime;
//                if (startOffsetReminder >= 30 * ConstantData.MINUTE_IN_MILISEC) {
//                    startOffsetReminder -= 10 * ConstantData.MINUTE_IN_MILISEC;
//                    mTempCalendar = Calendar.getInstance();
//                    mTempCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis() + startOffsetReminder);
//                    mAlarmBroadcast.setAlarm(getActivity(), startOffsetReminder, ConstantData.ALARM_PENDING_REMINDER);
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endCheckinNotification(long endtime) {

        try {

            if (addCheckinData.getEndtime() > addCheckinData.getStarttime()) {

                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.SECOND, 0);
                mCalendar.set(Calendar.MILLISECOND, 0);

                // end checkin
                long startOffsetEndtime = 0;
                if (endtime > mCalendar.getTimeInMillis()) {
                    startOffsetEndtime = endtime - mCalendar.getTimeInMillis();
                }

                mAlarmBroadcast.cancelAlarm(getActivity(), ConstantData.ALARM_ENDTIME);
                mAlarmBroadcast.setAlarm(getActivity(), startOffsetEndtime, ConstantData.ALARM_ENDTIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCheckIN() {

        try {

            final String reqString = RequestBuilder.getCreateCheckInRequest(getUTCTIME(addCheckinData.getTime()), addCheckinData.getDescription(), addCheckinData.getLocation(), addCheckinData.getLatitude(), getUTCTIME(addCheckinData.getStarttime()), addCheckinData.getCustom_frequency() > 0 ? addCheckinData.getCustom_frequency() : addCheckinData.getFrequency(), addCheckinData.getMessage_email(), addCheckinData.getReceiveprompt(), addCheckinData.getMessage_social(), addCheckinData.getMessage_sms(), addCheckinData.getLongitude(), addCheckinData.getCheckin_id(), getUTCTIME(addCheckinData.getEndtime()), addCheckinData.getStaus(), addCheckinData.getContactlist(), addCheckinData.getFb_token(), addCheckinData.getTwitter_token(), addCheckinData.getTwitter_token_secret());

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CREATE_CHECKIN, RequestMethod.POST, reqString);
            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        // dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {


                            if (jsonObject.get("status").toString().equals("1")) {

                                if (jsonObject.has("checkin_id")) {
                                    int checkin_id = jsonObject.getInt("checkin_id");
                                    addCheckinData.setCheckin_id(checkin_id);
                                }
                                Calendar mCalendar = Calendar.getInstance();
                                mCalendar.set(Calendar.SECOND, 0);
                                mCalendar.set(Calendar.MILLISECOND, 0);

                                if (mCalendar.getTimeInMillis() < addCheckinData.getStarttime()) {
                                    // Pending Check-in
                                    addCheckinData.setLastConfirmTime(addCheckinData.getStarttime());
                                } else {
                                    addCheckinData.setLastConfirmTime(mCalendar.getTimeInMillis());
                                }

                                saveCheckIn();

                                createCheckInNotification(addCheckinData.getLastConfirmTime(), addCheckinData.getFrequency());
                                endCheckinNotification(addCheckinData.getEndtime());

                                if (listPendingMedia != null && listPendingMedia.size() > 0) {
                                    for (int i = 0; i < listPendingMedia.size(); i++) {
                                        listPendingMedia.get(i).setRef_id(addCheckinData.getCheckin_id());
                                        listPendingMedia.get(i).setLatitude(addCheckinData.getLatitude());
                                        listPendingMedia.get(i).setLongitude(addCheckinData.getLongitude());
                                        listPendingMedia.get(i).setCreatedDate(addCheckinData.getTime());
                                        listPendingMedia.get(i).setTableId(1);
                                    }

                                    uploadMedia();

                                } else {
                                    dismissProgressDialog();

                                    if (addCheckinData.isStartNow()) {
                                        // Created

                                        if (mCalendar.getTimeInMillis() < addCheckinData.getStarttime()) {
                                            // Pending check-in

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(addCheckinData.getStarttime());
                                            final String nextConfirmTime = getFormatedTime(calendar.getTime());
                                            ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.checkin_will_start_at) + " " + nextConfirmTime);
                                        } else {
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(addCheckinData.getLastConfirmTime());
                                            calendar.add(Calendar.MINUTE, addCheckinData.getCustom_frequency() > 0 ? addCheckinData.getCustom_frequency() : addCheckinData.getFrequency());
                                            final String nextConfirmTime = getFormatedTime(calendar.getTime());
                                            ((BaseAppCompatActivity) getActivity()).showMessageWithFinish(getString(R.string.your_checkin_now_active), getString(R.string.your_next_conf) + nextConfirmTime);
                                        }
                                    } else {
                                        // Updated
                                        ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.checkin_edited_suc));
                                    }

                                }

                            } else if (jsonObject.get("status").toString().equals("3")) {
                                dismissProgressDialog();
                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {
                                dismissProgressDialog();
                                showMessage("", jsonObject.get("message").toString());
                            }
                        } else {
                            dismissProgressDialog();
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }
                    } catch (Exception e) {
                        dismissProgressDialog();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                }

            });
            displayProgressDialog();
            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void updateCheckRequest() {


        // ***********

        int status_ = 2;
        Calendar mCalendar1 = Calendar.getInstance();
        mCalendar1.set(Calendar.SECOND, 0);
        mCalendar1.set(Calendar.MILLISECOND, 0);

        if (addCheckinData.getStarttime() > mCalendar1.getTimeInMillis()) {
            addCheckinData.setTime(mCalendar1.getTimeInMillis());
            addCheckinData.setStarttime(mCalendar1.getTimeInMillis());
            addCheckinData.setLastConfirmTime(mCalendar1.getTimeInMillis());

            status_ = 1;
        }

        final int status = status_;

        String reqString;

        if (isMadeAnyChanges) {

            reqString = RequestBuilder.getCreateEditCheckInWhenConfirmRequest(getUTCTIME(addCheckinData.getTime()), addCheckinData.getDescription(), addCheckinData.getLocation(), addCheckinData.getLatitude(), getUTCTIME(addCheckinData.getStarttime()), addCheckinData.getCustom_frequency() > 0 ? addCheckinData.getCustom_frequency() : addCheckinData.getFrequency(), addCheckinData.getMessage_email(), addCheckinData.getReceiveprompt(), addCheckinData.getMessage_social(), addCheckinData.getMessage_sms(), addCheckinData.getLongitude(), addCheckinData.getCheckin_id(), getUTCTIME(addCheckinData.getEndtime()), addCheckinData.getStaus(), addCheckinData.getContactlist(), addCheckinData.getFb_token(), addCheckinData.getTwitter_token(), addCheckinData.getTwitter_token_secret(), isMadeAnyChanges);

        } else {

            reqString = RequestBuilder.getUpdateCheckInRequest(getUTCTIME(Calendar.getInstance().getTimeInMillis()), addCheckinData.getCheckin_id(), status, isMadeAnyChanges, "");
        }

        // ***********

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_UPDATE_CHECKIN, RequestMethod.POST, reqString);
        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                try {

                    dismissProgressDialog();

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("message") && jsonObject.has("status")) {


                        if (jsonObject.optInt("status", 0) == 1) {

                            Calendar mCalendar = Calendar.getInstance();
                            mCalendar.set(Calendar.SECOND, 0);
                            mCalendar.set(Calendar.MILLISECOND, 0);

                            addCheckinData.setLastConfirmTime(mCalendar.getTimeInMillis());

                            Editor editor = prefManager.getPrefs().edit();
                            if (status == 1) {
                                // Start Now for Pending check-in
                                editor.putLong(PARAMS.TAG_STARTTIME, addCheckinData.getLastConfirmTime());

                            }
                            editor.putLong(PARAMS.TAG_LASTCONFIRMTIME, addCheckinData.getLastConfirmTime());
                            editor.putLong(PARAMS.TAG_TIME, addCheckinData.getLastConfirmTime());
                            editor.apply();

                            addCheckinData = new AddCheckinData();
                            getCheckIn();

                            createCheckInNotification(addCheckinData.getLastConfirmTime(), addCheckinData.getFrequency());
                            endCheckinNotification(addCheckinData.getEndtime());

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(addCheckinData.getLastConfirmTime());
                            calendar.add(Calendar.MINUTE, addCheckinData.getCustom_frequency() > 0 ? addCheckinData.getCustom_frequency() : addCheckinData.getFrequency());
                            final String nextConfirmTime = getFormatedTime(calendar.getTime());

                            if (status == 1) {
                                // Start Now for Pending check-in
                                ((BaseAppCompatActivity) getActivity()).showMessageWithFinish(getString(R.string.your_checkin_now_active), getString(R.string.your_next_conf) + nextConfirmTime);

                            } else {
                                ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.your_next_conf) + nextConfirmTime);
                            }

                        } else if (jsonObject.optInt("status", 0) == 3) {

                            ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                        } else {

                            Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
                        }
                    } else {
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                dismissProgressDialog();
                Toast.displayError(getActivity(), getString(R.string.try_later));
            }
        });
        displayProgressDialog();
        mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveCheckIn() {

        try {
            Editor editor = prefManager.getPrefs().edit();

            editor.putString(PARAMS.TAG_DESCRIPTION, addCheckinData.getDescription());
            editor.putLong(PARAMS.TAG_STARTTIME, addCheckinData.getStarttime());
            editor.putInt(PARAMS.TAG_FREQUENCY, addCheckinData.getFrequency());
            editor.putString(PARAMS.TAG_MESSAGE_EMAIL, addCheckinData.getMessage_email());
            editor.putString(PARAMS.TAG_RECEIVEPROMPT, addCheckinData.getReceiveprompt());
            editor.putString(PARAMS.TAG_MESSAGE_SOCIAL, addCheckinData.getMessage_social());
            editor.putString(PARAMS.TAG_LONGITUDE, "" + addCheckinData.getLongitude());
            editor.putString(PARAMS.TAG_MESSAGE_SMS, addCheckinData.getMessage_sms());
            editor.putInt(PARAMS.TAG_CHECKIN_ID, addCheckinData.getCheckin_id());
            editor.putLong(PARAMS.TAG_ENDTIME, addCheckinData.getEndtime());
            editor.putInt(PARAMS.TAG_STATUS, addCheckinData.getStaus());
            editor.putString(PARAMS.TAG_CONTACTLIST, addCheckinData.getContactlist());
            editor.putString(PARAMS.TAG_LOCATION, addCheckinData.getLocation());
            editor.putString(PARAMS.TAG_LATITUDE, "" + addCheckinData.getLatitude());
            editor.putLong(PARAMS.TAG_LASTCONFIRMTIME, addCheckinData.getLastConfirmTime());

            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeCheckIn() {

        try {
            Editor editor = prefManager.getPrefs().edit();

            editor.remove(PARAMS.TAG_DESCRIPTION);
            editor.remove(PARAMS.TAG_STARTTIME);
            editor.remove(PARAMS.TAG_FREQUENCY);
            editor.remove(PARAMS.TAG_MESSAGE_EMAIL);
            editor.remove(PARAMS.TAG_RECEIVEPROMPT);
            editor.remove(PARAMS.TAG_MESSAGE_SOCIAL);
            editor.remove(PARAMS.TAG_LONGITUDE);
            editor.remove(PARAMS.TAG_MESSAGE_SMS);
            editor.remove(PARAMS.TAG_CHECKIN_ID);
            editor.remove(PARAMS.TAG_ENDTIME);
            editor.remove(PARAMS.TAG_STATUS);
            editor.remove(PARAMS.TAG_CONTACTLIST);
            editor.remove(PARAMS.TAG_LOCATION);
            editor.remove(PARAMS.TAG_LATITUDE);
            editor.remove(PARAMS.TAG_LASTCONFIRMTIME);

            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {

        edtSituation.setText(addCheckinData.getDescription());
        txtChooseLocation.setText(addCheckinData.getLocation());
        latitude = addCheckinData.getLatitude();
        longitude = addCheckinData.getLongitude();

        int frq;
        if (addCheckinData.getCustom_frequency() > 0) {
            frq = addCheckinData.getCustom_frequency();
        } else {
            frq = addCheckinData.getFrequency();
        }

        int hrs = (frq / 60);
        int mins = (frq % 60);

        if (mins == 0 && hrs > 0) {

            lblCheckinFrequency.setText(hrs + " " + getString(R.string.hours));

        } else if (mins > 0 && hrs == 0) {

            lblCheckinFrequency.setText(mins + " " + getString(R.string.minute));

        } else {

            lblCheckinFrequency.setText(hrs + " " + getString(R.string.hours) + " " + mins + " " + getString(R.string.minute));
        }

        if (!addCheckinData.getMessage_email().equals("") || !addCheckinData.getMessage_social().equals("") || !addCheckinData.getMessage_sms().equals("")) {

            lblEditAlertMsg.setText(getString(R.string.edit));
        }

        if (addCheckinData.getEndtime() > 0) {

            lblDateTime.setText(getFormatedTime(new Date(addCheckinData.getEndtime())));
        }
    }

    private void getCheckIn() {

        try {
            prefManager.getPrefs().getBoolean(PARAMS.KEY_SOS_STATUS, false);

            addCheckinData.setDescription(prefManager.getPrefs().getString(PARAMS.TAG_DESCRIPTION, ""));
            addCheckinData.setStarttime(prefManager.getPrefs().getLong(PARAMS.TAG_STARTTIME, 0));
            addCheckinData.setFrequency(prefManager.getPrefs().getInt(PARAMS.TAG_FREQUENCY, 0));
            addCheckinData.setMessage_email(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_EMAIL, ""));
            addCheckinData.setReceiveprompt(prefManager.getPrefs().getString(PARAMS.TAG_RECEIVEPROMPT, "3"));
            addCheckinData.setMessage_social(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_SOCIAL, ""));
            addCheckinData.setLongitude(Double.parseDouble(prefManager.getPrefs().getString(PARAMS.TAG_LONGITUDE, "0")));
            addCheckinData.setMessage_sms(prefManager.getPrefs().getString(PARAMS.TAG_MESSAGE_SMS, ""));
            addCheckinData.setCheckin_id(prefManager.getPrefs().getInt(PARAMS.TAG_CHECKIN_ID, 0));
            addCheckinData.setEndtime(prefManager.getPrefs().getLong(PARAMS.TAG_ENDTIME, 0));
            addCheckinData.setStaus(prefManager.getPrefs().getInt(PARAMS.TAG_STATUS, 0));
            addCheckinData.setContactlist(prefManager.getPrefs().getString(PARAMS.TAG_CONTACTLIST, ""));
            addCheckinData.setLocation(prefManager.getPrefs().getString(PARAMS.TAG_LOCATION, ""));
            addCheckinData.setLatitude(Double.parseDouble(prefManager.getPrefs().getString(PARAMS.TAG_LATITUDE, "0")));
            addCheckinData.setLastConfirmTime(prefManager.getPrefs().getLong(PARAMS.TAG_LASTCONFIRMTIME, 0));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void uploadMedia() {

        try {

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ADD_MEDIA, RequestMethod.POST, RequestBuilder.getMediaRequest(listPendingMedia.get(0)));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {
                                File file = new File(listPendingMedia.get(0).getFilePath());
                                if (file.exists()) {
                                    file.delete();
                                }
                                int checkinID = listPendingMedia.get(0).getRef_id();

                                listPendingMedia.remove(0);
                                if (listPendingMedia.size() > 0) {
                                    uploadMedia();
                                } else {
                                    dismissProgressDialog();
                                    sendMailAttachment(checkinID);
                                }
                            } else if (jsonObject.get("status").toString().equals("3")) {
                                dismissProgressDialog();
                                saveMediaInLocal();
                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());
                            } else {
                                dismissProgressDialog();
                                saveMediaInLocal();
                                showMessage("", jsonObject.get("message").toString());
                            }

                        } else {
                            dismissProgressDialog();
                            saveMediaInLocal();
                        }

                    } catch (Exception e) {
                        dismissProgressDialog();
                        e.printStackTrace();
                        saveMediaInLocal();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    saveMediaInLocal();
                }
            });
            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private void sendMailAttachment(final int checkinID) {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_SEND_MAIL_MEDIA, RequestMethod.POST, RequestBuilder.getSendMailWithMediaInRequest(checkinID, ConstantData.ALERT_TYPE_CHECKIN));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        // ****

                        if (addCheckinData.getStaus() == 2) {
                            ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.checkin_edited_suc));
                        } else {
                            addCheckinData.setCheckin_id(checkinID);
                            if (addCheckinData.isStartNow()) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new Date(addCheckinData.getStarttime()));
                                calendar.add(Calendar.MINUTE, addCheckinData.getCustom_frequency() > 0 ? addCheckinData.getCustom_frequency() : addCheckinData.getFrequency());
                                final String nextConfirmTime = getFormatedTime(calendar.getTime());
                                ((BaseAppCompatActivity) getActivity()).showMessageWithFinish(getString(R.string.your_checkin_now_active), getString(R.string.your_next_conf) + nextConfirmTime);

                            } else {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new Date(addCheckinData.getStarttime()));
                                final String nextConfirmTime = getFormatedTime(calendar.getTime());
                                ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.checkin_will_start_at) + nextConfirmTime);
                            }
                        }

                        // *****

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    saveMediaInLocal();
                }
            });
            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
            dismissProgressDialog();
        }
    }

    private void saveMediaInLocal() {

        try {

            if (listPendingMedia != null && listPendingMedia.size() > 0) {
                for (int i = 0; i < listPendingMedia.size(); i++) {
                    PendingMediaData data = listPendingMedia.get(i);
                    ConstantData.DB.insertPendingMedia(data.getRef_id(), data.getFilePath(), data.getMediaType(), data.getCreatedDate(), data.getTableId(), data.getLatitude(), data.getLongitude(), data.getAttemptCount());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

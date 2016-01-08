package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iwmf.CheckinSituationMessageActivity;
import com.iwmf.IamFacingActvity;
import com.iwmf.MediaActivity;
import com.iwmf.PickLocationActivity;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.ContactListData;
import com.iwmf.data.PendingMediaData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p> Fragment to generate new alert.
 * User can send alert to his/her circle to notify them. </p>
 */
@SuppressWarnings("ALL")
public class AlertFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = AlertFragment.class.getSimpleName();

    private final static int INTENT_ADD_MEDIA = 1;
    private final static int INTENT_PICK_LOCATION = 2;
    private final static int INTENT_ADD_SITUATION_MESSAGE = 3;
    private final static int INTENT_I_AM_FACING = 4;
    private final static int INTENT_ADD_OTHER_CONTACT = 5;

    private final static int REQUEST_CODE_PRIVATE = 110;
    private final static int REQUEST_CODE_PUBLIC = 111;
    private final static int REQUEST_CODE_SOCIAL = 112;

    private String private_contactlist_id = "";
    private String public_contactlist_id = "";

    private String social_twitter_token_secret = "";
    private String social_twitter_token = "";
    private String social_facebook = "";

    // ****

    private Button btnIamFacing = null, btnSendAlert = null;
    private TextView txtSituation = null, txtChooseLocation = null, lblPrivate = null, txtEdit = null, lblPublic = null, txtPublic = null, lblSocial = null, txtSocial = null, txtCheckinFreqOption = null, txtCheckinFrequency = null;
    private RelativeLayout rltLocation = null, rltPrivate = null, rltMedia = null, rlt_public = null, rltsocial = null;

    // ****

    private double latitude = 0, longitude = 0;
    private String facingIssue = "";
    private ArrayList<PendingMediaData> listPendingMedia = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_alert, container, false);

        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        btnIamFacing = (Button) v.findViewById(R.id.btnIamFacing);
        btnSendAlert = (Button) v.findViewById(R.id.btnSendAlert);

        txtSituation = (TextView) v.findViewById(R.id.txtSituation);
        txtChooseLocation = (TextView) v.findViewById(R.id.txtChooseLocation);
        lblPrivate = (TextView) v.findViewById(R.id.lblPrivate);
        txtEdit = (TextView) v.findViewById(R.id.txtEdit);
        lblPublic = (TextView) v.findViewById(R.id.lblPublic);
        txtPublic = (TextView) v.findViewById(R.id.txtPublic);
        lblSocial = (TextView) v.findViewById(R.id.lblSocial);
        txtSocial = (TextView) v.findViewById(R.id.txtSocial);
        txtCheckinFreqOption = (TextView) v.findViewById(R.id.txtCheckinFreqOption);
        txtCheckinFrequency = (TextView) v.findViewById(R.id.txtCheckinFrequency);

        rltLocation = (RelativeLayout) v.findViewById(R.id.rltLocation);
        rltPrivate = (RelativeLayout) v.findViewById(R.id.rltPrivate);
        rltMedia = (RelativeLayout) v.findViewById(R.id.rltMedia);

        rlt_public = (RelativeLayout) v.findViewById(R.id.rlt_public);
        rltsocial = (RelativeLayout) v.findViewById(R.id.rltsocial);

        rlt_public.setOnClickListener(this);
        rltsocial.setOnClickListener(this);
        rltPrivate.setOnClickListener(this);
        btnIamFacing.setOnClickListener(this);
        txtSituation.setOnClickListener(this);
        rltLocation.setOnClickListener(this);
        rltLocation.setOnClickListener(this);
        rltMedia.setOnClickListener(this);
        btnSendAlert.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        facingIssue = btnIamFacing.getText().toString();

        getContactList();
    }

    private void sendAlert(String situation, String description, String location, final double latitude, final double longitude, String contactList) {

        try {

            displayProgressDialog();

            final Date date = new Date();

            if (listPendingMedia == null) {
                listPendingMedia = new ArrayList<>();
            }

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_SEND_ALERT, RequestMethod.POST, RequestBuilder.getSendAlertRequest(situation, description, location, latitude, longitude, contactList, getUTCTIME(date.getTime()), social_facebook, social_twitter_token, social_twitter_token_secret, listPendingMedia.size()));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                final int alert_id = jsonObject.getInt("alert_id");

                                if (listPendingMedia != null && listPendingMedia.size() > 0) {

                                    for (int i = 0; i < listPendingMedia.size(); i++) {

                                        listPendingMedia.get(i).setRef_id(alert_id);
                                        listPendingMedia.get(i).setLatitude(latitude);
                                        listPendingMedia.get(i).setLongitude(longitude);
                                        listPendingMedia.get(i).setCreatedDate(date.getTime());
                                        listPendingMedia.get(i).setTableId(2);
                                    }

                                    uploadMedia();

                                } else {
                                    dismissProgressDialog();
                                    ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.alert_sent_successfully));
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
                        e.printStackTrace();
                        dismissProgressDialog();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                    }
                }

                @Override
                public void onError(String error) {

                    Toast.displayError(getActivity(), getString(R.string.try_later));
                    dismissProgressDialog();
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
            dismissProgressDialog();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnIamFacing:

                Intent intent = new Intent(getActivity(), IamFacingActvity.class);
                intent.putExtra("title", getString(R.string.alerts));
                intent.putExtra("facingIssue", facingIssue);
                startActivityForResult(intent, INTENT_I_AM_FACING);

                break;

            case R.id.txtSituation:

                intent = new Intent(getActivity(), CheckinSituationMessageActivity.class);
                intent.putExtra("title", getString(R.string.alerts));
                intent.putExtra("value", txtSituation.getText().toString());
                startActivityForResult(intent, INTENT_ADD_SITUATION_MESSAGE);

                break;

            case R.id.rltLocation:

                intent = new Intent(getActivity(), PickLocationActivity.class);
                intent.putExtra("title", getString(R.string.alerts));
                intent.putExtra("address", txtChooseLocation.getText().toString().trim());
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, INTENT_PICK_LOCATION);

                break;

            case R.id.rlt_public:

                Fragment mFragment = AddCircleFragment.getInstance(SelectedScreen.PUBLIC, false, public_contactlist_id);
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

            case R.id.rltPrivate:


                mFragment = AddCircleFragment.getInstance(SelectedScreen.PRIVATE, false, private_contactlist_id);
                mFragment.setTargetFragment(this, REQUEST_CODE_PRIVATE);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddCircleFragment.class.getSimpleName()).commit();

                break;

            case R.id.rltMedia:

                intent = new Intent(getActivity(), MediaActivity.class);
                startActivityForResult(intent, INTENT_ADD_MEDIA);

                break;

            case R.id.btnSendAlert:

                if (facingIssue == null || facingIssue.length() <= 0) {
                    Toast.displayError(getActivity(), getString(R.string.choose_valid_situation));
                    return;
                } else if (facingIssue.contains("Other") && txtSituation.getText().toString().length() <= 0) {
                    Toast.displayError(getActivity(), getString(R.string.enter_valid_description));
                    return;
                } else if (private_contactlist_id.length() <= 0) {
                    Toast.displayError(getActivity(), getString(R.string.enter_valid_contact));
                    return;
                } else if (latitude == 0.0 || longitude == 0.0) {
                    Toast.displayError(getActivity(), getString(R.string.choose_valid_location));
                    return;
                }

                String contactID = private_contactlist_id;
                if (public_contactlist_id.length() > 0) {
                    contactID += "," + public_contactlist_id;
                }

                sendAlert(facingIssue.replace(",,", ","), txtSituation.getText().toString(), txtChooseLocation.getText().toString(), latitude, longitude, contactID);

                break;
        }
        getActivity().overridePendingTransition(R.anim.enter, R.anim.no_anim);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case INTENT_ADD_MEDIA:

                    if (listPendingMedia == null) {
                        listPendingMedia = new ArrayList<>();
                    }
                    listPendingMedia.add((PendingMediaData) data.getExtras().getSerializable("data"));

                    break;

                case INTENT_PICK_LOCATION:

                    txtChooseLocation.setText(data.getStringExtra("address"));
                    latitude = data.getDoubleExtra("latitude", 0);
                    longitude = data.getDoubleExtra("longitude", 0);

                    break;

                case INTENT_ADD_SITUATION_MESSAGE:

                    txtSituation.setText(data.getStringExtra("value"));

                    break;

                case INTENT_I_AM_FACING:

                    facingIssue = data.getStringExtra("facingIssue");
                    btnIamFacing.setText(facingIssue.replace(",,", ","));

                    break;

                case INTENT_ADD_OTHER_CONTACT:

                    getContactList();

                    break;

                case REQUEST_CODE_PRIVATE:

                    private_contactlist_id = data.getExtras().getString("private_contactlist_id");

                    break;

                case REQUEST_CODE_PUBLIC:

                    public_contactlist_id = data.getExtras().getString("public_contactlist_id");

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

                        if (jsonObject.has("message")) {
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
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
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
                                int alertId = listPendingMedia.get(0).getRef_id();

                                listPendingMedia.remove(0);
                                if (listPendingMedia.size() > 0) {
                                    uploadMedia();
                                } else {
                                    dismissProgressDialog();
                                    sendMailAttachment(alertId);
                                }
                            } else if (jsonObject.get("status").toString().equals("3")) {
                                dismissProgressDialog();
                                saveMediaInLocal();
                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());
                            } else {
                                dismissProgressDialog();
                                saveMediaInLocal();
                            }
                        } else {
                            dismissProgressDialog();
                            saveMediaInLocal();
                        }

                    } catch (Exception e) {
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
            e.printStackTrace();
            dismissProgressDialog();
        }
    }

    private void saveMediaInLocal() {

        dismissProgressDialog();

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
        if (isAdded()) {
            ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.alert_sent_successfully));
        }
    }

    private void sendMailAttachment(int alertId) {

        try {
            displayProgressDialog();
            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_SEND_MAIL_MEDIA, RequestMethod.POST, RequestBuilder.getSendMailWithMediaInRequest(alertId, ConstantData.ALERT_TYPE_ALERT));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.get("status").toString().equals("1")) {
                            if (isAdded()) {
                                ((BaseAppCompatActivity) getActivity()).showMessageWithFinish("", getString(R.string.alert_sent_successfully));
                            }
                        }
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
}

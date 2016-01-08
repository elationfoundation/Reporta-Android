package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.PARAMS;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

/**
 * <p> Display user social circle. </p>
 */
public class SocialCircleFragment extends BaseFragment implements OnClickListener {

    private final static int REQUEST_CODE_SOCIAL = 112;

    private TwitterAuthClient mTwitterAuthClient = null;
    private String social_twitter_token = "", social_twitter_token_secret = "", social_facebook = "";
    private TextView txtFacebookLogin = null, txtTwitterLogin = null;

    private CallbackManager callbackManager = null;
    private LoginManager manager = null;

    private CheckBox chkFacebook = null, chkTwitter = null;

    // facebook
    private boolean isFromContacts = true;
    private int whichChecked = 0;

    public static SocialCircleFragment getInstance(boolean isFromContacts, int whichChecked) {

        SocialCircleFragment mFragment = new SocialCircleFragment();

        try {

            mFragment.isFromContacts = isFromContacts;
            mFragment.whichChecked = whichChecked;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            mTwitterAuthClient = new TwitterAuthClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            getKeyHash();

            callbackManager = CallbackManager.Factory.create();
            manager = LoginManager.getInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_social, container, false);

        mappingWidgets(mView);


        if (hasFacebookAccessToken()) {

            txtFacebookLogin.setText(getString(R.string.remove));

            if (whichChecked == 1 || whichChecked == 3) {
                chkFacebook.setChecked(true);
            }
        }

        if (hasTwitterAccessToken()) {

            txtTwitterLogin.setText(getString(R.string.remove));

            if (whichChecked == 2 || whichChecked == 3) {
                chkTwitter.setChecked(true);
            }
        }
        return mView;
    }

    private void mappingWidgets(View v) {

        txtFacebookLogin = (TextView) v.findViewById(R.id.txtFacebookLogin);
        txtTwitterLogin = (TextView) v.findViewById(R.id.txtTwitterLogin);

        chkFacebook = (CheckBox) v.findViewById(R.id.chkFacebook);
        chkTwitter = (CheckBox) v.findViewById(R.id.chkTwitter);

        if (isFromContacts) {
            chkFacebook.setVisibility(View.GONE);
            chkTwitter.setVisibility(View.GONE);
        } else {
            chkFacebook.setVisibility(View.VISIBLE);
            chkTwitter.setVisibility(View.VISIBLE);
        }

        txtFacebookLogin.setOnClickListener(this);
        txtTwitterLogin.setOnClickListener(this);

        chkFacebook.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (hasFacebookAccessToken()) {

                        if (!chkTwitter.isChecked()) {

                            social_twitter_token = "";
                            social_twitter_token_secret = "";
                        }

                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(REQUEST_CODE_SOCIAL, Activity.RESULT_OK, getActivity().getIntent().putExtra("social_facebook", social_facebook).putExtra("social_twitter_token", social_twitter_token).putExtra("social_twitter_token_secret", social_twitter_token_secret));
                        }

                    } else {

                        fbLogin();
                    }

                } else {

                    social_facebook = "";

                    if (!chkTwitter.isChecked()) {

                        social_twitter_token = "";
                        social_twitter_token_secret = "";
                    }

                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(REQUEST_CODE_SOCIAL, Activity.RESULT_OK, getActivity().getIntent().putExtra("social_facebook", social_facebook).putExtra("social_twitter_token", social_twitter_token).putExtra("social_twitter_token_secret", social_twitter_token_secret));
                    }
                }
            }
        });

        chkTwitter.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (hasTwitterAccessToken()) {

                        if (!chkFacebook.isChecked()) {

                            social_facebook = "";
                        }

                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(REQUEST_CODE_SOCIAL, Activity.RESULT_OK, getActivity().getIntent().putExtra("social_facebook", social_facebook).putExtra("social_twitter_token", social_twitter_token).putExtra("social_twitter_token_secret", social_twitter_token_secret));
                        }

                    } else {

                        twitterLogin();
                    }

                } else {

                    social_twitter_token = "";
                    social_twitter_token_secret = "";

                    if (!chkFacebook.isChecked()) {

                        social_facebook = "";
                    }

                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(REQUEST_CODE_SOCIAL, Activity.RESULT_OK, getActivity().getIntent().putExtra("social_facebook", social_facebook).putExtra("social_twitter_token", social_twitter_token).putExtra("social_twitter_token_secret", social_twitter_token_secret));
                    }

                }


            }
        });

    }

    private void fbLogin() {

        // Just added this code to disable facebook feature, once we get facebook api approved we will remove this if block to enable it.
        if (true) {
            return;
        }

        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                if (loginResult != null) {

                    storeFacebookAccessToken(loginResult.getAccessToken());

                }
            }

            @Override
            public void onCancel() {

                resetFacebookAccessToken();

                txtFacebookLogin.setText(getString(R.string.login));
                chkFacebook.setChecked(false);
            }

            @Override
            public void onError(FacebookException exception) {

                exception.printStackTrace();

                resetFacebookAccessToken();

                txtFacebookLogin.setText(getString(R.string.login));
                chkFacebook.setChecked(false);

            }
        });
//        manager.logInWithReadPermissions(this, Arrays.asList("publish_actions"));
//        manager.logInWithReadPermissions(this, Arrays.asList("public_profile"));
        manager.logInWithPublishPermissions(this, Arrays.asList("publish_actions", "public_profile"));

    }

    private void twitterLogin() {

        try {

            mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {

                @Override
                public void success(Result<TwitterSession> result) {

                    if (result != null) {

                        if (result.data != null) {

                            try {

                                storeTwitterAccessToken(result.data);

                                txtTwitterLogin.setText(getString(R.string.remove));

                                if (!chkFacebook.isChecked()) {
                                    social_facebook = "";
                                }

                                if (getTargetFragment() != null) {
                                    getTargetFragment().onActivityResult(REQUEST_CODE_SOCIAL, Activity.RESULT_OK, getActivity().getIntent().putExtra("social_facebook", social_facebook).putExtra("social_twitter_token", social_twitter_token).putExtra("social_twitter_token_secret", social_twitter_token_secret));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void failure(TwitterException e) {

                    e.printStackTrace();

                    resetTwitterAccessToken();

                    txtTwitterLogin.setText(getString(R.string.login));
                    chkTwitter.setChecked(false);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtCancel:

                getActivity().onBackPressed();

                break;

            case R.id.txtFacebookLogin:

                if (hasFacebookAccessToken()) {

                    resetFacebookAccessToken();

                    txtFacebookLogin.setText(getString(R.string.login));
                    chkFacebook.setChecked(false);

                } else {

                    fbLogin();
                }

                break;

            case R.id.txtTwitterLogin:

                if (hasTwitterAccessToken()) {

                    resetTwitterAccessToken();

                    txtTwitterLogin.setText(getString(R.string.login));
                    chkTwitter.setChecked(false);

                } else {

                    twitterLogin();
                }

                break;
        }
    }

    private boolean hasTwitterAccessToken() {

        social_twitter_token = prefManager.getPrefs().getString(PARAMS.TAG_TWITTER_TOKEN, "");
        social_twitter_token_secret = prefManager.getPrefs().getString(PARAMS.TAG_TWITTER_TOKEN_SECRET, "");

        return (!social_twitter_token.equals("") && !social_twitter_token_secret.equals(""));
    }

    private boolean hasFacebookAccessToken() {

        social_facebook = prefManager.getPrefs().getString(PARAMS.TAG_FB_TOKEN, "");

        return (!social_facebook.equals(""));
    }

    private void storeTwitterAccessToken(TwitterSession resultData) {

        prefManager.getPrefs().edit().putString(PARAMS.TAG_TWITTER_TOKEN, resultData.getAuthToken().token).apply();
        prefManager.getPrefs().edit().putString(PARAMS.TAG_TWITTER_TOKEN_SECRET, resultData.getAuthToken().secret).apply();

        social_twitter_token = resultData.getAuthToken().token;
        social_twitter_token_secret = resultData.getAuthToken().secret;

    }

    private void storeFacebookAccessToken(AccessToken mAccessToken) {

        prefManager.getPrefs().edit().putString(PARAMS.TAG_FB_TOKEN, mAccessToken.getToken()).apply();

        social_facebook = mAccessToken.getToken();
    }

    private void resetTwitterAccessToken() {

        prefManager.getPrefs().edit().putString(PARAMS.TAG_TWITTER_TOKEN, "").apply();
        prefManager.getPrefs().edit().putString(PARAMS.TAG_TWITTER_TOKEN_SECRET, "").apply();

        social_twitter_token = "";
        social_twitter_token_secret = "";
    }

    private void resetFacebookAccessToken() {

        prefManager.getPrefs().edit().putString(PARAMS.TAG_FB_TOKEN, "").apply();
        social_facebook = "";
    }
}

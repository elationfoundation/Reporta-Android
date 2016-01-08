package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.RequestBuilder;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;

/**
 * <p> Displays terms and conditions for account creation with Reporta app.
 * Ask user preference to continue. </p>
 */
public class TermConditionDescriptionFragment extends BaseFragment implements OnClickListener {

    private static final String EXTRA_AGREED = "isAgree";

    private boolean isAgreed = false;
    private ProgressBar progress;

    public static TermConditionDescriptionFragment getInstance(boolean isAgreed) {

        TermConditionDescriptionFragment mFragment = new TermConditionDescriptionFragment();
        Bundle mBundle = new Bundle();
        mBundle.putBoolean(EXTRA_AGREED, isAgreed);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_AGREED, isAgreed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            isAgreed = savedInstanceState.getBoolean(EXTRA_AGREED);
        } else {
            if (getArguments() != null && getArguments().containsKey(EXTRA_AGREED)) {
                isAgreed = getArguments().getBoolean(EXTRA_AGREED);
            }
        }

        View mView = inflater.inflate(R.layout.fragment_termcondition_description, container, false);
        mappingWidgets(mView);
        return mView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void mappingWidgets(View v) {

        Button btnAgree = (Button) v.findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(this);

        btnAgree.setVisibility(isAgreed ? View.GONE : View.VISIBLE);

        WebView webView = (WebView) v.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myChromClient());

        String languageCode = "english";
        String languageShortCode = TextUtils.isEmpty(ConstantData.LANGUAGE_CODE) ? Language_code.EN.name() : ConstantData.LANGUAGE_CODE;

        if (Language_code.EN.name().equalsIgnoreCase(languageShortCode)) {
            languageCode = "english";
        } else if (Language_code.AR.name().equalsIgnoreCase(languageShortCode)) {
            languageCode = "arabic";
        } else if (Language_code.ES.name().equalsIgnoreCase(languageShortCode)) {
            languageCode = "spanish";
        } else if (Language_code.FR.name().equalsIgnoreCase(languageShortCode)) {
            languageCode = "french";
        } else if (Language_code.IW.name().equalsIgnoreCase(languageShortCode)) {
            languageCode = "hebrew";
        } else if (Language_code.TR.name().equalsIgnoreCase(languageShortCode)) {
            languageCode = "turkish";
        }

        webView.loadUrl(RequestBuilder.TERMS_OF_USE_URL + "/" + languageCode);
        progress = (ProgressBar) v.findViewById(R.id.progress);

        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAgree:

                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                }

                getActivity().onBackPressed();

                break;
        }
    }

    private class myChromClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            progress.setVisibility(View.VISIBLE);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progress.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            progress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }
}
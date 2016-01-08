package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.RequestBuilder;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Utils;

/**
 * <p> About page of Reporta app.
 * This shows the detailed information about the Reporta.
 * This fragment is included in AboutMeActivity. </p>
 */
public class AboutReportaFragment extends BaseFragment {

    private ProgressBar progress = null;
    private WebView webView = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myChromClient());

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

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

        webView.loadUrl(RequestBuilder.ABOUT_REPORTA_URL + "/" + languageCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_aboutreports, container, false);
        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        webView = (WebView) v.findViewById(R.id.webView);

        progress = (ProgressBar) v.findViewById(R.id.progress);

        progress.setVisibility(View.VISIBLE);

        v.findViewById(R.id.imvInfoClose).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.no_anim, R.anim.abc_slide_out_top);
            }
        });

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

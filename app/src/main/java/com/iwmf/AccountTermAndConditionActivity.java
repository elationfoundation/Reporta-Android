package com.iwmf;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.AccountTermAndConditionFragment;

/**
 * <p> Displays terms and conditions for account creation with Reporta app.
 * This include AccountTermAndConditionFragment which displays actual terms and conditions and get user agreement to continue.
 * </p>
 */
@SuppressWarnings("ALL")
public class AccountTermAndConditionActivity extends BaseAppCompatActivity {

    private static final String TAG = AccountTermAndConditionActivity.class.getSimpleName();

    private ScrollView scrollview_info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        scrollview_info = (ScrollView) findViewById(R.id.scrollview_info);

        ((TextView) findViewById(R.id.txtInfo)).setText(getString(R.string.info_profile));

        restoreActionBar();

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("username", getIntent().getStringExtra("username"));
            bundle.putString("password", getIntent().getStringExtra("password"));
            bundle.putString("email", getIntent().getStringExtra("email"));
            bundle.putString("firstname", getIntent().getStringExtra("firstname"));
            bundle.putString("lastname", getIntent().getStringExtra("lastname"));
            bundle.putString("language", getIntent().getStringExtra("language"));
            bundle.putString("phone", getIntent().getStringExtra("phone"));
            bundle.putString("jobtitle", getIntent().getStringExtra("jobtitle"));
            bundle.putString("affiliation_id", getIntent().getStringExtra("affiliation_id"));
            bundle.putString("chkbx_freelancer", getIntent().getStringExtra("chkbx_freelancer"));
            bundle.putString("origion_country", getIntent().getStringExtra("origion_country"));
            bundle.putString("working_country", getIntent().getStringExtra("working_country"));

            Fragment fragment = new AccountTermAndConditionFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        }
    }

    public void restoreActionBar() {

        try {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setIcon(R.drawable.info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.top_bar, null, false);
        v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        ImageView imvInfo = (ImageView) v.findViewById(R.id.imvInfo);
        ImageView imvHome = (ImageView) v.findViewById(R.id.imvHome);

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.create_account_header));

        imvHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        imvInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (scrollview_info.getVisibility() != View.VISIBLE) {
                    scrollview_info.setVisibility(View.VISIBLE);
                    scrollview_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_from_top));
                } else {
                    scrollview_info.setVisibility(View.GONE);
                    scrollview_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_out_top));
                }
            }
        });

        findViewById(R.id.imvInfoClose).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                scrollview_info.setVisibility(View.GONE);
                scrollview_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_out_top));

            }
        });

        getSupportActionBar().setCustomView(v);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {

        super.onActivityResult(arg0, arg1, arg2);
    }
}

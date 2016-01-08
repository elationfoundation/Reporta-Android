package com.iwmf;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.AlertFragment;
import com.iwmf.fragments.SocialCircleFragment;

/**
 * <p> Activity to generate new alert.
 * User can send alert to his/her circle to notify them. </p>
 */
@SuppressWarnings("ALL")
public class AlertActivity extends BaseAppCompatActivity implements OnClickListener {

    private static final String TAG = AlertActivity.class.getSimpleName();

    private ScrollView scrollview_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        scrollview_info = (ScrollView) findViewById(R.id.scrollview_info);

        findViewById(R.id.imvInfoClose).setOnClickListener(this);

        ((TextView) findViewById(R.id.txtInfo)).setText(getString(R.string.info_alerts));

        restoreActionBar(getString(R.string.alerts));

        if (savedInstanceState == null) {
            addFragment(new AlertFragment(), false);
        }

        findViewById(R.id.imbSOS).setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                performSOS(false, true);
                return false;
            }
        });
    }

    public void addFragment(Fragment f, boolean isAddToBack) {

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_container, f);
        if (isAddToBack) {
            mFragmentTransaction.addToBackStack(null);
        }
        mFragmentTransaction.commit();
    }

    public void restoreActionBar(String title) {

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
        getSupportActionBar().setCustomView(v);

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        if (TextUtils.isEmpty(title)) {
            txtTitle.setText(getString(R.string.home_contacts));
        } else {
            txtTitle.setText(title);
        }

        v.findViewById(R.id.imvInfo).setOnClickListener(this);
        v.findViewById(R.id.imvHome).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SocialCircleFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imvInfo:
                if (scrollview_info.getVisibility() != View.VISIBLE) {
                    scrollview_info.setVisibility(View.VISIBLE);
                    scrollview_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_from_top));
                } else {
                    scrollview_info.setVisibility(View.GONE);
                    scrollview_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_out_top));
                }
                break;
            case R.id.imvHome:
                finish();
                overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
                break;
            case R.id.imvInfoClose:
                scrollview_info.setVisibility(View.GONE);
                scrollview_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_out_top));
                break;
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }
}

package com.iwmf;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.AddCheckinFragment;
import com.iwmf.fragments.SocialCircleFragment;

/**
 * <p> Add new checkin by specifying time.
 * User must check in within time otherwise app will be locked and his/her circle will be notified.
 * User can edit and delete checkin.
 * </p>
 */
@SuppressWarnings("ALL")
public class AddCheckinActivity extends BaseAppCompatActivity {

    private static final String TAG = AddCheckinActivity.class.getSimpleName();

    private ScrollView scrollview_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        scrollview_info = (ScrollView) findViewById(R.id.scrollview_info);

        ((TextView) findViewById(R.id.txtInfo)).setText(getString(R.string.info_checkin));

        restoreActionBar();
        if (savedInstanceState == null) {
            addFragment(new AddCheckinFragment(), false);
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
        txtTitle.setText(getString(R.string.check_in));

        imvHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
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
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SocialCircleFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

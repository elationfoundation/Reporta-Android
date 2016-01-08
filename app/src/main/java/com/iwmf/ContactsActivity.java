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
import com.iwmf.fragments.AddContactToCircleFragment;
import com.iwmf.fragments.AllContactsAllCirclesFragment;
import com.iwmf.fragments.SocialCircleFragment;
import com.iwmf.utils.Toast;

import java.util.List;

/**
 * <p> Displays your contacts and circles.
 * Gives a list of all circles to choose from. </p>
 */
@SuppressWarnings("ALL")
public class ContactsActivity extends BaseAppCompatActivity {

    private static final String TAG = ContactsActivity.class.getSimpleName();

    private ScrollView scrollview_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_container);
        scrollview_info = (ScrollView) findViewById(R.id.scrollview_info);

        this.setResult(RESULT_OK);

        restoreActionBar(true);

        ((TextView) findViewById(R.id.txtInfo)).setText(getString(R.string.info_contacts));

        if (savedInstanceState == null) {
            addFragment(new AllContactsAllCirclesFragment(), false);
        }

        findViewById(R.id.imbSOS).setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                performSOS(false, true);
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SocialCircleFragment.class.getSimpleName());
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void addFragment(Fragment f, boolean isAddToBack) {

        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_container, f);
        if (isAddToBack) {
            mFragmentTransaction.addToBackStack(null);
        }
        mFragmentTransaction.commit();
    }

    public void restoreActionBar(boolean isShowHome) {

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

        if (isShowHome) {
            imvInfo.setVisibility(View.VISIBLE);
            imvHome.setVisibility(View.VISIBLE);
        } else {
            imvInfo.setVisibility(View.GONE);
            imvHome.setVisibility(View.GONE);
        }

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.home_contacts));

        imvHome.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Fragment mFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount());

                if (mFragment instanceof AddContactToCircleFragment) {

                    if (!AddContactToCircleFragment.isActive) {

                        finish();
                        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);

                    } else {

                        Toast.displayError(ContactsActivity.this, getString(R.string.add_atleast_one_contact));
                    }
                } else {

                    finish();
                    overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
                }

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

        List<Fragment> mFragments = getSupportFragmentManager().getFragments();

        if (mFragments != null && mFragments.size() > getSupportFragmentManager().getBackStackEntryCount()) {
            Fragment mFragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount());

            if (mFragment instanceof AddContactToCircleFragment) {

                if (!AddContactToCircleFragment.isActive) {

                    super.onBackPressed();
                    overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);

                } else {

                    Toast.displayError(this, getString(R.string.add_atleast_one_contact));
                }
            } else {

                super.onBackPressed();
                overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
            }
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
        }
    }
}

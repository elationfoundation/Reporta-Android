package com.iwmf;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iwmf.fragments.AccountCreationFragment;
import com.iwmf.utils.Utils;

/**
 * <p> Activity for creating a new user account. </p>
 */
@SuppressWarnings("ALL")
public class AccountCreationActivity extends AppCompatActivity {

    private static final String TAG = AccountCreationActivity.class.getSimpleName();

    private ImageView imvHome = null, imvInfo = null;
    private TextView txtTitle = null, txtDone = null;
    private ScrollView scrollview_info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        scrollview_info = (ScrollView) findViewById(R.id.scrollview_info);

        ((TextView) findViewById(R.id.txtInfo)).setText(getString(R.string.info_profile));

        restoreActionBar(false, "");
        if (savedInstanceState == null) {
            addFragment(new AccountCreationFragment(), false);
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

    public void restoreActionBar(boolean isWantBack, String title) {

        if (isWantBack) {

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

            View v = LayoutInflater.from(this).inflate(R.layout.top_bar_back, null, false);
            v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            getSupportActionBar().setCustomView(v);

            txtDone = (TextView) v.findViewById(R.id.txtDone);
            txtDone.setText(getString(R.string.done));
            txtDone.setVisibility(View.VISIBLE);
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            if (Utils.isEmpty(title)) {
                txtTitle.setText(getString(R.string.home_contacts));
            } else {

                if (title.startsWith("Coun")) {
                    txtTitle.setText(getString(R.string.countries));
                } else {
                    txtTitle.setText(title);
                }
            }

        } else {

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

            View v = LayoutInflater.from(this).inflate(R.layout.top_bar, null, false);
            v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            imvInfo = (ImageView) v.findViewById(R.id.imvInfo);
            imvHome = (ImageView) v.findViewById(R.id.imvHome);

            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtTitle.setText(getString(R.string.create_account_header));

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
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }

}

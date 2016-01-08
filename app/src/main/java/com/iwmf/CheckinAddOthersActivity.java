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
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.AddCircleFragment;
import com.iwmf.utils.SelectedScreen;

/**
 * <p> This activity is used to add other members to existing circle.
 * AddCircleFragment will be called to add circle </p>
 */
@SuppressWarnings("ALL")
public class CheckinAddOthersActivity extends BaseAppCompatActivity {

    private static final String TAG = CheckinAddOthersActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        restoreActionBar();

        if (savedInstanceState == null) {

            Fragment mFragment = AddCircleFragment.getInstance(SelectedScreen.CHECKIN, true, "");

            addFragment(mFragment, false);
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

        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.top_bar_back, null, false);
        v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.check_in));

        getSupportActionBar().setCustomView(v);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {

        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }
}

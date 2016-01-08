package com.iwmf;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.IamFacingFragment;

/**
 * <p> User can describe what he/she is facing in
 * terms of its situation. It shows a list of all situation. </p>
 */
@SuppressWarnings("ALL")
public class IamFacingActvity extends BaseAppCompatActivity {

    private static final String TAG = IamFacingActvity.class.getSimpleName();

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_container);

        restoreActionBar(getIntent().getStringExtra("title"));

        if (savedInstanceState == null) {

            Bundle bundle = new Bundle();
            bundle.putString("facingIssue", getIntent().getStringExtra("facingIssue"));

            fragment = new IamFacingFragment();
            fragment.setArguments(bundle);

            addFragment(fragment, false);
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
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.top_bar_back, null, false);
        v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView txtDone = (TextView) v.findViewById(R.id.txtDone);
        txtDone.setText(getString(R.string.done));
        txtDone.setVisibility(View.VISIBLE);

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        if (TextUtils.isEmpty(title)) {
            txtTitle.setText("");
        } else {
            txtTitle.setText(title);
        }

        txtDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ((IamFacingFragment) fragment).onDoneClick();
                overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
            }
        });

        getSupportActionBar().setCustomView(v);

    }
}

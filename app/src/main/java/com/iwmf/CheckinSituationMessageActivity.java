package com.iwmf;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.CheckinSituationMessageFragment;

/**
 * <p> Describe situation you are reporting for in this activity.
 * Leave a message about current situation.  </p>
 */
@SuppressWarnings("ALL")
public class CheckinSituationMessageActivity extends BaseAppCompatActivity {

    private static final String TAG = CheckinSituationMessageActivity.class.getSimpleName();

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_container);

        restoreActionBar(getIntent().getExtras().getString("title"));

        if (savedInstanceState == null) {

            Bundle bundle = new Bundle();
            bundle.putString("value", getIntent().getStringExtra("value"));

            fragment = new CheckinSituationMessageFragment();
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

    public void restoreActionBar(final String title) {

        try {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.top_bar_back, null, false);
        v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        TextView txtDone = (TextView) v.findViewById(R.id.txtDone);
        txtTitle.setText(title);

        txtDone.setText(getString(R.string.done));
        txtDone.setVisibility(View.VISIBLE);

        txtDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ((CheckinSituationMessageFragment) fragment).onDoneClick();
                overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
            }
        });

        getSupportActionBar().setCustomView(v);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }

}

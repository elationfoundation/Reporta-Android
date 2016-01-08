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
import com.iwmf.fragments.PickLocationFragment;

/**
 * <p> Opens Google map and displays current user location.
 * Also shows the list of nearby locations to select for the user. </p>
 */
@SuppressWarnings("ALL")
public class PickLocationActivity extends BaseAppCompatActivity {

    private TextView txtTitle = null;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_container);

        if (savedInstanceState == null) {

            fillBundleData(getIntent().getExtras());

            Bundle bundle = new Bundle();
            bundle.putString("address", getIntent().getStringExtra("address"));
            bundle.putDouble("latitude", getIntent().getDoubleExtra("latitude", 0));
            bundle.putDouble("longitude", getIntent().getDoubleExtra("longitude", 0));

            fragment = new PickLocationFragment();
            fragment.setArguments(bundle);

            addFragment(fragment, false);

        } else {
            fillBundleData(savedInstanceState);
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
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString("title", txtTitle.getText().toString().trim());
    }

    private void fillBundleData(Bundle bundle) {

        restoreActionBar(bundle.getString("title"));
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

        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.top_bar_back, null, false);
        v.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView txtDone = (TextView) v.findViewById(R.id.txtDone);
        txtDone.setText(getString(R.string.done));
        txtDone.setVisibility(View.VISIBLE);

        txtTitle = (TextView) v.findViewById(R.id.txtTitle);

        if (TextUtils.isEmpty(title)) {
            txtTitle.setText("");
        } else {
            txtTitle.setText(title);
        }

        txtDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ((PickLocationFragment) fragment).getLatLongFromLocation();
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

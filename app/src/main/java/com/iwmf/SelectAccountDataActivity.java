package com.iwmf;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.SelectAccountDataFragment;
import com.iwmf.utils.Utils;

/**
 * <p> User can enter more details about profile like job, country, etc. </p>
 */
@SuppressWarnings("ALL")
public class SelectAccountDataActivity extends BaseAppCompatActivity {

    private static final String TAG = SelectAccountDataActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_container);

        restoreActionBar(getIntent().getStringExtra("title"));

        if (savedInstanceState == null) {

            Bundle bundle = new Bundle();
            bundle.putString("selectdata", getIntent().getStringExtra("selectdata"));
            bundle.putString("titleid", getIntent().getStringExtra("title"));
            bundle.putString("selectjobtitle", getIntent().getStringExtra("selectjobtitle"));
            bundle.putString("selectcountryorigin", getIntent().getStringExtra("selectcountryorigin"));
            bundle.putString("selectcountryworking", getIntent().getStringExtra("selectcountryworking"));

            Fragment fragment = new SelectAccountDataFragment();
            fragment.setArguments(bundle);

            addFragment(fragment, false);
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
        getSupportActionBar().setCustomView(v);

        TextView txtDone = (TextView) v.findViewById(R.id.txtDone);
        txtDone.setText(getString(R.string.done));
        txtDone.setVisibility(View.VISIBLE);
        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        if (Utils.isEmpty(title)) {
            txtTitle.setText(getString(R.string.home_contacts));
        } else {

            if (title.startsWith("Coun")) {
                txtTitle.setText(getString(R.string.countries));
            } else {
                txtTitle.setText(title);
            }
        }
    }

}

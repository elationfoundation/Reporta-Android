package com.iwmf;

import android.os.Bundle;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.AboutReportaFragment;

/**
 * <p> About page of Reporta app.
 * This shows the detailed information about the Reporta.
 * Calls AboutReportaFragment. </p>
 */
public class AboutMeActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutReportaFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.abc_slide_out_top);
    }

}

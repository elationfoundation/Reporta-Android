package com.iwmf;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.PendingMediaFragment;

/**
 * <p> Shows the list of pending media files to upload on server. </p>
 */
public class PendingMediaActivity extends BaseAppCompatActivity {

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(FRAGMENT_CONTAINER, new PendingMediaFragment()).commit();
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
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.abc_slide_out_top);

    }
}

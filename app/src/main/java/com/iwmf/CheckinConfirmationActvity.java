package com.iwmf;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.CheckinConfirmationFragment;

/**
 * <p> Confirmation for checkin close. </p>
 */
public class CheckinConfirmationActvity extends BaseAppCompatActivity {

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(FRAGMENT_CONTAINER, new CheckinConfirmationFragment()).commit();
        }

        findViewById(R.id.imbSOS).setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                performSOS(false, true);
                return false;
            }
        });

    }
}

package com.iwmf;

import android.os.Bundle;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.fragments.SelectLanguageFragment;

/**
 * <p> Language selection option for the user. </p>
 */
public class SelectLanguageActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectLanguageFragment()).commit();
        }
    }
}

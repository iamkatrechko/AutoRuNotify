package com.ramgaunt.autorunotify.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ramgaunt.autorunotify.R;
import com.ramgaunt.autorunotify.fragment.EnterCaptchaFragment;

/**
 * @author iamkatrechko
 *         Date: 05.05.2017
 */
public class EnterCaptchaActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String URI = getIntent().getStringExtra("URL");

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, EnterCaptchaFragment.newInstance(URI))
                    .commit();
        }
    }
}

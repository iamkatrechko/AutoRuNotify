package com.ramgaunt.autorunotify.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String URI = "https://m.auto.ru/cars/lexus/gx/all/?image=true&sort_offers=cr_date-DESC&page_num_offers=1";

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, EnterCaptchaFragment.newInstance(URI))
                    .commit();
        }
    }
}

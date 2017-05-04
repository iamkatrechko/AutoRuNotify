package com.ramgaunt.autorunotify.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ramgaunt.autorunotify.R;
import com.ramgaunt.autorunotify.fragment.MainActivityFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MainActivityFragment.newInstance())
                    .commit();
        }
    }
}

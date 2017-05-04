package com.ramgaunt.autorunotify.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.R;

public class NotWorkActivity extends AppCompatActivity {
    private Methods mMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_work);
        mMethods = new Methods(this);

        findViewById(R.id.bRecreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(1);
                finish();
            }
        });

        findViewById(R.id.bSendMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMethods.mSendMail("");
            }
        });
    }
}

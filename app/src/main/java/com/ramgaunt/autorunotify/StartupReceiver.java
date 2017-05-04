package com.ramgaunt.autorunotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.ArrayList;

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        QueryLab queryLab = QueryLab.get(context);
        ArrayList<Query> queries = queryLab.getAll();
        for (Query query : queries){
            if (query.isOn()){
                SearchIntentService.setServiceAlarm(context, query.getId(), false);
                SearchIntentService.setServiceAlarm(context, query.getId(), query.isOn());
            }
        }
    }
}

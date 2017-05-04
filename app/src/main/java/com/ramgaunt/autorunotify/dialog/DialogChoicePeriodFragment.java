/*
 * Decompiled with CFR 0_92.
 * 
 * Could not load the following classes:
 *  android.app.AlertDialog
 *  android.app.AlertDialog$Builder
 *  android.app.Dialog
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.content.Intent
 *  android.database.Cursor
 *  android.os.Bundle
 *  android.support.v4.app.DialogFragment
 *  android.support.v4.app.Fragment
 *  android.support.v4.app.FragmentActivity
 *  android.support.v4.content.CursorLoader
 *  android.support.v4.widget.CursorAdapter
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.View$OnClickListener
 *  android.view.ViewGroup
 *  android.widget.CheckBox
 *  android.widget.ListAdapter
 *  android.widget.ListView
 *  android.widget.TextView
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 */
package com.ramgaunt.autorunotify.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.ramgaunt.autorunotify.R;

public class DialogChoicePeriodFragment extends DialogFragment {

    public static DialogChoicePeriodFragment newInstance() {
        return new DialogChoicePeriodFragment();
    }

    private void sendResult(int period) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("period", period);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {

        final String[] mItemsNames = getResources().getStringArray(R.array.periods);
        final int[] mItemsValues = getResources().getIntArray(R.array.periods_values);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setItems(mItemsNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                sendResult(mItemsValues[item]);
            }
        });
        return builder.create();
    }
}


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
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ramgaunt.autorunotify.R;

public class DialogSetTimeFragment extends DialogFragment {
    private boolean isAround;
    private String timeFrom;
    private String timeTo;

    private Integer hourFrom;
    private Integer minuteFrom;
    private Integer hourTo;
    private Integer minuteTo;

    public static DialogSetTimeFragment newInstance(boolean isAround, String timeFrom, String timeTo) {
        DialogSetTimeFragment fragment = new DialogSetTimeFragment();
        Bundle args = new Bundle();

        args.putBoolean("isAround", isAround);
        args.putString("timeFrom", timeFrom);
        args.putString("timeTo", timeTo);

        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(boolean isAround, String timeFrom, String timeTo) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("isAround", isAround);
        a.putExtra("timeFrom", timeFrom);
        a.putExtra("timeTo", timeTo);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        isAround = getArguments().getBoolean("isAround");
        timeFrom = getArguments().getString("timeFrom");
        timeTo = getArguments().getString("timeTo");

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
        final CheckBox cbAround = (CheckBox) view.findViewById(R.id.cbAround);
        final EditText etFrom = (EditText) view.findViewById(R.id.etFrom);
        final EditText etTo = (EditText) view.findViewById(R.id.etTo);

        cbAround.setChecked(isAround);
        etFrom.setText(timeFrom);
        etTo.setText(timeTo);

        etFrom.setEnabled(!isAround);
        etTo.setEnabled(!isAround);

        setBufTimeFrom(timeFrom);
        setBufTimeTo(timeTo);

        cbAround.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAround = b;
                etFrom.setEnabled(!isAround);
                etTo.setEnabled(!isAround);
            }
        });

        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TimePickerDialog.OnTimeSetListener kTimePickerListener =
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                hourFrom = i;
                                minuteFrom = i1;
                                String bTime = "" + String.format("%02d", hourFrom) + ":" + String.format("%02d", minuteFrom);
                                ((EditText) view).setText(bTime);
                            }
                        };

                TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), kTimePickerListener, hourFrom, minuteFrom, true);
                timeDialog.show();
            }
        });

        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                TimePickerDialog.OnTimeSetListener kTimePickerListener =
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                hourTo = i;
                                minuteTo = i1;
                                String bTime = "" + String.format("%02d", hourTo) + ":" + String.format("%02d", minuteTo);
                                ((EditText) view).setText(bTime);
                            }
                        };

                TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), kTimePickerListener, hourTo, minuteTo, true);
                timeDialog.show();
            }
        });

        /*
        if (time.equals("null")){
            editTextTime.setText("12:00");
            setBufTime("12:00");
        }else {
            checkBoxTime.setChecked(true);
            editTextTime.setEnabled(true);
            editTextTime.setText(time);
            setBufTime(time);
        }

        checkBoxTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextTime.setEnabled(isChecked);
                if (isChecked) {
                    TimePickerDialog.OnTimeSetListener kTimePickerListener =
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                    hour = i;
                                    minute = i1;
                                    String bTime = "" + String.format("%02d", hour) + ":" + String.format("%02d", minute);
                                    editTextTime.setText(bTime);
                                }
                            };
                    TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), kTimePickerListener, hour, minute, true);
                    timeDialog.show();
                }
            }
        });*/

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Установка времени")
                .setPositiveButton("Применить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int n) {
                        sendResult(isAround, getTimeFrom(), getTimeTo());
                        /*if (checkBoxTime.isChecked()) {
                            time = editTextTime.getText().toString();
                        } else {
                            time = "null";
                        }
                        date = editTextDate.getText().toString();
                        sendResult(date, time);*/
                    }
                }).create();
    }
/*
    private void setBufDate(String date){
        year = Integer.valueOf(date.split("\\.")[2]);
        month = Integer.valueOf(date.split("\\.")[1]) - 1;
        day = Integer.valueOf(date.split("\\.")[0]);
    }*/

    private void setBufTimeFrom(String time){
        String[] a = time.split("\\:");
        hourFrom = Integer.valueOf(a[0]);
        minuteFrom =  Integer.valueOf(a[1]);
    }

    private void setBufTimeTo(String time){
        String[] a = time.split("\\:");
        hourTo = Integer.valueOf(a[0]);
        minuteTo =  Integer.valueOf(a[1]);
    }

    private String getTimeFrom(){
        return  "" + String.format("%02d", hourFrom) + ":" + String.format("%02d", minuteFrom);
    }

    private String getTimeTo(){
        return  "" + String.format("%02d", hourTo) + ":" + String.format("%02d", minuteTo);
    }
}
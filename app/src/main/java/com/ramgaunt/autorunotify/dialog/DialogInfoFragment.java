
package com.ramgaunt.autorunotify.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.R;

public class DialogInfoFragment extends DialogFragment {
    Methods m;

    public static DialogInfoFragment newInstance() {
        return new DialogInfoFragment();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        m = new Methods(getActivity());
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_info, null);

        v.findViewById(R.id.buttonReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.mGoToGooglePlay();
            }
        });
        v.findViewById(R.id.buttonOtherApps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.mGoToGooglePlayDeveloper();
            }
        });
        v.findViewById(R.id.buttonSendMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.mSendMail("");
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}


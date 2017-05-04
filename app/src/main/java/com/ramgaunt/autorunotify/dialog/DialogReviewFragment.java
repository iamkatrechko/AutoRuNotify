
package com.ramgaunt.autorunotify.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.R;

public class DialogReviewFragment extends DialogFragment {
    AlertDialog dialog;
    Methods m;

    public static DialogReviewFragment newInstance() {
        return new DialogReviewFragment();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        m = new Methods(getActivity());
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_review, null);

        v.findViewById(R.id.buttonReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.getReviewIsShowed(true);
                m.mGoToGooglePlay();
                dialog.cancel();
            }
        });
        v.findViewById(R.id.buttonLate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.getReviewIsShowed(false);
                dialog.cancel();
            }
        });
        v.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.getReviewIsShowed(true);
                dialog.cancel();
            }
        });

        dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .create();
        return dialog;
    }
}


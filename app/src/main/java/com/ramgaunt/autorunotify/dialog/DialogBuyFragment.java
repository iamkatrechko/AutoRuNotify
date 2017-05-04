
package com.ramgaunt.autorunotify.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.R;

public class DialogBuyFragment extends DialogFragment implements BillingProcessor.IBillingHandler {
    private String KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhxHTDrY2VjM++xnA7HII5YvouX9gr8Fptsy4TTLwikETLWT8DcuT4oM9DMCetV8XHg69kz94PQIaf59p9TLL/wEaIFETcKX6xh0N+avqw2yFdEfN6q8Bs4Sjxm12Ca6vrW61I/mQptgmlizIiz60Zw8N/SBQqMJlL+IqU/W5qcZEiubfuUPIEBIofMnoWmC6j8f4yH2C9EDb49mmDI7/OBSqr8jw0b5gQTs9Q/lED5gsXU5OHusoX2TWabrB5CSZ+n4/aaGf1zkdldArUOADM+K+EnujoB5hil5j9c+6dlQy1EDUXsiP/nq63oFr5fOsA7SZu5CaoYjq6bTVYYHjbQIDAQAB";
    private final String PREMIUM_IDENTIFIER_1 = "premium_identifier_1";
    private final String PREMIUM_IDENTIFIER_2 = "premium_identifier_2";
    private final String PREMIUM_IDENTIFIER_3 = "premium_identifier_3";

    private static String TAG = "DialogBuyFragment";
    private BillingProcessor bp;
    private Methods m;

    private LinearLayout linBuy1;
    private LinearLayout linBuy2;
    private LinearLayout linBuy3;

    private TextView tvPrice1;
    private TextView tvPrice2;
    private TextView tvPrice3;

    public static DialogBuyFragment newInstance() {
        return new DialogBuyFragment();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_buy, null);

        m = new Methods(getActivity());
        bp = new BillingProcessor(getActivity(), KEY, this);

        if(!BillingProcessor.isIabServiceAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.billing_unavailable, Toast.LENGTH_SHORT).show();
        }

        tvPrice1 = ((TextView) v.findViewById(R.id.tvPrice1));
        tvPrice2 = ((TextView) v.findViewById(R.id.tvPrice2));
        tvPrice3 = ((TextView) v.findViewById(R.id.tvPrice3));

        linBuy1 = (LinearLayout) v.findViewById(R.id.linBuy1);
        linBuy2 = (LinearLayout) v.findViewById(R.id.linBuy2);
        linBuy3 = (LinearLayout) v.findViewById(R.id.linBuy3);

        setPremiumView(m.getPLevel());

        linBuy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), PREMIUM_IDENTIFIER_1);
            }
        });

        linBuy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), PREMIUM_IDENTIFIER_2);
            }
        });

        linBuy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), PREMIUM_IDENTIFIER_3);
            }
        });

        v.findViewById(R.id.linRestore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.loadOwnedPurchasesFromGoogle();

                TransactionDetails t3 = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_3);
                if (t3 != null) {
                    setLicense(3);
                } else {

                    TransactionDetails t2 = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_2);
                    if (t2 != null) {
                        setLicense(2);
                    } else {

                        TransactionDetails t1 = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_1);
                        if (t1 != null) {
                            setLicense(1);
                        } else {

                            Toast.makeText(getActivity(), "У вас нет оплаченных лицензий", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

    /**
     * Устанавливает внешний вид диалога в зависимости от лицензии
     * @param level Уровень лицензии
     */
    private void setPremiumView(Integer level){
        switch (level){
            case 3:
                linBuy1.setVisibility(View.GONE);
                linBuy2.setVisibility(View.GONE);
                tvPrice3.setText("активировано");
                break;
            case 2:
                linBuy1.setVisibility(View.GONE);
                tvPrice2.setText("активировано");
                break;
            case 1:
                tvPrice1.setText("активировано");
        }
    }

    /**
     * Устанавливает и записывает лицензию
     * @param level Уровень лицензии
     */
    private void setLicense(int level){
        switch (level){
            case 1:
                m.setLevelPref(1);
                Toast.makeText(getActivity(), "Активирована лицензия \"До 5 поисков\"", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                m.setLevelPref(2);
                Toast.makeText(getActivity(), "Активирована лицензия \"До 10 поисков\"", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                m.setLevelPref(3);
                Toast.makeText(getActivity(), "Активирована лицензия \"Без ограничений\"", Toast.LENGTH_SHORT).show();
                break;
        }
        setPremiumView(1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Слушатели ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: " + productId + " : " + details.orderId);

        switch (productId){
            case PREMIUM_IDENTIFIER_1:
                setLicense(1);
                break;
            case PREMIUM_IDENTIFIER_2:
                setLicense(2);
                break;
            case PREMIUM_IDENTIFIER_3:
                setLicense(3);
                break;
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(TAG, "onBillingError");

    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }
}


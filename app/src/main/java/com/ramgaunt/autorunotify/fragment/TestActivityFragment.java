package com.ramgaunt.autorunotify.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.QueryLab;
import com.ramgaunt.autorunotify.R;

public class TestActivityFragment extends Fragment implements BillingProcessor.IBillingHandler{
    private static String TAG = "DialogBuyFragment";
    private String KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhxHTDrY2VjM++xnA7HII5YvouX9gr8Fptsy4TTLwikETLWT8DcuT4oM9DMCetV8XHg69kz94PQIaf59p9TLL/wEaIFETcKX6xh0N+avqw2yFdEfN6q8Bs4Sjxm12Ca6vrW61I/mQptgmlizIiz60Zw8N/SBQqMJlL+IqU/W5qcZEiubfuUPIEBIofMnoWmC6j8f4yH2C9EDb49mmDI7/OBSqr8jw0b5gQTs9Q/lED5gsXU5OHusoX2TWabrB5CSZ+n4/aaGf1zkdldArUOADM+K+EnujoB5hil5j9c+6dlQy1EDUXsiP/nq63oFr5fOsA7SZu5CaoYjq6bTVYYHjbQIDAQAB";
    private BillingProcessor bp;
    private Methods mMethods;
    private QueryLab mQueryLab;

    private final String PREMIUM_IDENTIFIER_1 = "premium_identifier_1";
    private final String PREMIUM_IDENTIFIER_2 = "premium_identifier_2";
    private final String PREMIUM_IDENTIFIER_3 = "premium_identifier_3";

    public TestActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test, container, false);
        mMethods = new Methods(getActivity());
        mQueryLab = QueryLab.get(getActivity());

        if(!BillingProcessor.isIabServiceAvailable(getActivity())) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(getActivity(), KEY, this);

        String stringLevel = "Уровни - " + mMethods.getPLevel();
        ((TextView) v.findViewById(R.id.tvLevel)).setText(stringLevel);

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMethods.setLevelPref(0);
            }
        });

        v.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMethods.setLevelPref(1);
            }
        });

        v.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMethods.setLevelPref(2);
            }
        });

        v.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMethods.setLevelPref(3);
            }
        });

        v.findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), PREMIUM_IDENTIFIER_1);
            }
        });

        v.findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAvailable = BillingProcessor.isIabServiceAvailable(getActivity());
                Log.d(TAG, "" + isAvailable);

                SkuDetails skuDetails = bp.getPurchaseListingDetails(PREMIUM_IDENTIFIER_1);
                showSkuDetails(skuDetails);
            }
        });

        v.findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionDetails transactionDetails = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_1);
                showTransactionDetails(transactionDetails);
            }
        });

        v.findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), PREMIUM_IDENTIFIER_2);
            }
        });

        v.findViewById(R.id.button13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAvailable = BillingProcessor.isIabServiceAvailable(getActivity());
                Log.d(TAG, "" + isAvailable);

                SkuDetails skuDetails = bp.getPurchaseListingDetails(PREMIUM_IDENTIFIER_2);
                showSkuDetails(skuDetails);
            }
        });

        v.findViewById(R.id.button14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionDetails transactionDetails = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_2);
                showTransactionDetails(transactionDetails);
            }
        });

        v.findViewById(R.id.button15).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), PREMIUM_IDENTIFIER_3);
            }
        });

        v.findViewById(R.id.button16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAvailable = BillingProcessor.isIabServiceAvailable(getActivity());
                Log.d(TAG, "" + isAvailable);

                SkuDetails skuDetails = bp.getPurchaseListingDetails(PREMIUM_IDENTIFIER_3);
                showSkuDetails(skuDetails);
            }
        });

        v.findViewById(R.id.button17).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionDetails transactionDetails = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_3);
                showTransactionDetails(transactionDetails);
            }
        });

        v.findViewById(R.id.button18).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*bp.loadOwnedPurchasesFromGoogle();*/
                if (bp.loadOwnedPurchasesFromGoogle()) {
                    showToast("Subscriptions updated.");
                }

            }
        });

        v.findViewById(R.id.button19).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQueryLab.generate(20);
            }
        });

        String startCount = "Количество запусков: " + mMethods.getStartCount();
        ((TextView) v.findViewById(R.id.textView7)).setText(startCount);

        return v;
    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: " + productId + " : " + details.orderId);
        showToast(productId + " : " + details.orderId);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored");
        showToast("onPurchaseHistoryRestored");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(TAG, "onBillingError");
        showToast("onBillingInitialized");
    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized");
        showToast("onBillingInitialized");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        showToast("onActivityResult");
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

    private void showSkuDetails(SkuDetails skuDetails){
        if (skuDetails == null){
            Log.d(TAG, "skuDetails == null");
            showToast("skuDetails == null");
            return;
        }

        String log = "";
        log += "productId: " + skuDetails.productId + "\n";
        log += "title: " + skuDetails.title + "\n";
        log += "currency: " + skuDetails.currency + "\n";
        log += "isSubscription: " + skuDetails.isSubscription + "\n";
        log += "priceValue: " + skuDetails.priceValue + "\n";
        log += "priceText: " + skuDetails.priceText + "\n";
        log += "priceLong: " + skuDetails.priceLong + "\n";
        log += "description: " + skuDetails.description + "\n";
        Log.d(TAG, log);
        showToast("SKU" + log);
    }

    private void showTransactionDetails(TransactionDetails transactionDetails){
        if (transactionDetails == null){
            Log.d(TAG, "transactionDetails == null");
            showToast("transactionDetails == null");
            return;
        }

        String log = "";
        log += "productId: " + transactionDetails.productId + "\n";
        log += "orderId: " + transactionDetails.orderId + "\n";
        log += "purchaseToken: " + transactionDetails.purchaseToken + "\n";
        log += "purchaseInfo: " + transactionDetails.purchaseInfo + "\n";
        log += "purchaseTime: " + transactionDetails.purchaseTime + "\n";
        Log.d(TAG, log);
        showToast("TRANSACTION" + log);
    }


    private void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}

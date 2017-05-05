package com.ramgaunt.autorunotify.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.QueryLab;
import com.ramgaunt.autorunotify.R;
import com.ramgaunt.autorunotify.activity.CreateActivity;
import com.ramgaunt.autorunotify.activity.NotWorkActivity;
import com.ramgaunt.autorunotify.activity.SettingsActivity;
import com.ramgaunt.autorunotify.activity.TestActivity;
import com.ramgaunt.autorunotify.adapter.QueriesCursorAdapter;
import com.ramgaunt.autorunotify.dialog.DialogBuyFragment;
import com.ramgaunt.autorunotify.dialog.DialogInfoFragment;
import com.ramgaunt.autorunotify.dialog.DialogReviewFragment;
import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.ArrayList;

/**
 * Фрагмент со список всех созданных поисков
 */
public class MainActivityFragment extends Fragment implements QueriesCursorAdapter.OnEmptyListener {
    private final String TAG = "MainActivityFragment";
    private final String KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhxHTDrY2VjM++xnA7HII5YvouX9gr8Fptsy4TTLwikETLWT8DcuT4oM9DMCetV8XHg69kz94PQIaf59p9TLL/wEaIFETcKX6xh0N+avqw2yFdEfN6q8Bs4Sjxm12Ca6vrW61I/mQptgmlizIiz60Zw8N/SBQqMJlL+IqU/W5qcZEiubfuUPIEBIofMnoWmC6j8f4yH2C9EDb49mmDI7/OBSqr8jw0b5gQTs9Q/lED5gsXU5OHusoX2TWabrB5CSZ+n4/aaGf1zkdldArUOADM+K+EnujoB5hil5j9c+6dlQy1EDUXsiP/nq63oFr5fOsA7SZu5CaoYjq6bTVYYHjbQIDAQAB";
    private final String PREMIUM_IDENTIFIER_1 = "premium_identifier_1";
    private final String PREMIUM_IDENTIFIER_2 = "premium_identifier_2";
    private final String PREMIUM_IDENTIFIER_3 = "premium_identifier_3";

    private static final int DIALOG_REVIEW = 515125;
    private static final int DIALOG_INFO = 151251;
    private static final int DIALOG_BUY = 126122;
    private static final int DIALOG_HOW_IT_WORK = 372521;
    private static final int DIALOG_NOT_WORK = 732363;

    private static final int RESULT_FULL_RECREATE = 12;

    private RecyclerView recyclerView;
    private QueriesCursorAdapter adapter;
    private QueryLab queryLab;
    private Methods mMethods;
    private LinearLayout linEmpty;
    private BillingProcessor bp;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mMethods = new Methods(getActivity());
        queryLab = QueryLab.get(getActivity());

        mMethods.incrementStartCount();


        bp = new BillingProcessor(getActivity(), KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {

            }

            @Override
            public void onPurchaseHistoryRestored() {

            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {

            }

            @Override
            public void onBillingInitialized() {
                if (bp.loadOwnedPurchasesFromGoogle()) {
                    checkPremium();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        linEmpty = (LinearLayout) v.findViewById(R.id.linEmpty);
        recyclerView = (RecyclerView) v.findViewById(R.id.section_list);                            //Находим ID виджета

        adapter = new QueriesCursorAdapter(queryLab.getAll(), getActivity(), this);

        recyclerView.setHasFixedSize(true);                                                         //Фиксируем размер виджета recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (queryLab.getQueries().size() >= mMethods.getAllowedSearches()) {
                    showDialog(DIALOG_BUY);
                } else {
                    Intent i = new Intent(getActivity(), CreateActivity.class);
                    i.putExtra("ID", -1);
                    startActivity(i);
                }
            }
        });

        if (mMethods.showDialogReview()) {
            showDialog(DIALOG_REVIEW);
        }

        if (mMethods.getStartCount() == 1) {
            showDialog(DIALOG_HOW_IT_WORK);
        }

        v.findViewById(R.id.button_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query lastQuery = queryLab.getLast();
                if (lastQuery != null) {
                    Intent i = new Intent(getActivity(), SearchIntentService.class);
                    i.putExtra("ID", lastQuery.getId());
                    getActivity().startService(i);
                } else {
                    Toast.makeText(getActivity(), "Необходимо создать хотя бы один поиск", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAndBlock();
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        if (mMethods.getPLevel() == 3) {
            menu.findItem(R.id.action_unlock).setVisible(false);
        }
        if (Methods.isDeveloper) {
            menu.findItem(R.id.action_test).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_review:
                mMethods.mGoToGooglePlay();
                break;
            case R.id.action_settings: {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                break;
            }
            case R.id.action_info:
                showDialog(DIALOG_INFO);
                break;
            case R.id.action_unlock:
                showDialog(DIALOG_BUY);
                break;
            case R.id.action_how_it_work:
                showDialog(DIALOG_HOW_IT_WORK);
                break;
            case R.id.action_not_work:
                showDialog(DIALOG_NOT_WORK);
                break;
            case R.id.recreate:
                recreate();
                break;
            case R.id.action_recreate:
                fullRecreate();
                break;
            case R.id.action_test:
                Intent i2 = new Intent(getActivity(), TestActivity.class);
                startActivity(i2);
        }
        return true;
    }

    private void recreate() {
        for (Query query : queryLab.getQueries()) {
            if (query.isOn()) {
                SearchIntentService.hardAlarmOff(getActivity(), query.getId());
                SearchIntentService.setServiceAlarm(getActivity(), query.getId(), true);
            }
        }
    }

    private void showDialog(int dialogCode) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch (dialogCode) {
            case DIALOG_INFO:
                DialogInfoFragment fragmentDialogInfo = DialogInfoFragment.newInstance();
                fragmentDialogInfo.setTargetFragment(this, DIALOG_INFO);
                fragmentDialogInfo.show(fragmentManager, "dialogInfo");
                break;
            case DIALOG_REVIEW:
                DialogReviewFragment fragmentDialogReview = DialogReviewFragment.newInstance();
                fragmentDialogReview.setTargetFragment(this, DIALOG_REVIEW);
                fragmentDialogReview.show(fragmentManager, "dialogReview");
                break;
            case DIALOG_BUY:
                DialogBuyFragment fragmentDialogBuy = DialogBuyFragment.newInstance();
                fragmentDialogBuy.setTargetFragment(this, DIALOG_REVIEW);
                fragmentDialogBuy.show(fragmentManager, "dialogBuy");
                break;
            case DIALOG_HOW_IT_WORK:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getWindow().getContext());
                builder.setTitle("Руководство")
                        .setMessage(R.string.how_it_work)
                        .setCancelable(false)
                        .setPositiveButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case DIALOG_NOT_WORK:
                Intent i = new Intent(getActivity(), NotWorkActivity.class);
                startActivityForResult(i, RESULT_FULL_RECREATE);
                break;
        }
    }

    /** Пересоздает поиски, создавая копии и удаляя старые */
    public void fullRecreate() {
        ArrayList<Query> list = queryLab.getAll();

        for (Query query : list) {
            query.setLastId("-1");
            query.setLastDate("01.01.2001-00:00");
            queryLab.deleteById(query.getId());
            queryLab.addQuery(query);
        }
        Toast.makeText(getActivity(), "Поиски пересозданы", Toast.LENGTH_SHORT).show();

        if (adapter != null) {
            ArrayList<Query> newList = queryLab.getAll();
            for (Query query : newList) {
                SearchIntentService.setServiceAlarm(getActivity(), query.getId(), query.isOn());
            }

            adapter.replaceQueries(newList);
            adapter.notifyDataSetChanged();
        }
    }

    public void showEmpty(boolean isShow) {
        recyclerView.setVisibility(isShow ? View.GONE : View.VISIBLE);
        linEmpty.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * Проверяет на наличие недопустимых поисков и выключает их, перезаписывая базу
     */
    private void checkAndBlock() {
        if (queryLab.getQueries().size() > mMethods.getAllowedSearches()) {
            Log.d(TAG, "Количество поисков больше допустимого: " + queryLab.getQueries().size() + ">" + mMethods.getAllowedSearches());
            ArrayList<Query> queries = queryLab.getQueries();

            for (int i = 1; i <= queries.size(); i++) {
                if (i > mMethods.getAllowedSearches()) {
                    Log.d(TAG, "Отключение поиска #" + i);
                    Query query = queries.get(i - 1);
                    query.setOn(false);
                    SearchIntentService.setServiceAlarm(getActivity(), query.getId(), false);
                    queryLab.updateQuery(query);
                }
            }
        }

        if (adapter != null) {
            adapter.replaceQueries(queryLab.getAll());
            adapter.notifyDataSetChanged();
        }
    }

    private void checkPremium() {
        TransactionDetails t3 = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_3);
        int level;
        if (t3 != null) {

            level = 3;
        } else {

            TransactionDetails t2 = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_2);
            if (t2 != null) {
                level = 2;
            } else {

                TransactionDetails t1 = bp.getPurchaseTransactionDetails(PREMIUM_IDENTIFIER_1);
                if (t1 != null) {
                    level = 1;
                } else {

                    level = 0;
                }
            }
        }

        mMethods.setLevelPref(level);
        Methods.developerToast(getActivity(), "Уровень: " + level);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == RESULT_FULL_RECREATE && resultCode == 1) {
            fullRecreate();
        }
    }
}

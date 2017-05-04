package com.ramgaunt.autorunotify.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ramgaunt.autorunotify.activity.BrowserActivity;
import com.ramgaunt.autorunotify.activity.CreateActivity;
import com.ramgaunt.autorunotify.dialog.DialogChoicePeriodFragment;
import com.ramgaunt.autorunotify.dialog.DialogSetTimeFragment;
import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.QueryLab;
import com.ramgaunt.autorunotify.R;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Фрагмент окна создания/найстройки поиска
 */
public class CreateActivityFragment extends Fragment implements View.OnClickListener{
    private static final int DIALOG_PERIOD_FRAGMENT = 646545;
    private static final int DIALOG_TIME_FRAGMENT = 125125;

    private boolean isAround;
    private String timeFrom;
    private String timeTo;

    private EditText etTitle;
    private TextView tvPeriod;
    private TextView tvTime;
    private TextView tvURI;

    private QueryLab queryLab;
    private Query query;

    private String[] mPeriodsNames;
    private int[] mPeriodsValues;

    public CreateActivityFragment() {

    }

    public static CreateActivityFragment newInstance(int ID){
        CreateActivityFragment fragment = new CreateActivityFragment();

        Bundle args = new Bundle();
        args.putInt("ID", ID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        queryLab = QueryLab.get(getActivity());
        mPeriodsNames = getResources().getStringArray(R.array.periods);
        mPeriodsValues = getResources().getIntArray(R.array.periods_values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_create, container, false);

        etTitle = (EditText) getActivity().findViewById(R.id.etTitle);
        tvPeriod = (TextView) v.findViewById(R.id.tvPeriod);
        tvTime = (TextView) v.findViewById(R.id.tvTime);
        tvURI = (TextView) v.findViewById(R.id.tvURI);

        v.findViewById(R.id.linPeriod).setOnClickListener(this);
        v.findViewById(R.id.linURI).setOnClickListener(this);
        v.findViewById(R.id.linTime).setOnClickListener(this);
        v.findViewById(R.id.linResult).setOnClickListener(this);

        final int ID = getArguments().getInt("ID");

        if (ID != -1){
            query = queryLab.getQueryByID(ID);
            tvURI.setText(query.getURI());

            try {
                ((CreateActivity) getActivity()).getSupportActionBar().setTitle(query.getTitle());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            query = new Query();
            tvURI.setText("Нажмите для настройки");
        }
        etTitle.setText(query.getTitle());
        tvPeriod.setText(getPeriodText((int)query.getPeriod()));
        isAround = query.isAround();
        timeFrom = query.getTimeFrom();
        timeTo = query.getTimeTo();
        setTvTime(isAround, timeFrom, timeTo);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.buttonSave);
        fab.setOnClickListener(this);

        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == DIALOG_PERIOD_FRAGMENT) {
            int period = data.getIntExtra("period", 60000);
            tvPeriod.setText(getPeriodText(period));
            Methods methods = new Methods(getActivity());
            if (period < 300000 && !methods.getWarningIsShow() && methods.getPLevel() != 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Предупреждение!")
                        .setMessage(R.string.error)
                        .setIcon(R.drawable.ic_error)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                methods.setWarningIsShow(true);
                alert.show();
            }
            return;
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DIALOG_TIME_FRAGMENT){
            isAround = data.getBooleanExtra("isAround", true);
            timeFrom = data.getStringExtra("timeFrom");
            timeTo = data.getStringExtra("timeTo");
            setTvTime(isAround, timeFrom, timeTo);
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            String URI = data.getStringExtra("ResultURI");
            if (URI != null){
                tvURI.setText(URI);
            }
        }
    }

    private String getParametrs(String URI){
        String startText = "avito.ru/";
        if (!URI.contains(startText)){
            return "rossiya";
        }
        return URI.substring(URI.indexOf(startText) + startText.length());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch (id){
            case R.id.linPeriod:
                DialogChoicePeriodFragment fragmentDialog = DialogChoicePeriodFragment.newInstance();
                fragmentDialog.setTargetFragment(this, DIALOG_PERIOD_FRAGMENT);
                fragmentDialog.show(fragmentManager, "dialog_choice_periods");
                break;
            case R.id.linTime:
                DialogSetTimeFragment fragmentDialog2 = DialogSetTimeFragment.newInstance(isAround, timeFrom, timeTo);
                fragmentDialog2.setTargetFragment(this, DIALOG_TIME_FRAGMENT);
                fragmentDialog2.show(fragmentManager, "dialog_choice_time");
                break;
            case R.id.linURI:
                Intent i = new Intent(getActivity(), BrowserActivity.class);
                i.putExtra("URI", "https://m.avito.ru/search/" + getParametrs(tvURI.getText().toString()));
                startActivityForResult(i, 0);
                break;
            case R.id.linResult:
                if (tvURI.getText().toString().equals("Нажмите для настройки")){
                    Toast.makeText(getActivity(), "Укажите параметры поиска", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i2 = new Intent(Intent.ACTION_VIEW, Uri.parse(tvURI.getText().toString()));
                startActivity(i2);
                break;
            case R.id.buttonSave:
                if (tvURI.getText().toString().equals("Нажмите для настройки")){
                    Toast.makeText(getActivity(), "Укажите параметры поиска", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etTitle.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Введите название поиска", Toast.LENGTH_SHORT).show();
                    return;
                }

                query.setTitle(etTitle.getText().toString());
                query.setURI(tvURI.getText().toString());
                boolean isPeriodChange = query.setPeriod(getPeriodValue(tvPeriod.getText().toString()));
                query.setAround(isAround);
                query.setTimeFrom(timeFrom);
                query.setTimeTo(timeTo);

                if (query.getId() != -1){
                    queryLab.updateQuery(query);
                    if (isPeriodChange){
                        SearchIntentService.setServiceAlarm(getActivity(), query.getId(), false);
                        SearchIntentService.setServiceAlarm(getActivity(), query.getId(), query.isOn());
                    }
                }else{
                    queryLab.addQuery(query);
                    Query q2 = queryLab.getLast();
                    SearchIntentService.setServiceAlarm(getActivity(), q2.getId(), q2.isOn());
                }

                getActivity().finish();
                break;
        }
    }

    private String getPeriodText(int period){
        int position = Arrays.binarySearch(mPeriodsValues, period);
        return mPeriodsNames[position];
    }

    private int getPeriodValue(String periodText){
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(mPeriodsNames));
        int position = arrayList.indexOf(periodText);
        return mPeriodsValues[position];
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete:
                queryLab.deleteById(query.getId());
                getActivity().finish();
            case android.R.id.home:
                getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTvTime(boolean isAround, String timeFrom, String timeTo){
        if (isAround){
            tvTime.setText("Круглосуточно");
        }else{
            tvTime.setText("С " + timeFrom + " до " + timeTo);
        }
    }
}

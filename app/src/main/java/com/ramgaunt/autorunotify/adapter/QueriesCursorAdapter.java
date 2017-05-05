package com.ramgaunt.autorunotify.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ramgaunt.autorunotify.Methods;
import com.ramgaunt.autorunotify.QueryLab;
import com.ramgaunt.autorunotify.R;
import com.ramgaunt.autorunotify.activity.CreateActivity;
import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Адаптер списка созданных поисков для RecyclerView
 */
public class QueriesCursorAdapter extends RecyclerView.Adapter<QueriesCursorAdapter.ViewHolder> {

    /** Список имен периодов */
    private String[] mPeriodsNames;
    /** Список периодов */
    private int[] mPeriodsValues;
    /** Контекст */
    private Context aContext;
    /** Список созданных поисков */
    private ArrayList<Query> mQuery;
    /** Класс для работы с поисками в базе данных */
    private QueryLab queryLab;
    /** Дополнительные методы (утилита) */
    private Methods mMethods;
    /** Слушатель отображения информации о пустом списке */
    private OnEmptyListener mOnEmptyListener;

    /**
     * Конструктор
     * @param queries  список созданных поисков
     * @param context  контекст
     * @param listener слушатель отображения информации о пустом списке
     */
    public QueriesCursorAdapter(ArrayList<Query> queries, Context context, OnEmptyListener listener) {
        aContext = context;
        mPeriodsNames = context.getResources().getStringArray(R.array.periods);
        mPeriodsValues = context.getResources().getIntArray(R.array.periods_values);
        mQuery = queries;
        mMethods = new Methods(context);
        queryLab = QueryLab.get(context);
        mOnEmptyListener = listener;
        mOnEmptyListener.showEmpty(mQuery.size() == 0);
    }

    /**
     * Заменяет список созданных  поисков на новые
     * @param list новый список поисков
     */
    public void replaceQueries(ArrayList<Query> list) {
        mQuery = list;
        if (mOnEmptyListener != null) {
            mOnEmptyListener.showEmpty(mQuery.size() == 0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(inflater.inflate(R.layout.list_item_query, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        Query query = mQuery.get(position);

        vHolder._id = query.getId();
        vHolder.tvTitle.setText(query.getTitle());
        vHolder.tvPeriod.setText(getPeriodText((int) query.getPeriod()));
        vHolder.tvTime.setText(getTvTime(query));
        vHolder.switchService.setChecked(query.isOn());

        if (Methods.isDeveloper) {
            vHolder.tvURI.setText(query.getURI().substring(8));
            vHolder.tvLastId.setText(query.getLastId());
            vHolder.tvServiceIsRun.setText(SearchIntentService.isServiceAlarmOn(aContext, vHolder._id) ? "true" : "false");
            vHolder.tvLastDate.setText(query.getLastDate());
            vHolder.tvLastShowedId.setText(query.getLastShowedId());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int _id;
        public TextView tvTitle;
        public TextView tvURI;
        public TextView tvTime;
        public TextView tvLastId;
        public TextView tvPeriod;
        public TextView tvServiceIsRun;
        public Switch switchService;
        public TextView tvLastDate;
        public TextView tvLastShowedId;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.textView2);
            tvURI = (TextView) itemView.findViewById(R.id.tvURI);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvLastId = (TextView) itemView.findViewById(R.id.textView8);
            tvPeriod = (TextView) itemView.findViewById(R.id.tvPeriod);
            tvServiceIsRun = (TextView) itemView.findViewById(R.id.textView5);
            switchService = (Switch) itemView.findViewById(R.id.switchService);
            tvLastDate = (TextView) itemView.findViewById(R.id.textView4);
            tvLastShowedId = (TextView) itemView.findViewById(R.id.textView6);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(aContext, CreateActivity.class);
                    i.putExtra("ID", _id);
                    aContext.startActivity(i);
                }
            });

            switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d("CheckedChangeListener", String.valueOf(_id) + " - " + b);
                    Query query = queryLab.getQueryByID(_id);
                    if ((getAdapterPosition() + 1) > mMethods.getAllowedSearches()) {
                        switchService.setChecked(false);
                        SearchIntentService.setServiceAlarm(aContext, _id, false);
                        query.setOn(false);
                    } else {
                        SearchIntentService.setServiceAlarm(aContext, _id, b);
                        query.setOn(b);
                    }
                    queryLab.updateQuery(query);
                }
            });

            switchService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Methods.developerToast(getActivity(), "setOnClickListener");
                    if ((getAdapterPosition() + 1) > mMethods.getAllowedSearches()) {
                        showDialog(DIALOG_BUY);
                    } else {*/
                    // Жесткий сброс поиска
                    if (!((Switch) view).isChecked()) {
                        SearchIntentService.hardAlarmOff(aContext, _id);
                    }
                    //}
                }
            });

            if (Methods.isDeveloper) {
                tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemView.findViewById(R.id.linDeveloperInfo).getVisibility() == View.GONE) {
                            itemView.findViewById(R.id.linDeveloperInfo).setVisibility(View.VISIBLE);
                        } else {
                            itemView.findViewById(R.id.linDeveloperInfo).setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mQuery.size();
    }

    private String getPeriodText(int period) {
        int position = Arrays.binarySearch(mPeriodsValues, period);
        return mPeriodsNames[position];
    }

    private String getTvTime(Query query) {
        if (query.isAround()) {
            return "Круглосуточно";
        } else {
            return "С " + query.getTimeFrom() + " до " + query.getTimeTo();
        }
    }

    /** Слушатель отображения информации о пустом списке */
    public interface OnEmptyListener {

        /** Скрывает информацию о пустом списке и отображаем сам список (и наоборот) */
        void showEmpty(boolean isEmpty);
    }
}

package com.ramgaunt.autorunotify;

import android.content.Context;
import android.database.Cursor;

import com.ramgaunt.autorunotify.database.QueryDatabase;
import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.ArrayList;

public class QueryLab {
    private static QueryLab sQueryLab;
    private ArrayList<Query> mQueries;
    private Context mContext;
    private QueryDatabase queryDatabase;

    private QueryLab(Context context) {
        mContext = context.getApplicationContext();
        queryDatabase = new QueryDatabase(context);
        mQueries = new ArrayList<>();

        try {
            queryDatabase.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static QueryLab get(Context context){
        if (sQueryLab == null){
            sQueryLab = new QueryLab(context);
        }
        return sQueryLab;
    }

    public ArrayList<Query> getAll(){
        Cursor cursor = queryDatabase.getAll();
        mQueries = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            mQueries.add(new Query(cursor));
        }

        return mQueries;
    }

    public ArrayList<Query> getQueries() {
        return mQueries;
    }

    public Query getQueryByID(int id) {
        Cursor cursor = queryDatabase.getByID(id);
        cursor.moveToFirst();

        try {
            return new Query(cursor);
        }catch (Exception e){
            return null;
        }
    }

    public void addQuery(Query q){
        queryDatabase.addQuery(q);
        refresh();
    }

    public void updateQuery(Query q) {
        queryDatabase.updateQuery(q);
    }

    public void deleteById(int ID){
        queryDatabase.deleteById(ID);
        SearchIntentService.hardAlarmOff(mContext, ID);
    }

    public void refresh(){
        mQueries = new ArrayList<>();
        queryDatabase.getAll();
    }

    public void generate(int count){
        for (int i = 0; i < count; i++){
            Query q = new Query();
            q.setTitle("Название " + i);
            q.setURI("https://m.avito.ru/rossiya");
            queryDatabase.addQuery(q);
        }
    }

    public int getCount(){
        return mQueries.size();
    }

    public Query getLast(){
        Cursor a = queryDatabase.getLast();
        a.moveToFirst();

        return new Query(a);
    }

    public void deletePreviously(int Id){

    }
}

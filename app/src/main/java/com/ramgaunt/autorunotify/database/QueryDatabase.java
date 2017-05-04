package com.ramgaunt.autorunotify.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ramgaunt.autorunotify.entity.Query;

public class QueryDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "queryBase.db";

    private static final String TABLE_NAME = "Queries";

    public QueryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY autoincrement, " +
                "title STRING, " +
                "last_id STRING, " +
                "service INTEGER, " +
                "URI String, " +
                "period INTEGER, " +
                "is_around INTEGER, " +
                "time_from STRING, " +
                "time_to STRING, " +
                "last_date STRING, " +
                "last_showed_id STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor getAll(){
        return getReadableDatabase().rawQuery("SELECT * " +
                "FROM " + TABLE_NAME, null);
    }

    private static ContentValues getContentValues(Query query) {
        ContentValues values = new ContentValues();
        values.put("title", query.getTitle());
        values.put("last_id", query.getLastId());
        values.put("service", query.isOn() ? 1 : 0);
        values.put("URI", query.getURI());
        values.put("period", query.getPeriod());
        values.put("is_around", query.isAround() ? 1 : 0);
        values.put("time_from", query.getTimeFrom());
        values.put("time_to", query.getTimeTo());
        values.put("last_date", query.getLastDate());
        values.put("last_showed_id", query.getLastShowedId());
        return values;
    }

    public void addQuery(Query c) {
        ContentValues values = getContentValues(c);
        getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public void updateQuery(Query q){
        ContentValues values = getContentValues(q);
        getWritableDatabase().update(TABLE_NAME, values, "_id = " + q.getId(), null);
    }

    public Cursor getByID(int id) {
        return getWritableDatabase().rawQuery("SELECT * " +
                "FROM " + TABLE_NAME  + " " +
                "WHERE _id = " + id, null);
    }

    public void deleteById(int ID) {
        getWritableDatabase().delete(TABLE_NAME, "_id = " + ID, null);
    }

    public Cursor getLast(){
        return getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY _id DESC LIMIT 1", null);
    }

    public void deletePrev(int Id){
        getWritableDatabase().rawQuery("DELETE FROM " + TABLE_NAME + " WHERE _id <= " + Id, null);
    }
}
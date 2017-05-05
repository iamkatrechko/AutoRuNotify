package com.ramgaunt.autorunotify.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ramgaunt.autorunotify.entity.Query;

/**
 * База данных со списком созданных поисков
 */
public class QueryDatabase extends SQLiteOpenHelper {

    /** Версия базы данных */
    private static final int VERSION = 1;
    /** Имя базы данных */
    private static final String DATABASE_NAME = "queryBase.db";
    /** Имя таблицы со списком поисков */
    private static final String TABLE_NAME = "Queries";

    /**
     * Конструктор
     * @param context контекст
     */
    public QueryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Описание всех полей находится в классе Query
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

    /**
     * Возвращает список всех созданных поисков
     * @return список всех созданных поисков
     */
    public Cursor getAll() {
        return getReadableDatabase().rawQuery("SELECT * " +
                "FROM " + TABLE_NAME, null);
    }

    /**
     * Возвращает контейнер со всеми значениями поиска для записи в базу данных
     * @param query поиск
     * @return контейнер со всеми значениями поиска для записи в базу данных
     */
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

    /**
     * Добавляет новый поиск в базу данных
     * @param c новый поиск
     */
    public void addQuery(Query c) {
        ContentValues values = getContentValues(c);
        getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    /**
     * Обновляет ранее созданный поиск в базе данных
     * @param q поиск
     */
    public void updateQuery(Query q) {
        ContentValues values = getContentValues(q);
        getWritableDatabase().update(TABLE_NAME, values, "_id = " + q.getId(), null);
    }

    /**
     * Возвращает курсор с поиском по его идентификатору
     * @param id идентификатор искомого поиска
     * @return курсор с поиском
     */
    public Cursor getByID(int id) {
        return getWritableDatabase().rawQuery("SELECT * " +
                "FROM " + TABLE_NAME + " " +
                "WHERE _id = " + id, null);
    }

    /**
     * Удаляет поиск из базы данных
     * @param ID идентификатор удаляемого поиска
     */
    public void deleteById(int ID) {
        getWritableDatabase().delete(TABLE_NAME, "_id = " + ID, null);
    }

    /**
     * Возвращает последний поиск в базе данных
     * @return последний поиск в базе данных
     */
    public Cursor getLast() {
        return getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY _id DESC LIMIT 1", null);
    }
}
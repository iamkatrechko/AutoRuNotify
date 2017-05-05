package com.ramgaunt.autorunotify;

import android.content.Context;
import android.database.Cursor;

import com.ramgaunt.autorunotify.database.QueryDatabase;
import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.ArrayList;

/** Класс для работы с поисками в базе данных */
public class QueryLab {

    /** Статический экземпляр текущего класса */
    private static QueryLab sQueryLab;
    /** Список всех поисков */
    private ArrayList<Query> mQueries;
    /** Контекст */
    private Context mContext;
    /** База данных со всеми поисками */
    private QueryDatabase queryDatabase;

    /**
     * Конструктор
     * @param context контекст
     */
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

    /**
     * Возвращает экземпляр класса-синглтона
     * @param context контекст
     * @return экземпляр класса-синглтона
     */
    public static QueryLab get(Context context) {
        if (sQueryLab == null) {
            sQueryLab = new QueryLab(context);
        }
        return sQueryLab;
    }

    /**
     * Возвращает список всех поисков
     * @return список всех поисков
     */
    public ArrayList<Query> getAll() {
        Cursor cursor = queryDatabase.getAll();
        mQueries = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            mQueries.add(new Query(cursor));
        }

        return mQueries;
    }

    /**
     * Возвращает список всех поисков без чтения из базы данных
     * @return список всех поисков без чтения из базы данных
     */
    public ArrayList<Query> getQueries() {
        return mQueries;
    }

    /**
     * Возвращает поиск по его идентификатору
     * @param id идентификатор искомого поиска
     * @return сущность поиска
     */
    public Query getQueryByID(int id) {
        Cursor cursor = queryDatabase.getByID(id);
        cursor.moveToFirst();

        try {
            return new Query(cursor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Добавляет новый поиск в базу данных
     * @param query новый поиск
     */
    public void addQuery(Query query) {
        queryDatabase.addQuery(query);
        refresh();
    }

    /**
     * Обновляет параметры поиска в базе данных
     * @param q обновленный поиск
     */
    public void updateQuery(Query q) {
        queryDatabase.updateQuery(q);
    }

    /**
     * Удаляет поиск по его идентификатору
     * @param ID идентификатор искомого поиска
     */
    public void deleteById(int ID) {
        queryDatabase.deleteById(ID);
        SearchIntentService.hardAlarmOff(mContext, ID);
    }

    /** Обновляет список поисков по базе данных */
    public void refresh() {
        mQueries = new ArrayList<>();
        queryDatabase.getAll();
    }

    /**
     * Возвращает последний поиск из списка
     * @return последний поиск из списка
     */
    public Query getLast() {
        Cursor a = queryDatabase.getLast();
        if (a.getCount() == 0) {
            return null;
        }

        a.moveToFirst();
        return new Query(a);
    }
}

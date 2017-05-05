package com.ramgaunt.autorunotify.entity;

import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;

/**
 * Класс-сущность поиска с определенными настройками
 */
public class Query {

    /** Идентификатор поиска */
    private int mId;
    /** Название поиска */
    private String mTitle;
    /** Ссылка на поиск (его настройки) */
    private String mUri;
    /** Идентификатор последнего найденного объявления этого поиска */
    private String mLastId;
    /** Включен ли поиск */
    private boolean mOn;
    /** Период автоматического запуска поиска */
    private int mPeriod;
    /** Необходимость соблюдать временной режим (например: до полуночи) */
    private boolean mAround;
    /** Начало временного режима */
    private String mTimeFrom;
    /** Конец временного режима */
    private String mTimeTo;
    /** Дата последнего найденного объявления */
    private String mLastDate;
    /** Дата последнего отображенного объявления */
    private String mLastShowedId;

    /** Конструктор */
    public Query() {
        setId(-1);
        setTitle("");
        setOn(true);
        setURI("Не задано");
        setLastId("-1");
        setPeriod(300000);
        setAround(true);
        setTimeFrom("8:00");
        setTimeTo("21:00");
        setLastDate("01.01.2001-00:00");
        setLastShowedId("-1");
    }

    /**
     * Конструктор
     * @param cursor курсор с поиском из базы данных
     */
    public Query(Cursor cursor) {
        setId(cursor.getInt(0));
        setTitle(cursor.getString(1));
        setLastId(cursor.getString(2));
        setOn(cursor.getInt(3) == 1);
        setURI(cursor.getString(4));
        setPeriod(cursor.getInt(5));
        setAround(cursor.getInt(6) == 1);
        setTimeFrom(cursor.getString(7));
        setTimeTo(cursor.getString(8));
        setLastDate(cursor.getString(9));
        setLastShowedId(cursor.getString(10));
    }

    /**
     * Возвращает идентификатор поиска
     * @return идентификатор поиска
     */
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    /**
     * Возвращает название поиска
     * @return название поиска
     */
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Возвращает настройки поиска
     * @return настройки поиска
     */
    public String getURI() {
        return mUri;
    }

    public void setURI(String URI) {
        mUri = URI;
    }

    /**
     * Включен ли поиск
     * @return включен ли поиск
     */
    public boolean isOn() {
        return mOn;
    }

    public void setOn(Boolean isOn) {
        this.mOn = isOn;
    }

    /**
     * Возвращает идентификатор последнего найденного объявления поиска
     * @return идентификатор последнего найденного объявления
     */
    public String getLastId() {
        return mLastId;
    }

    public void setLastId(String mLastId) {
        this.mLastId = mLastId;
    }

    /**
     * Возвращает период автозапуска поиска
     * @return период автозапуска поиска
     */
    public long getPeriod() {
        return mPeriod;
    }

    /**
     * Устанавливает период автопоиска
     * @param period период в мс
     * @return true - если период был изменен
     */
    public boolean setPeriod(int period) {
        if (mPeriod != period) {
            mPeriod = period;
            return true;
        } else {
            mPeriod = period;
            return false;
        }
    }

    /**
     * Возвращает необходимость соблюдения временного режима
     * @return необходимость соблюдения временного режима
     */
    public boolean isAround() {
        return mAround;
    }

    public void setAround(boolean around) {
        mAround = around;
    }

    /**
     * Возвращает начальное время режима
     * @return начальное время режима
     */
    public String getTimeFrom() {
        return mTimeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        mTimeFrom = timeFrom;
    }

    /**
     * Возвращает конечное время режима
     * @return конечное время режима
     */
    public String getTimeTo() {
        return mTimeTo;
    }

    public void setTimeTo(String timeTo) {
        mTimeTo = timeTo;
    }

    /**
     * Возвращает дату последнего найденного объявления
     * @return дата последнего найденного объявления
     */
    public String getLastDate() {
        return mLastDate;
    }

    public void setLastDate(String lastDate) {
        mLastDate = lastDate;
    }

    /**
     * Возвращает дату последнего отображенного объявления
     * @return дата последнего отображенного объявления
     */
    public String getLastShowedId() {
        return mLastShowedId;
    }

    public void setLastShowedId(String lastShowedId) {
        mLastShowedId = lastShowedId;
    }

    /**
     * Проверяет, входит ли текукщее время в настроенное время работы автопоиска
     * @return true - проводить поиск, false - отмена поиска
     */
    public boolean isTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return isTime(hour, minute);
    }

    /**
     * Проверяет, входит ли текукщее время в настроенное время работы автопоиска
     * @return true - проводить поиск, false - отмена поиска
     */
    public boolean isTime(int hourNow, int minuteNow) {
        if (isAround()) {
            return true;
        } else {
            int hourFrom = Integer.valueOf(getTimeFrom().split("\\:")[0]);
            int minuteFrom = Integer.valueOf(getTimeFrom().split("\\:")[1]);
            int hourTo = Integer.valueOf(getTimeTo().split("\\:")[0]);
            int minuteTo = Integer.valueOf(getTimeTo().split("\\:")[1]);

            int tNow = hourNow * 60 + minuteNow;
            int tFrom = hourFrom * 60 + minuteFrom;
            int tTo = hourTo * 60 + minuteTo;

            if (tFrom < tTo) {
                return tNow > tFrom && tNow < tTo;
            } else {
                return tNow < tFrom || tNow > tTo;
            }
        }
    }

    /**
     * Сравнивает 2 даты между собой
     * @param date Вторая дата
     * @return -1 - если 1-ая дата раньше 2-й, 0 - если равны, 1 - если 1-ая дата позже 2-й
     */
    public int compareDate(String date) {
        Calendar date1, date2;

        try {
            date1 = getCalendarFromString(date);
            date2 = getCalendarFromString(getLastDate());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return date2.compareTo(date1);
    }

    /**
     * Возвращает дату в виде экземпляра {@link Calendar}
     * @param date Дата в формате "01.01.2001-00:00"
     * @return Дата в виде {@link Calendar}
     */
    private Calendar getCalendarFromString(String date) {
        String[] sss = date.split("\\-|\\.|\\:");
        int year = Integer.valueOf(sss[0]);
        int month = Integer.valueOf(sss[1]);
        int day = Integer.valueOf(sss[2]);
        int hour = Integer.valueOf(sss[3]);
        int minute = Integer.valueOf(sss[4]);
        Log.d("compareDate", "" + year + "." + month + "." + day + "-" + hour + ":" + minute);
        Calendar dateOne = Calendar.getInstance();
        dateOne.set(year, month, day, hour, minute, 0);

        return dateOne;
    }
}
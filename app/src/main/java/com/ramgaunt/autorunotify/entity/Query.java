package com.ramgaunt.autorunotify.entity;

import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Query {
    private int mId;
    private String mTitle;
    private String mUri;
    private String mLastId;
    private boolean mOn;
    private int mPeriod;
    private boolean mAround;
    private String mTimeFrom;
    private String mTimeTo;
    private String mLastDate;
    private String mLastShowedId;

    public Query(){
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

    public Query(Cursor cursor){
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

    public int getId(){
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getURI() {
        return mUri;
    }

    public void setURI(String URI) {
        mUri = URI;
    }

    public boolean isOn(){
        return mOn;
    }

    public void setOn(Boolean isOn){
        this.mOn = isOn;
    }

    public String getLastId() {
        return mLastId;
    }

    public void setLastId(String mLastId) {
        this.mLastId = mLastId;
    }

    public long getPeriod() {
        return mPeriod;
    }

    /**
     * Устанавливает период автопоиска
     * @param period Период в мс
     * @return true - если период был изменен
     */
    public boolean setPeriod(int period) {
        if (mPeriod != period){
            mPeriod = period;
            return true;
        }else{
            mPeriod = period;
            return false;
        }
    }

    public boolean isAround() {
        return mAround;
    }

    public void setAround(boolean around) {
        mAround = around;
    }

    public String getTimeFrom() {
        return mTimeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        mTimeFrom = timeFrom;
    }

    public String getTimeTo() {
        return mTimeTo;
    }

    public void setTimeTo(String timeTo) {
        mTimeTo = timeTo;
    }

    public String getLastDate() {
        return mLastDate;
    }

    public void setLastDate(String lastDate) {
        mLastDate = lastDate;
    }

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
    public boolean isTime(Calendar calendar){
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return isTime(hour, minute);
    }

    /**
     * Проверяет, входит ли текукщее время в настроенное время работы автопоиска
     * @return true - проводить поиск, false - отмена поиска
     */
    public boolean isTime(int hourNow, int minuteNow) {
        if (isAround()){
            return true;
        }else{
            int hourFrom = Integer.valueOf(getTimeFrom().split("\\:")[0]);
            int minuteFrom = Integer.valueOf(getTimeFrom().split("\\:")[1]);
            int hourTo = Integer.valueOf(getTimeTo().split("\\:")[0]);
            int minuteTo = Integer.valueOf(getTimeTo().split("\\:")[1]);

            int tNow = hourNow * 60 + minuteNow;
            int tFrom = hourFrom * 60 + minuteFrom;
            int tTo = hourTo * 60 + minuteTo;

            if (tFrom < tTo){
                return tNow > tFrom && tNow < tTo;
            }else{
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
    private Calendar getCalendarFromString(String date){
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
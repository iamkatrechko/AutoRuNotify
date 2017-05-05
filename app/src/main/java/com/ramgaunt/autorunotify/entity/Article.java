package com.ramgaunt.autorunotify.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс сущность объявления, которое содержит всю информацию о нем, например:
 * Заголовок, стоимость, дату объявления
 */
public class Article {

    /** Список месяцев для отображения даты обявления */
    private static final String[] MONTHS = {"января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};

    /** Идентификатор объявления */
    private String mId;
    /** Заголовок объявления */
    private String mTitle;
    /** Цена в объявлении */
    private String mPrice;
    /** Дата объявления */
    private String mDate;
    /** Признак того, что объявление является оплаченным и находится на вершине списка */
    private boolean mVip;
    /** URL главного изображение объявления */
    private String mImgUrl;
    /** Флаг того, необходимо ли ввести капчу */
    private boolean mCaptcha;

    /** Паттерн для поиска идентификатора */
    private static final Pattern patternId = Pattern.compile("\"saleId\":\"(.*?)\"");
    /** Паттерн для поиска VIP-признака */
    private static final Pattern patternVip = Pattern.compile("\"isTop\":(.*?),");
    /** Паттерн для поиска заголовка */
    private static final Pattern patternTitle = Pattern.compile("\"label\":\"(.*?)\"");
    /** Паттерн для поиска цены */
    private static final Pattern patternPrice = Pattern.compile("\"value\":(.*?),");
    /** Паттерн для поиска даты */
    private static final Pattern patternDate = Pattern.compile("\"freshDate\":\"(.*?)\"");
    /** Паттерн для поиска URL изображения */
    private static final Pattern patternImgUrl = Pattern.compile("\"cover\":\"(.*?)\"");

    /**
     * Конструктор, использующийся для передачи флага необходимости ввода капчи
     * @param captcha флаг необходимости ввода капчи
     */
    public Article(boolean captcha) {
        mCaptcha = captcha;
    }

    /**
     * Конструктор
     * @param HTMLtext исходный HTML-текст одного объявления
     */
    public Article(String HTMLtext) {
        setId(parseID(HTMLtext));
        setTitle(parseTitle(HTMLtext));
        setPrice(parsePrice(HTMLtext));
        setDate(parseDate(HTMLtext));
        setVip(parseVIP(HTMLtext));
        setImgUrl(parseImgUrl(HTMLtext));
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPrice() {
        return mPrice + " руб.";
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public boolean isVIP() {
        return mVip;
    }

    public void setVip(boolean vip) {
        mVip = vip;
    }

    public String getImgUrl() {
        if (mImgUrl != null) {
            return mImgUrl;
        } else {
            return "";
        }
    }

    public boolean isCaptcha() {
        return mCaptcha;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
    }

    private String parseID(String HTMLtext) {
        Matcher m = patternId.matcher(HTMLtext);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    private String parseTitle(String HTMLtext) {
        Matcher m = patternTitle.matcher(HTMLtext);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    private String parsePrice(String HTMLtext) {
        Matcher m = patternPrice.matcher(HTMLtext);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    private String parseDate(String HTMLtext) {
        Matcher m = patternDate.matcher(HTMLtext);
        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

    private boolean parseVIP(String HTMLtext) {
        Matcher m = patternVip.matcher(HTMLtext);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        } else {
            return false;
        }
    }

    private String parseImgUrl(String HTMLtext) {
        Matcher m = patternImgUrl.matcher(HTMLtext);
        if (m.find()) {
            return "https:" + m.group(1);
        } else {
            return null;
        }
    }

    /**
     * Преобразует дату из {@link Calendar} в строку
     * @param calendar Экземпляр даты
     * @return Дата в формате "01.01.2001-00:00"
     */
    public String getDateCalendar(Calendar calendar) {
        int hour, minute, day, month, year;

        try {
            String articleDate = getDate();
            if (articleDate.contains("Сегодня")) {
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH) + 1;
            } else if (articleDate.contains("Вчера")) {
                calendar.roll(Calendar.DAY_OF_MONTH, -1);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH) + 1;
            } else {
                String date = articleDate.substring(0, articleDate.indexOf(","));
                String[] date2 = date.split("\\s+");
                day = Integer.valueOf(date2[0]);
                ArrayList<String> m = new ArrayList<>(Arrays.asList(MONTHS));
                month = m.indexOf(date2[1]) + 1;
            }

            hour = Integer.valueOf(articleDate.substring(articleDate.indexOf(",") + 2).split("\\:")[0]);
            minute = Integer.valueOf(articleDate.substring(articleDate.indexOf(",")).split("\\:")[1]);

            year = calendar.get(Calendar.YEAR);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return "" + day + "." + month + "." + year + "-" + hour + ":" + minute;
    }
}

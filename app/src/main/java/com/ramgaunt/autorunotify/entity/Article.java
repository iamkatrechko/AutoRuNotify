package com.ramgaunt.autorunotify.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Article {
    private String[] months = {"января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};

    private String mId;
    private String mTitle;
    private String mPrice;
    private String mDate;
    private Integer mVip;
    private String mImgUrl;
    private int mUnreadCount;

    private final String PARSE_ID_START = "data-item-id=\"";
    private final String PARSE_ID_END = "\"";

    private final String PARSE_TITLE_START = "<span class=\"header-text\">";
    private final String PARSE_TITLE_END = "</span>";

    private final String PARSE_PRICE_START = "<div class=\"item-price \">";
    private final String PARSE_PRICE_END =  "</div>";

    private final String PARSE_DATE_START = "<div class=\"info-date info-text\">";
    private final String PARSE_DATE_END = "</div>";

    private final String PARSE_VIP_START = "data-item-premium=\"";
    private final String PARSE_VIP_END = "\"";

    private final String PARSE_IMG_URL_START = "style=\"background-image: url(";
    private final String PARSE_IMG_URL_END = ");\"></span>";

    public Article(String HTMLtext){
        mUnreadCount = 1;
        setId(parseID(HTMLtext));
        setTitle(parseTitle(HTMLtext));
        setPrice(parsePrice(HTMLtext));
        setDate(parseDate(HTMLtext));
        setVip(parseVIP(HTMLtext));
        setImgUrl(parseImgUrl(HTMLtext));
    }

    public String getId(){
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPrice(){
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getDate(){
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public Integer getVIP(){
        return mVip;
    }

    public void setVip(Integer vip) {
        mVip = vip;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
    }

    /**
     * Возвращает число непрочитанных объявлений
     * @return Число непрочитанных объявлений
     */
    public int getUnreadCount() {
        return mUnreadCount;
    }

    /**
     * Возвращает количество непрочитанных объявлений в конечном виде
     * @return Строка с информацией о кол-ве непрочитанных объявлений
     */
    public String getUnreadCountString() {
        String newCount;
        if (getUnreadCount() > 19) {
            newCount = " (новых: 20+)";
        } else if (getUnreadCount() == 0) {
            newCount = "";
        } else {
            newCount = " (новых: " + getUnreadCount() + ")";
        }

        return newCount;
    }

    public void setUnreadCount(int unreadCount) {
        mUnreadCount = unreadCount;
    }

    private String parseID(String HTMLtext){
        return parse(HTMLtext, PARSE_ID_START, PARSE_ID_END);
    }

    private String parseTitle(String HTMLtext){
        return parse(HTMLtext, PARSE_TITLE_START, PARSE_TITLE_END);
    }

    private String parsePrice(String HTMLtext){
        return parse(HTMLtext, PARSE_PRICE_START, PARSE_PRICE_END);
    }

    private String parseDate(String HTMLtext){
        return parse(HTMLtext, PARSE_DATE_START, PARSE_DATE_END);
    }

    private Integer parseVIP(String HTMLtext){
        String res = parse(HTMLtext, PARSE_VIP_START, PARSE_VIP_END);
        if (res.equals("Не указано")){
            return -1;
        }else{
            return Integer.valueOf(res);
        }
    }

    private String parseImgUrl(String HTMLtext){
        return "https:" + parse(HTMLtext, PARSE_IMG_URL_START, PARSE_IMG_URL_END);
    }

    private String parse(String HTMLtext, String start, String end){
        if (!HTMLtext.contains(start)){
            return "Не указано";
        }

        int iStart = HTMLtext.indexOf(start) + start.length();
        int iEnd = HTMLtext.indexOf(end, iStart);

        String result = "";
        result = HTMLtext.substring(iStart, iEnd);
        result = result.replaceAll("&[a-z0-9A-Z]+;", " ");                                          //Убираем escape-последовательности
        result = result.replaceAll("<span class=\"nobr\">", "");
        result = result.trim();                                                                     //Убираем пробелы по краям

        return result;
    }

    /**
     * Инкрементирует количество новых объявлений для данного экземпляра
     */
    public void incrementUnreadCount(){
        mUnreadCount++;
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
                ArrayList<String> m = new ArrayList<>(Arrays.asList(months));
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

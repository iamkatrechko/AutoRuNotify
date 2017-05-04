package com.ramgaunt.autorunotify;

import android.content.Context;

import com.ramgaunt.autorunotify.entity.Article;
import com.ramgaunt.autorunotify.entity.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ArticleSearcher {
    private DownloadManager mDownloadManager;

    public ArticleSearcher(){
        mDownloadManager = new DownloadManager();
    }

    public Article checkUpdate(Context context, Query query) {
        String result = "";
        try {
            result = mDownloadManager.getUrlString(query.getURI());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getLast(result, query);
    }

    private Article getLast(String result, Query query){
        Article articleNew = null;

        //Если есть результаты
        if (result.contains("<article")) {
            int indexEnd = 0;
            int indexStart = result.indexOf("<article", indexEnd);
            while (indexStart != -1) {
                indexEnd = result.indexOf("</article>", indexStart) + 10;
                String articleHTML = result.substring(indexStart, indexEnd);

                if (!articleHTML.contains("data-item-premium=")){
                    //Log.d("getLast", "Реклама Avito Контекст");
                    indexStart = result.indexOf("<article", indexEnd);
                    continue;
                }

                Article article = new Article(articleHTML);
                if (article.getVIP() == 0) {
                    //Если это первый поиск
                    if (query.getLastId().equals("-1")) {
                        article.setUnreadCount(0);
                        return article;
                    }

                    //Если новых объявлений нет (больше нет)
                    if (article.getId().equals(query.getLastId())){
                        return articleNew;
                    }else {
                        if (articleNew == null){
                            //Сравниваем дату найденного объявления с датой последнего найденного объявления
                            //(в случае удаление последнего объявления)
                            int compareResult = query.compareDate(article.getDateCalendar(Calendar.getInstance()));
                            if (compareResult == 1) {
                                //Log.d("ArticleSearcher", "Дата найденного объявления раньше даты последнего объявления");
                                return null;
                            }else{
                                articleNew = new Article(articleHTML);
                            }
                        }else{
                            articleNew.incrementUnreadCount();
                        }
                    }
                }else{
                    //Log.d("getLast", "" + article.getId() + " - isVip");
                }

                indexStart = result.indexOf("<article", indexEnd);
            }
            return articleNew;
        }else{
            return null;
        }
    }

    public List<String> parse(String HTMLtext) {
        List<String> result = new ArrayList<>();


        //String start = "listing-item listing-item_view_columns";
        String start = "class=\"commercial-listing-item\"";
        String end = "</div></div></div>";

        int indexEnd = 0;
        int indexStart = HTMLtext.indexOf(start, indexEnd);
        while (indexStart != -1) {
            indexEnd = HTMLtext.indexOf(end, indexStart) + end.length();
            String articleHTML = HTMLtext.substring(indexStart, indexEnd);

            result.add(articleHTML);
            indexStart = HTMLtext.indexOf(start, indexEnd);
        }

        return result;
    }
}
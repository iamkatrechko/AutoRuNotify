package com.ramgaunt.autorunotify;

import android.content.Context;
import android.util.Log;

import com.ramgaunt.autorunotify.entity.Article;
import com.ramgaunt.autorunotify.entity.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для нахождения новых объявлений
 */
public class ArticleSearcher {

    private static final String TAG = ArticleSearcher.class.getSimpleName();

    private DownloadManager mDownloadManager;

    public ArticleSearcher() {
        mDownloadManager = new DownloadManager();
    }

    public Article checkUpdate(Context context, Query query) {
        String result = "";
        try {
            result = mDownloadManager.getUrlString(context, query.getURI());
            //result = mDownloadManager.getUrlString(context, "https://m.auto.ru/cars/lexus/gx/all/?image=true&sort_offers=cr_date-DESC&page_num_offers=1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getLast(result, query);
    }

    private Article getLast(String result, Query query) {
        Article articleNew = null;

        //Если есть результаты
        /*if (result.contains("<article")) {
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
                if (article.isVip() == 0) {
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
        }*/

        if (result.contains("Ой!")) {
            return new Article(true);
        }

        for (Article article : getAllArticles(result)) {
            if (article.isVip()) {
                // Если объявление оплаченное, не рассматриваем его
                continue;
            }

            /*if (article.getId().equals(query.getLastId())) {
                // Если первое отображаемое объявление равно ранее сохраненному -> новых нет
                return null;
            }*/

            return article;
        }

        Log.d(TAG, result);
        Log.d(TAG, "Пустой результат");
        return null;
    }

    /**
     * Возвращает список всех скачанных объявлений
     * @param HTMLText исходная HTML-строка страницы со всеми объявлениями
     * @return список всех скачанных объявлений
     */
    private List<Article> getAllArticles(String HTMLText) {
        List<Article> result = new ArrayList<>();

        String start = "{\"isFetching\":false,\"isFavoriteProcessing\":false";
        String end = "}}},";

        int indexEnd = 0;
        int indexStart = HTMLText.indexOf(start, indexEnd);
        while (indexStart != -1) {
            indexEnd = HTMLText.indexOf(end, indexStart) + end.length();
            String articleHTML = HTMLText.substring(indexStart, indexEnd);

            result.add(new Article(articleHTML));
            indexStart = HTMLText.indexOf(start, indexEnd);
        }

        return result;
    }
}
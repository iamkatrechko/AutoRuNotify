package com.ramgaunt.autorunotify.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.ramgaunt.autorunotify.ArticleSearcher;
import com.ramgaunt.autorunotify.NotificationsManager;
import com.ramgaunt.autorunotify.QueryLab;
import com.ramgaunt.autorunotify.entity.Article;
import com.ramgaunt.autorunotify.entity.Query;

import java.util.Calendar;

public class SearchIntentService extends IntentService {

    /** Константа для логирования */
    private static final String TAG = "SearchIntentService";
    /** Константа для установки идентификатора последнего уведомления */
    public static final String ACTION_SET_LAST_ID = "setLastId";
    /** Константа для открытия объявление в браузере */
    public static final String ACTION_OPEN_IN_BROWSER = "openInBrowser";

    /** Время появления нового уведомления */
    private Calendar calendar;
    /** Класс для работы с поисками в базе данных */
    private QueryLab queryLab;
    /** Класс для нахождения новых объявлений */
    private ArticleSearcher mArticleSearcher;
    /** Менеджер сохраненные на устройстве свойст и данных */
    private SharedPreferences prefs;

    /** Менеджер уведомлений */
    private NotificationsManager mNotificationsManager;

    public SearchIntentService() {
        super("SearchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Данный метод отрабатывает, когда "звенит" будильник, который просит сделать поиск новых объявлений
        // Здесь происходит запрос на скачку всех объявлений конкретного поиска из базы данных
        // Далее скачанный объявления проверяются на наличие новых и, в случая нахождения новых, отображает уведомление

        if (intent != null) {
            mNotificationsManager = new NotificationsManager(this);
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mArticleSearcher = new ArticleSearcher();
            queryLab = QueryLab.get(this);
            calendar = Calendar.getInstance();

            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_SET_LAST_ID)) {
                    setLastId(intent);
                    return;
                }
                if (action.equals(ACTION_OPEN_IN_BROWSER)) {
                    setLastId(intent);
                    String queryURI = intent.getStringExtra("queryURI");
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(queryURI));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    return;
                }
            }

            int queryID = intent.getIntExtra("ID", -1);
            if (queryID == -1) return;
            Query query = queryLab.getQueryByID(queryID);

            setServiceAlarm(this, queryID, false);
            setServiceAlarm(this, queryID, query.isOn());

            if (checkLimits(query)) {
                return;
            }

            //Log.d(TAG, "Новый поиск - " + query.getURI());
            Article article = mArticleSearcher.checkUpdate(this, query);

            if (article != null) {
                Log.d(TAG, "Появилось новое объявление");

                if (article.isCaptcha()) {
                    // Если необходимо ввести капчу
                    mNotificationsManager.showCaptchaNotification();
                    return;
                }
                /*if (query.getLastShowedId().equals(article.getId())) {
                    // Если данное объявление уже отображалось, либо уже находится на экране
                    Log.d(TAG, "Данное объявление уже активно на экране");
                }else{
                    mNotificationsManager.showNotification(query, article);
                }*/

                mNotificationsManager.showNotification(query, article);
            } else {
                Log.d(TAG, "Новых объявлений нет");
            }
        }
    }

    /**
     * Работает ли устройство через Wi-Fi
     * @return true - да, false - нет
     */
    private boolean isNetworkIsWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    /**
     * Подключено ли устройство к интернету
     * @return true - да, false - нет
     */
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Устанавливает отложенный запуск службы, или отключает его
     * @param context Контекст
     * @param queryId ID запроса
     * @param isOn    Включена/выключена служба
     */
    public static void setServiceAlarm(Context context, int queryId, boolean isOn) {
        Intent i = new Intent(context, SearchIntentService.class);
        i.putExtra("ID", queryId);

        PendingIntent pi;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            if (!isServiceAlarmOn(context, queryId)) {
                Query query = QueryLab.get(context).getQueryByID(queryId);

                pi = PendingIntent.getService(context, queryId, i, 0);

                Log.d(TAG, "Запуск службы для ID: " + queryId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + query.getPeriod(), pi);
                } else {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + query.getPeriod(), pi);
                }
            }
        } else {
            if (isServiceAlarmOn(context, queryId)) {
                if (alarmManager != null) {
                    Log.d(TAG, "Остановка службы для ID: " + queryId);
                    pi = PendingIntent.getService(context, queryId, i, 0);
                    alarmManager.cancel(pi);
                    pi.cancel();
                }
            }
        }
    }

    /**
     * Отключает постоянную проверку для определенного поиска
     * @param context контекст
     * @param queryId идентификатор отключаемого поиска
     */
    public static void hardAlarmOff(Context context, int queryId) {
        Intent i = new Intent(context, SearchIntentService.class);
        i.putExtra("ID", queryId);

        PendingIntent pi = PendingIntent.getService(context, queryId, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "Жесткая остановка службы для ID: " + queryId);
        alarmManager.cancel(pi);
        pi.cancel();
    }

    /**
     * Проверяет, включен ли отложенный запуск службы
     * @param context Контекст
     * @param Id      ID запроса
     * @return Включен ли запуск
     */
    public static boolean isServiceAlarmOn(Context context, int Id) {
        Intent i = new Intent(context, SearchIntentService.class);
        PendingIntent pi = PendingIntent
                .getService(context, Id, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    /**
     * Сохраняет последнее найденное объявление, чтобы не отображать его в следующий раз
     * @param intent интент, присланный уведомлением после нажатия на кнопка "Отметить как "прочитанное""
     */
    private void setLastId(Intent intent) {
        int queryId = intent.getIntExtra("queryId", -1);
        String articleId = intent.getStringExtra("articleId");
        String articleLastDate = intent.getStringExtra("lastDate");

        Query query = queryLab.getQueryByID(queryId);
        if (query == null) {
            return;
        }
        query.setLastId(articleId);
        query.setLastDate(articleLastDate);
        queryLab.updateQuery(query);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(queryId);
    }

    /**
     * Проверяет возможность нахождение объявлений через интернет
     * @param query запрос на поиск объявлений
     * @return {@code true} - все нормально, можно искать, {@code false} - поиск следует остановить
     */
    private boolean checkLimits(Query query) {
        if (!isNetworkAvailableAndConnected()) {
            //Log.d(TAG, "Отмена поиска: нет сети");
            return true;
        }

        boolean onlyWifi = prefs.getBoolean("only_wifi", false);
        if (onlyWifi && !isNetworkIsWifi()) {
            //Log.d(TAG, "Отмена поиска: нет подключения к Wi-Fi");
            return true;
        }

        if (!query.isTime(calendar)) {
            //Log.d(TAG, "Отмена поиска: текущее время не входит во время работы данного поиска");
            return true;
        }
        return false;
    }
}

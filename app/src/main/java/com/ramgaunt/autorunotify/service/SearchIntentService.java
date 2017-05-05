package com.ramgaunt.autorunotify.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.ramgaunt.autorunotify.ArticleSearcher;
import com.ramgaunt.autorunotify.DownloadManager;
import com.ramgaunt.autorunotify.NotificationsManager;
import com.ramgaunt.autorunotify.QueryLab;
import com.ramgaunt.autorunotify.R;
import com.ramgaunt.autorunotify.entity.Article;
import com.ramgaunt.autorunotify.entity.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class SearchIntentService extends IntentService {

    final static String TAG = "SearchIntentService";
    public static final String ACTION_SET_LAST_ID = "setLastId";
    public static final String ACTION_OPEN_IN_BROWSER = "openInBrowser";

    private Calendar calendar;
    private QueryLab queryLab;
    private ArticleSearcher mArticleSearcher;

    private SharedPreferences prefs;

    private NotificationsManager mNotificationsManager;

    public SearchIntentService() {
        super("SearchIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, "onHandleIntent----------------------------------------");

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
                if (action.equals(ACTION_OPEN_IN_BROWSER)){
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

            /*if (*//*queryID == 1 && *//*Methods.isDeveloper) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);

                try {
                    String text = readNote(String.valueOf(queryID));
                    saveNote(String.valueOf(queryID), text + "\n" + hour + ":" + minute + ":" + seconds);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                checkLimitsByLogFile(query);
                *//*return;*//*
            }*/

            if (checkLimits(query)) return;

            //Log.d(TAG, "Новый поиск - " + query.getURI());
            Article article = mArticleSearcher.checkUpdate(this, query);

            if (article != null) {
                //Log.d(TAG, "Появилось новое объявление");

                if (article.isCaptcha()) {
                    mNotificationsManager.showCaptchaNotification();
                    return;
                }
                /*if (query.getLastShowedId().equals(article.getId())){
                    //Log.d(TAG, "Данное объявление уже активно на экране");
                }else{
                    mNotificationsManager.showNotification(query, article);
                }*/

                mNotificationsManager.showNotification(query, article);
            } else {
                //Log.d(TAG, "Новых объявлений нет");
            }
        }
    }

    /**
     * Работает ли устройство через Wi-Fi
     * @return true - да, false - нет
     */
    private boolean isNetworkIsWifi(){
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
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    /**
     * Устанавливает отложенный запуск службы, или отключает ее
     * @param context Контекст
     * @param queryId ID запроса
     * @param isOn Включена/выключена служба
     */
    public static void setServiceAlarm(Context context, int queryId, boolean isOn){
        Intent i = new Intent(context, SearchIntentService.class);
        i.putExtra("ID", queryId);

        PendingIntent pi;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            if (!isServiceAlarmOn(context, queryId)){
                Query query = QueryLab.get(context).getQueryByID(queryId);

                pi = PendingIntent.getService(context, queryId, i, 0);

                Log.d(TAG, "Запуск службы для ID: " + queryId);
                /*alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime(), query.getPeriod(), pi);*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + query.getPeriod(), pi);
                }else{
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + query.getPeriod(), pi);
                }
            }
        } else {
            if (isServiceAlarmOn(context, queryId)){
                if (alarmManager != null){
                    Log.d(TAG, "Остановка службы для ID: " + queryId);
                    pi = PendingIntent.getService(context, queryId, i, 0);
                    alarmManager.cancel(pi);
                    pi.cancel();
                }
            }
        }
    }

    public static void hardAlarmOff(Context context, int queryId){
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
     * @param Id ID запроса
     * @return Включен ли запуск
     */
    public static boolean isServiceAlarmOn(Context context, int Id) {
        Intent i = new Intent(context, SearchIntentService.class);
        PendingIntent pi = PendingIntent
                .getService(context, Id, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void setLastId(Intent intent){
        int queryId = intent.getIntExtra("queryId", -1);
        String articleId = intent.getStringExtra("articleId");
        String articleLastDate = intent.getStringExtra("lastDate");

        Query query = queryLab.getQueryByID(queryId);
        if (query == null){
            return;
        }
        query.setLastId(articleId);
        query.setLastDate(articleLastDate);
        queryLab.updateQuery(query);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(queryId);
        //Log.d(TAG, "Установлено последнее время и ID: " + articleId + " для " + queryId);
    }

    private boolean checkLimits(Query query){
        if (!isNetworkAvailableAndConnected()) {
            //Log.d(TAG, "Отмена поиска: нет сети");
            return true;
        }

        boolean onlyWifi = prefs.getBoolean("only_wifi", false);
        if (onlyWifi && !isNetworkIsWifi()){
            //Log.d(TAG, "Отмена поиска: нет подключения к Wi-Fi");
            return true;
        }
        if (!query.isTime(calendar)) {
            //Log.d(TAG, "Отмена поиска: текущее время не входит во время работы данного поиска");
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Удалить /////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void showTestNotification(){
        mArticleSearcher = new ArticleSearcher();
        DownloadManager downloadManager = new DownloadManager();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Bitmap bitmap1 = downloadManager.getUrlBitmap("https://38.img.avito.st/140x105/2968702838.jpg");
        Bitmap bitmap2 = downloadManager.getUrlBitmap("https://44.img.avito.st/140x105/2974080744.jpg");

        Notification notification1 = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_result)
                .setLargeIcon(bitmap1)
                .setColor(Color.parseColor("#1f89de"))
                .setContentTitle("Продам Nexus 5x на 32gb (в идеале)")
                .setContentText("12 500 руб., сегодня 6:29")
                .setSubText("Nexus 5x (новых: 4)")
                .addAction(new android.support.v4.app.NotificationCompat.Action(0, "Отметить как \"Прочитано\"", null))
                .build();

        Notification notification2 = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_result)
                .setLargeIcon(bitmap2)
                .setColor(Color.parseColor("#1f89de"))
                .setContentTitle("3-к квартира, 60 м², 9/16 эт.")
                .setContentText("11 600 000 руб., сегодня 6:13")
                .setSubText("Квартира на ВДНХ (новых: 13)")
                .addAction(new android.support.v4.app.NotificationCompat.Action(0, "Отметить как \"Прочитано\"", null))
                .build();

        notificationManager.notify(1, notification1);
        notificationManager.notify(2, notification2);
    }

    private boolean checkLimitsByLogFile(Query query){
        if (!isNetworkAvailableAndConnected()) {
            try {
                String text = readNote("");
                saveNote("", text + "Отмена поиска: нет сети");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        boolean onlyWifi = prefs.getBoolean("only_wifi", false);
        if (onlyWifi && !isNetworkIsWifi()){
            try {
                String text = readNote("");
                saveNote("", text + "Отмена поиска: нет подключения к Wi-Fi");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (!query.isTime(calendar)) {
            try {
                String text = readNote("");
                saveNote("", text + "Отмена поиска: текущее время не входит во время работы данного поиска");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void saveNote (String fileName, String text) throws IOException {
        String fN = "avito_log_file.txt";

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/"+ "");
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fN);

        try {
            FileWriter f = new FileWriter(sdFile);
            f.write(text);
            // закрываем поток
            f.close();
            Log.d(TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readNote (String fileName) throws IOException {

        String fN = "avito_log_file.txt";

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return "Errorrr";
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/"+ "");
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fN);

        try {
            FileReader f = new FileReader(sdFile);
            BufferedReader br = new BufferedReader(f);
            String ss = "";
            String str;
            while ((str = br.readLine()) != null) {
                ss = ss + (!ss.equals("") ? "\n" : "") + str;
            }
            f.close();
            return ss;
            // закрываем поток
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Errorrrrrrrrr";
    }
}

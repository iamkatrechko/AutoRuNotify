package com.ramgaunt.autorunotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.ramgaunt.autorunotify.activity.EnterCaptchaActivity;
import com.ramgaunt.autorunotify.entity.Article;
import com.ramgaunt.autorunotify.entity.Query;
import com.ramgaunt.autorunotify.service.SearchIntentService;

import java.util.Calendar;

/**
 * Менеджер уведомлений
 */
public class NotificationsManager {

    private Context mContext;
    private SharedPreferences mPreferenceManager;
    private QueryLab queryLab;

    public NotificationsManager(Context context) {
        mContext = context;
        mPreferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        queryLab = QueryLab.get(context);
    }

    /**
     * Выводит уведомление
     * @param query Запрос
     * @param article Объявление
     */
    public void showNotification(Query query, Article article) {
        Calendar calendar = Calendar.getInstance();

        DownloadManager downloadManager = new DownloadManager();
        Bitmap bitmap = downloadManager.getUrlBitmap(mContext, article.getImgUrl());

        String ringtone = mPreferenceManager.getString("notifications_new_message_ringtone", "");
        boolean vibration = mPreferenceManager.getBoolean("notifications_new_message", true);

        int defaults;
        if (vibration) {
            defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        } else {
            defaults = Notification.DEFAULT_LIGHTS;
        }
        if (ringtone.equals("")) {
            defaults += Notification.DEFAULT_SOUND;
        }

        //Интент запуска браузера при нажатии на уведомление
        Intent intentBrowser = new Intent(mContext, SearchIntentService.class);
        intentBrowser.setAction(SearchIntentService.ACTION_OPEN_IN_BROWSER);
        intentBrowser.putExtra("queryId", query.getId());
        intentBrowser.putExtra("articleId", article.getId());
        intentBrowser.putExtra("lastDate", article.getDateCalendar(calendar));
        intentBrowser.putExtra("queryURI", query.getURI());
        PendingIntent piBrowser = PendingIntent.getService(mContext, query.getId(), intentBrowser, PendingIntent.FLAG_UPDATE_CURRENT);

        //Интент записи последнего найденного объявление
        Intent intentSetLast = new Intent(mContext, SearchIntentService.class);
        int queryId = query.getId();
        String articleId = article.getId();
        intentSetLast.setAction(SearchIntentService.ACTION_SET_LAST_ID);
        intentSetLast.putExtra("queryId", queryId);
        intentSetLast.putExtra("articleId", articleId);
        intentSetLast.putExtra("lastDate", article.getDateCalendar(calendar));
        PendingIntent piSetLast = PendingIntent.getService(mContext, queryId, intentSetLast, PendingIntent.FLAG_UPDATE_CURRENT);

        //Отметить как прочитанное при нажатии (также открыть браузер)
        Notification notification = new NotificationCompat.Builder(mContext)
                .setTicker("Появилось новое объявление")
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setContentTitle(article.getTitle())
                .setContentText(article.getPrice() + ", " + article.getDate())
                .setSubText(query.getTitle())
                .setContentIntent(piBrowser)
                .setSound(Uri.parse(ringtone))
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(defaults)
                .setAutoCancel(true)
                .setDeleteIntent(piSetLast)
                .addAction(new android.support.v4.app.NotificationCompat.Action(0, "Отметить как \"Прочитано\"", piSetLast))
                .build();

        query.setLastShowedId(articleId);
        queryLab.updateQuery(query);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(query.getId(), notification);
    }

    /** Отображает уведомление о необходимости ввода капчи */
    public void showCaptchaNotification(String URL) {
        String ringtone = mPreferenceManager.getString("notifications_new_message_ringtone", "");
        boolean vibration = mPreferenceManager.getBoolean("notifications_new_message", true);

        int defaults;
        if (vibration) {
            defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
        } else {
            defaults = Notification.DEFAULT_LIGHTS;
        }
        if (ringtone.equals("")) {
            defaults += Notification.DEFAULT_SOUND;
        }

        Intent intentCaptcha = new Intent(mContext, EnterCaptchaActivity.class);
        intentCaptcha.putExtra("URL", URL);
        intentCaptcha.setAction("null");
        PendingIntent piBrowser = PendingIntent.getActivity(mContext, 777, intentCaptcha, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setTicker(mContext.getString(R.string.captcha))
                .setSmallIcon(R.drawable.ic_notification)
                //.setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setContentTitle(mContext.getString(R.string.captcha_find))
                .setContentText(mContext.getString(R.string.captcha_next))
                .setSubText(mContext.getString(R.string.captcha))
                .setContentIntent(piBrowser)
                //.setSound(Uri.parse(ringtone))
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(defaults)
                .setAutoCancel(true)
                //.setDeleteIntent(piSetLast)
                //.addAction(new android.support.v4.app.NotificationCompat.Action(0, mContext.getString(R.string.captcha_enter), piBrowser))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(777, notification);
    }

    /** Отображает тестовое уведомление с поготовленными данными */
    private void showTestNotification(){
        DownloadManager downloadManager = new DownloadManager();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        Bitmap bitmap1 = downloadManager.getUrlBitmap(mContext, "https://38.img.avito.st/140x105/2968702838.jpg");
        Bitmap bitmap2 = downloadManager.getUrlBitmap(mContext, "https://44.img.avito.st/140x105/2974080744.jpg");

        Notification notification1 = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_result)
                .setLargeIcon(bitmap1)
                .setColor(Color.parseColor("#1f89de"))
                .setContentTitle("Продам Nexus 5x на 32gb (в идеале)")
                .setContentText("12 500 руб., сегодня 6:29")
                .setSubText("Nexus 5x (новых: 4)")
                .addAction(new android.support.v4.app.NotificationCompat.Action(0, "Отметить как \"Прочитано\"", null))
                .build();

        Notification notification2 = new NotificationCompat.Builder(mContext)
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
}

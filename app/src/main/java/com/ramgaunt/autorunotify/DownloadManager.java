package com.ramgaunt.autorunotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Помощник для загрузки данных из интернета
 */
public class DownloadManager {

    /**
     * Загружает массив байтов по ссылке
     * @param context контекст
     * @param urlSpec ссылка
     * @return массив байтов
     * @throws IOException ошибка
     */
    public byte[] getUrlBytes(Context context, String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (context != null) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //connection.setRequestProperty("User-Agent", defaultSharedPreferences.getString("useragent", ""));
            //connection.setRequestProperty("Cookie", defaultSharedPreferences.getString("cookie", ""));
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Загружает интернет-ресурс в виде строки
     * @param context контекст
     * @param urlSpec ссылка
     * @return интернет-ресурс в виде строки
     * @throws IOException ошибка
     */
    public String getUrlString(Context context, String urlSpec) throws IOException {
        return new String(getUrlBytes(context, urlSpec));
    }

    /**
     * Загружает изображение из интернета
     * @param context контекст
     * @param URI     ссылка
     * @return изображение из интернета
     */
    public Bitmap getUrlBitmap(Context context, String URI) {
        byte[] bitmapBytes = new byte[0];
        try {
            bitmapBytes = getUrlBytes(context, URI);
        } catch (IOException e) {
            Log.d("ArticleSearcher", "Ошибка загрузки изображения");
        }

        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }
}

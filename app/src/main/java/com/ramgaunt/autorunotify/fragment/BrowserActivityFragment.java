package com.ramgaunt.autorunotify.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ramgaunt.autorunotify.R;

import java.util.ArrayList;

/**
 * Фрагмент для отображения окна браузера с настройками поиска
 */
public class BrowserActivityFragment extends Fragment {

    /** Тэг для логов */
    private static final String TAG = "BrowserActivityFragment";

    /** Текущая ссылка с настройками */
    private String currentURI;
    /** Виджет браузера для отображения страницы с настройками */
    private WebView mWebView;
    /** Окно с информацией о загрузке */
    private LinearLayout linEmpty;
    /** Индикатор прогресса */
    private ProgressBar mProgressBar;

    /**
     * Возвращает новый экземпляр фрагмента
     * @param uri ссылка с настройками
     * @return новый экземпляр фрагмента
     */
    public static BrowserActivityFragment newInstance(String uri) {
        BrowserActivityFragment fragment = new BrowserActivityFragment();

        Bundle args = new Bundle();
        args.putString("URI", uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_browser, container, false);

        currentURI = getArguments().getString("URI");

        mWebView = (WebView) v.findViewById(R.id.webView);
        linEmpty = (LinearLayout) v.findViewById(R.id.linEmpty);
        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_page_progress_bar);
        v.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebView(true);
            }
        });

        mProgressBar.setMax(100); // Значения в диапазоне 0-100
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl(currentURI);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                /*String js = "javascript:document.getElementsByClassName('b-header')[0].style.display = \"none\";" +
                        "document.getElementsByClassName('b-tabs')[0].style.display = \"none\";" +
                        "document.getElementsByClassName('b-nav-helper')[0].style.display = \"none\";" +
                        "document.getElementsByClassName('control-self-submit')[0].value = \"Поиск\";";
                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d(TAG, "onReceiveValue");
                        }
                    });
                } else {
                    view.loadUrl(js);
                }*/
                showWebView(true);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!checkURL(url)) return true;
                Log.d(TAG, url);
                currentURI = url;

                return false;
            }
        });

        v.findViewById(R.id.button_save_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("ResultURI", currentURI);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        });

        return v;
    }

    /**
     * Проверяет URL на правильность возвращаемого адреса с параметрами поиска
     * @param url Проверяемый URL
     * @return true - ссылка верная, false - ссылка не верна
     */
    private boolean checkURL(String url) {
        /*ArrayList<String> URLs = new ArrayList<>();
        URLs.add("https://m.avito.ru/add");
        URLs.add("https://m.avito.ru/");
        URLs.add("https://m.avito.ru/favorites");
        URLs.add("https://m.avito.ru/profile");
        if (URLs.indexOf(url) != -1) {
            Toast.makeText(getActivity(), "Не та кнопка ;)", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (url.contains("hist_back=1")) {
                Toast.makeText(getActivity(), "Не та кнопка ;)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }*/
        return true;
    }

    /** Скрывает окно загрузки и отображает загруженную страницу */
    private void showWebView(boolean showWebView) {
        mWebView.setVisibility(showWebView ? View.VISIBLE : View.GONE);
        linEmpty.setVisibility(showWebView ? View.GONE : View.VISIBLE);
    }
}

package com.ramgaunt.autorunotify.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ramgaunt.autorunotify.PrefUtils;
import com.ramgaunt.autorunotify.R;

/**
 * Фрагмент ввода капчи
 */
public class EnterCaptchaFragment extends Fragment {

    /** Тэг для логирования */
    private static String TAG = "BrowserActivityFragment";

    /** Виджет браузера для отображения страницы с капчей */
    private WebView mWebView;
    /** Окно с информацией о загрузке */
    private LinearLayout linEmpty;
    /** Индикатор прогресса */
    private ProgressBar mProgressBar;

    /**
     * Возвращает новый экземпляр фрагмента
     * @param uri ссылка с капчей
     * @return новый экземпляр фрагмента
     */
    public static EnterCaptchaFragment newInstance(String uri) {
        EnterCaptchaFragment fragment = new EnterCaptchaFragment();

        Bundle args = new Bundle();
        args.putString("URI", uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_captcha, container, false);

        final String URI = getArguments().getString("URI");

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

        mWebView.loadUrl(URI);

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
                showWebView(true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String cookie = CookieManager.getInstance().getCookie(url);
                int i = 0;
                //if (cookie.contains("spravka")) {
                    String[] split = cookie.split(";");
                    int length = split.length;
                    while (i < length) {
                        if (split[i].toLowerCase().contains("spravka")) {
                            PrefUtils.setCookie(getActivity(), CookieManager.getInstance().getCookie(url));
                            PrefUtils.setUserAgent(getActivity(), mWebView.getSettings().getUserAgentString());
                        }
                        i++;
                    }
                //}
            }
        });

        return v;
    }

    /** Скрывает окно загрузки и отображает загруженную страницу */
    private void showWebView(boolean showWebView) {
        mWebView.setVisibility(showWebView ? View.VISIBLE : View.GONE);
        linEmpty.setVisibility(showWebView ? View.GONE : View.VISIBLE);
    }
}

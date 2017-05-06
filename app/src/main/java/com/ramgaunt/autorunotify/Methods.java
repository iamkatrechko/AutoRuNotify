package com.ramgaunt.autorunotify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Дополнительные методы (утилита)
 */
public class Methods {
    public static final boolean isDeveloper = false;

    private final int LEVEL_0 = 151251;
    private final int LEVEL_1 = 786654;
    private final int LEVEL_2 = 455468;
    private final int LEVEL_3 = 978645;

    private Context mContext;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    public Methods(Context context){
        mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefsEditor = prefs.edit();
        prefsEditor.apply();
    }

    public boolean showDialogReview() {
        return ((getStartCount() % 5) == 0) && !getReviewIsShowed();
    }

    public Boolean getReviewIsShowed(){
        return prefs.getBoolean("mReview", false);
    }

    public void getReviewIsShowed(Boolean bool){
        prefsEditor.putBoolean("mReview", bool);
        prefsEditor.apply();
    }

    public boolean getWarningIsShow(){
        return prefs.getBoolean("mWarning", false);
    }

    public void setWarningIsShow(Boolean bool){
        prefsEditor.putBoolean("mWarning", bool);
        prefsEditor.apply();
    }

    private int getLevelPref(){
        return prefs.getInt("per_seconds", LEVEL_3);
    }

    public void setLevelPref(int level){
        int LEVEL_CODE;
        switch (level){
            default:
                LEVEL_CODE = LEVEL_3;
                break;
        }
        Log.d("Methods", "Установка уровня: " + level);
        prefsEditor.putInt("per_seconds", LEVEL_CODE);
        prefsEditor.apply();
    }

    public int getPLevel(){
        int LEVEL_CODE = getLevelPref();
        switch (LEVEL_CODE){
            default:
                return 3;
        }
    }

    public int getAllowedSearches(){
        int level = getPLevel();
        switch (level){
            default:
                return 1000;
        }
    }

    public int getStartCount(){
        return prefs.getInt("mStartCount", 0);
    }

    public void incrementStartCount(){
        int startCount = getStartCount() + 1;
        prefsEditor.putInt("mStartCount", startCount);
        prefsEditor.apply();
    }

    public static void developerToast(Context context, String text){
        if (isDeveloper) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public void mGoToGooglePlay() {
        final String appName = mContext.getPackageName();
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        }
    }

    public void mGoToGooglePlayDeveloper() {
        final String appName = "I'm Katrechko";
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=" + appName)));
        }
    }

    public void mSendMail(String text){
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        // Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "iamkatrechko@gmail.com"});
        // Зачем
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.app_name));
        // О чём
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        mContext.startActivity(Intent.createChooser(emailIntent,
                mContext.getResources().getText(R.string.send)));
    }
}

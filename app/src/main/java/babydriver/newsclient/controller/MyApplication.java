package babydriver.newsclient.controller;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import babydriver.newsclient.R;

/**
 * Application class used to initialize settings
 */

public class MyApplication extends Application
{
    public static boolean isPreviewShowPicture;
    private static boolean first = true;

    final String FAVORITE_LIST = "favorite_list";
    final String DOWNLOADED_LIST = "download_list";
    public static HashSet<String> favorite_list = new HashSet<>();
    public static HashSet<String> downloaded_list = new HashSet<>();
    public static List<Integer> showCateNumList = new ArrayList<>();
    public static HashSet<String> read_list = new HashSet<>();

    public static final List<String> cateNames = new ArrayList<>();

    public static boolean isFirst()
    {
        if (first)
        {
            first = false;
            return true;
        }
        return false;
    }

    private void setDefaultCateNames()
    {
        cateNames.add(getApplicationContext().getString(R.string.news_kind0));
        cateNames.add(getApplicationContext().getString(R.string.news_kind1));
        cateNames.add(getApplicationContext().getString(R.string.news_kind2));
        cateNames.add(getApplicationContext().getString(R.string.news_kind3));
        cateNames.add(getApplicationContext().getString(R.string.news_kind4));
        cateNames.add(getApplicationContext().getString(R.string.news_kind5));
        cateNames.add(getApplicationContext().getString(R.string.news_kind6));
        cateNames.add(getApplicationContext().getString(R.string.news_kind7));
        cateNames.add(getApplicationContext().getString(R.string.news_kind8));
        cateNames.add(getApplicationContext().getString(R.string.news_kind9));
        cateNames.add(getApplicationContext().getString(R.string.news_kind10));
        cateNames.add(getApplicationContext().getString(R.string.news_kind11));
        cateNames.add(getApplicationContext().getString(R.string.news_kind12));
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        SpeechUtility.createUtility(getBaseContext(), SpeechConstant.APPID + "=" + getString(R.string.appid));

        SharedPreferences favorite_preferences = getSharedPreferences(FAVORITE_LIST, MODE_PRIVATE);
        SharedPreferences.Editor favorite_editor = favorite_preferences.edit();
        favorite_editor.putStringSet(FAVORITE_LIST, favorite_list);
        favorite_editor.apply();

        SharedPreferences downloaded_preferences = getSharedPreferences(DOWNLOADED_LIST, MODE_PRIVATE);
        SharedPreferences.Editor downloaded_editor = downloaded_preferences.edit();
        downloaded_editor.putStringSet(DOWNLOADED_LIST, downloaded_list);
        downloaded_editor.apply();

        setDefaultCateNames();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isNight = sharedPreferences.getBoolean("night_switch", false);
        if (isNight)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        isPreviewShowPicture = sharedPreferences.getBoolean("pic_switch", true);
        Set<String> defaultCategories = new TreeSet<>();
        for (int i = 1; i <= 12; i++)
        {
            defaultCategories.add(String.valueOf(i));
        }

        Set<String> cateNumStrs = sharedPreferences.getStringSet("category_select", defaultCategories);
        List<Integer> showCateNumList = new ArrayList<>();
        showCateNumList.add(0);
        for (String cateNumStr : cateNumStrs)
        {
            showCateNumList.add(Integer.parseInt(cateNumStr));
        }
        Collections.sort(showCateNumList);
        MyApplication.showCateNumList = showCateNumList;
    }
}

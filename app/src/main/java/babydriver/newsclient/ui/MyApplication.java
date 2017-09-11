package babydriver.newsclient.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import babydriver.newsclient.R;
import babydriver.newsclient.model.Settings;

/**
 * Application class used to initialize settings
 */

public class MyApplication extends Application
{
    public static final List<String> cateNames = new ArrayList<>();

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
        setDefaultCateNames();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isNight = sharedPreferences.getBoolean("night_switch", false);
        if (isNight)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Settings.isPreviewShowPicture = sharedPreferences.getBoolean("pic_switch", true);
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
        Settings.showCateNumList = showCateNumList;
    }
}

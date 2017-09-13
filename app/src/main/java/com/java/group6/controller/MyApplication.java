package com.java.group6.controller;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.java.group6.R;
import com.java.group6.model.NewsBrief;

/**
 * Application class used to initialize settings
 */

public class MyApplication extends Application
{
    public static boolean isPreviewShowPicture;
    public static String newsDetail_directory;
    public static String favorite_newsBrief_directory;
    public static String downloaded_newsBrief_directory;
    public static String cache_newsBrief_directory;
    public static String news_list_directory;

    private static boolean first = true;

    final static String FAVORITE_LIST = "favorite_list";
    final static String DOWNLOADED_LIST = "download_list";
    final static String CACHE_LIST = "cache_list";

    public static List<Integer> showCateNumList = new ArrayList<>();
    public static HashSet<String> read_list = new HashSet<>();

    public static HashSet<String> favorite_list = new HashSet<>();
    public static HashSet<String> downloaded_list = new HashSet<>();
    public static ArrayList<HashSet<String>> cache_list = new ArrayList<>();
    public static HashMap<String, NewsBrief> favorite = new HashMap<>();
    public static HashMap<String, NewsBrief> downloaded = new HashMap<>();
    public static HashMap<String, NewsBrief> cache = new HashMap<>();

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

        for (int i = 0; i <= 12; i++)
        {
            cache_list.add(new HashSet<String>());
            cache_list.get(i).clear();
        }

        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        assert dir != null;
        boolean f;
        newsDetail_directory = dir.getPath() + "/newsDetails";
        favorite_newsBrief_directory = dir.getPath() + "/favorite_newsBrief";
        downloaded_newsBrief_directory = dir.getPath() + "/downloaded_newsBrief";
        cache_newsBrief_directory = dir.getPath() + "/cache_newsBrief";
        news_list_directory = dir.getPath() + "/news_list";

        f = new File(newsDetail_directory).mkdir();
        f &= new File(favorite_newsBrief_directory).mkdir();
        f &= new File(downloaded_newsBrief_directory).mkdir();
        f &= new File(cache_newsBrief_directory).mkdir();
        f &= new File(news_list_directory).mkdir();
        f &= new File(news_list_directory + "/cache").mkdir();
        if (f) Log.e("MyApplication", "directory made success!");

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

    public static void load(Context context)
    {
        Operation.loadList(news_list_directory + "/" + FAVORITE_LIST + ".txt", favorite_list);
        Operation.loadList(news_list_directory + "/" + DOWNLOADED_LIST + ".txt", downloaded_list);
        for (int i = 0; i <= 12; i++)
        {
            HashSet<String> tmp = new HashSet<>();
            Operation.loadList(news_list_directory + "/cache/" + cateNames.get(i) + ".txt", tmp);
            cache_list.set(i, tmp);
        }

        Operation.loadNewsBrief(favorite_list, favorite_newsBrief_directory, favorite);
        Operation.loadNewsBrief(downloaded_list, downloaded_newsBrief_directory, downloaded);
        for (int i = 0; i <= 12; i++)
            Operation.loadNewsBrief(cache_list.get(i), cache_newsBrief_directory, cache);
        Log.e("MyApplication", "load settings");

    }

    public static void save(Context context)
    {
        Operation.saveList(news_list_directory + "/" + FAVORITE_LIST + ".txt", favorite_list);
        Operation.saveList(news_list_directory + "/" + DOWNLOADED_LIST + ".txt", downloaded_list);
        for (int i = 0; i <= 12; i++)
            Operation.saveList(news_list_directory + "/cache/" + cateNames.get(i) + ".txt", cache_list.get(i));

        Operation.saveNewsBrief(favorite_list, favorite_newsBrief_directory, favorite);
        Operation.saveNewsBrief(downloaded_list, downloaded_newsBrief_directory, downloaded);
        for (int i = 0; i <= 12; i++)
            Operation.saveNewsBrief(cache_list.get(i), cache_newsBrief_directory, cache);
        Log.e("MyApplication", "save settings");
    }
}

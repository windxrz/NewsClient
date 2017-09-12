package babydriver.newsclient.controller;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBrief;

/**
 * Application class used to initialize settings
 */

public class MyApplication extends Application
{
    public static boolean isPreviewShowPicture;
    public static String newsDetail_directory;
    public static String favorite_newsBrief_directory;
    public static String downloaded_newsBrief_directory;

    private static boolean first = true;

    final static String FAVORITE_LIST = "favorite_list";
    final static String DOWNLOADED_LIST = "download_list";
    public static HashSet<String> favorite_list = new HashSet<>();
    public static HashSet<String> downloaded_list = new HashSet<>();
    public static List<Integer> showCateNumList = new ArrayList<>();
    public static HashSet<String> read_list = new HashSet<>();
    public static HashMap<String, NewsBrief> favorite = new HashMap<>();
    public static HashMap<String, NewsBrief> downloaded = new HashMap<>();

    private static SharedPreferences favorite_preferences;
    private static SharedPreferences downloaded_preferences;

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

        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        assert dir != null;
        boolean f;
        newsDetail_directory = dir.getPath() + "/newsDetails";
        favorite_newsBrief_directory = dir.getPath() + "/favorite_newsBrief";
        downloaded_newsBrief_directory = dir.getPath() + "/downloaded_newsBrief";
        f = new File(newsDetail_directory).mkdir();
        f &= new File(favorite_newsBrief_directory).mkdir();
        f &= new File(downloaded_newsBrief_directory).mkdir();

        if (f) Log.e("ok", "ok");

        favorite_preferences = getSharedPreferences(FAVORITE_LIST, MODE_PRIVATE);
        favorite_list = (HashSet<String>) favorite_preferences.getStringSet(FAVORITE_LIST, favorite_list);
        if (favorite_list == null) favorite_list = new HashSet<>();
        downloaded_preferences = getSharedPreferences(DOWNLOADED_LIST, MODE_PRIVATE);
        downloaded_list = (HashSet<String>) downloaded_preferences.getStringSet(DOWNLOADED_LIST, downloaded_list);
        if (downloaded_list == null) downloaded_list = new HashSet<>();


        Operation.fetchNewsBrief(favorite_list, favorite_newsBrief_directory, favorite);
        Operation.fetchNewsBrief(downloaded_list, downloaded_newsBrief_directory, downloaded);
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

    public static void save()
    {
        Log.e("MyApplication", "save");
        SharedPreferences.Editor favorite_editor = favorite_preferences.edit();
        favorite_editor.putStringSet(FAVORITE_LIST, favorite_list);
        favorite_editor.apply();

        SharedPreferences.Editor download_editor = downloaded_preferences.edit();
        download_editor.putStringSet(DOWNLOADED_LIST, downloaded_list);
        download_editor.apply();

        Operation.saveNewsBrief(favorite_list, favorite_newsBrief_directory, favorite);
        Operation.saveNewsBrief(downloaded_list, downloaded_newsBrief_directory, downloaded);
    }
}

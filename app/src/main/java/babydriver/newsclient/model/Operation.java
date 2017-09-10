package babydriver.newsclient.model;

import android.util.Log;

public class Operation
{
    public static boolean isFavorite(NewsBrief news)
    {
        return Settings.favorite_list.contains(news.news_ID);
    }

    public static boolean isDownloaded(NewsBrief news)
    {
        return Settings.favorite_list.contains(news.news_ID);
    }

    public static void like(NewsBrief news)
    {
        if (isFavorite(news))
            Settings.favorite_list.remove(news.news_ID);
        else
            Settings.favorite_list.add(news.news_ID);
    }

    public static void download(NewsBrief news)
    {
        Log.e("download", news.news_ID);
    }
}

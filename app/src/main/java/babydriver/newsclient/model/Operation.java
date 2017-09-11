package babydriver.newsclient.model;

import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import babydriver.newsclient.model.NewsRequester.OnRequestListener;

public class Operation
{
    private static HashSet<String> downloading = new HashSet<>();

    public static boolean isFavorite(String id)
    {
        return Settings.favorite_list.contains(id);
    }

    public static boolean isDownloaded(String id)
    {
        return Settings.downloaded_list.contains(id);
    }

    public static boolean isDownloading(String id)
    {
        return downloading.contains(id);
    }

    private int missions;
    private String id;
    private boolean success;

    public void finish(boolean state)
    {
        missions--;
        if (!state) success = false;
    }

    public boolean isFinished()
    {
        if (missions == 0)
        {
            downloading.remove(id);
            if (success) Settings.downloaded_list.add(id);
            return true;
        }
        else
            return false;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void like(String id)
    {
        if (isFavorite(id))
            Settings.favorite_list.remove(id);
        else
            Settings.favorite_list.add(id);
    }

    private void remove(NewsBrief news, File dir)
    {
        Settings.downloaded_list.remove(news.news_ID);
        String directory = dir.getPath() + "/" + id;
        File d = new File(directory);
        if (d.isDirectory())
        {
            String files[] = d.list();
            boolean isOK;
            for (String file : files)
            {
                File f = new File(directory + "/" + file);
                isOK = f.delete();
                if (!isOK) Log.e(f.toString(), "delete fail");
            }
            isOK = d.delete();
            if (!isOK) Log.e(d.toString(), "delete fail");
        }
    }

    public void download(NewsBrief news, File dir, OnRequestListener<String> mListener)
    {
        id = news.news_ID;
        success = true;
        if (isDownloaded(news.news_ID))
            remove(news, dir);
        else
        {
            String directory = dir.getPath();
            directory = directory + "/" + id + "/";
            File file = new File(directory);
            boolean isOk = (file.isDirectory() || file.mkdir());
            if (isOk)
            {
                downloading.add(id);
                missions = 1 + news.newsPictures.size();
                NewsRequester requester = new NewsRequester();
                requester.downloadRequestDetail(id, directory, mListener);
                for (int i = 0; i < news.newsPictures.size(); i++)
                {
                    String picUrl = news.newsPictures.get(i);
                    String suffix = "";
                    Pattern p = Pattern.compile("\\.[^\\.]+$");
                    Matcher m = p.matcher(picUrl);
                    if (m.find())
                        suffix = m.group();
                    requester.downloadRequestPicture(id, picUrl, directory + i + suffix, mListener);
                }
            }
        }
    }
}

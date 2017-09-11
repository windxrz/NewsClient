package babydriver.newsclient.model;

import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import babydriver.newsclient.model.NewsRequester.OnRequestListener;

public class Operation
{
    public static String normal = "normal";
    static String download = "download";

    final private static HashSet<String> downloading = new HashSet<>();

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

    private String id;

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

    @SuppressWarnings("unchecked")
    public void download(NewsBrief news, File dir, final OnOperationListener mListener)
    {
        id = news.news_ID;
        final boolean[] success = {true};
        final int[] missions = new int[1];
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
                missions[0] = 1 + news.newsPictures.size();
                NewsRequester requester = new NewsRequester();
                OnRequestListener<String> requestListener = new OnRequestListener<String>()
                {
                    @Override
                    public void onSuccess(String detail)
                    {
                        missions[0]--;
                        if (missions[0] == 0)
                        {
                            mListener.onSuccess(download, id);
                            downloading.remove(id);
                            if (success[0]) Settings.downloaded_list.add(id);
                        }
                    }

                    @Override
                    public void onFailure()
                    {
                        missions[0]--;
                        success[0] = false;
                        if (missions[0] == 0)
                            mListener.onFailure(download, "");
                    }
                };
                requester.downloadRequestDetail(id, directory, requestListener);
                for (int i = 0; i < news.newsPictures.size(); i++)
                {
                    String picUrl = news.newsPictures.get(i);
                    String suffix = "";
                    Pattern p = Pattern.compile("\\.[^\\.]+$");
                    Matcher m = p.matcher(picUrl);
                    if (m.find())
                        suffix = m.group();
                    requester.downloadRequestPicture(picUrl, directory + i + suffix, requestListener);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void requestLatest(Map<String, Integer> map, final OnOperationListener listener)
    {
        NewsRequester requester = new NewsRequester();

        requester.requestLatest(map, new OnRequestListener<NewsBriefList>()
            {
                @Override
                public void onSuccess(NewsBriefList data)
                {
                    listener.onSuccess(normal, data);
                }

                @Override
                public void onFailure()
                {
                    listener.onFailure(normal, null);
                }
            });
    }

    @SuppressWarnings("unchecked")
    public void normalRequestDetail(final String newsId, final OnOperationListener listener)
    {
        NewsRequester requester = new NewsRequester();
        requester.normalRequestDetail(newsId, new OnRequestListener<NewsDetail>()
        {
            @Override
            public void onSuccess(NewsDetail detail)
            {
                listener.onSuccess(normal, detail);
            }

            @Override
            public void onFailure()
            {
                listener.onFailure(normal, null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void normalRequestPicture(String picUrl, final String cacheDir, final int pos, final OnOperationListener listener)
    {
        NewsRequester requester = new NewsRequester();
        requester.normalRequestPicture(picUrl, cacheDir, new OnRequestListener<Integer>()
        {
            @Override
            public void onSuccess(Integer detail)
            {
                listener.onSuccess(normal, pos);
            }

            @Override
            public void onFailure()
            {
                listener.onFailure(normal, null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void requestSearch(String keyword, Map<String, Integer> map, final OnOperationListener listener)
    {
        NewsRequester requester = new NewsRequester();
        requester.requestSearch(keyword, map, new OnRequestListener<NewsBriefList>()
        {
            @Override
            public void onSuccess(NewsBriefList detail)
            {
                listener.onSuccess(normal, detail);
            }

            @Override
            public void onFailure()
            {
                listener.onFailure(normal, null);
            }
        });
    }

    public interface OnOperationListener<T>
    {
        void onSuccess(String type, T detail);
        void onFailure(String type, T detail);
    }
}

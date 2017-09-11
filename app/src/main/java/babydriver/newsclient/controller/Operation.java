package babydriver.newsclient.controller;

import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import babydriver.newsclient.controller.NewsRequester.OnRequestListener;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.NewsDetail;

public class Operation
{
    public static String LATEST = "latest";
    public static String DETAIL = "detail";
    public static String PICTURE = "picture";
    public static String SEARCH = "search";
    private static String DOWNLOAD = "download";

    final private static HashSet<String> downloading = new HashSet<>();

    public static boolean isFavorite(String id)
    {
        return MyApplication.favorite_list.contains(id);
    }

    public static boolean isDownloaded(String id)
    {
        return MyApplication.downloaded_list.contains(id);
    }

    public static boolean isDownloading(String id)
    {
        return downloading.contains(id);
    }

    private String id;

    private OnOperationListener listener;

    public Operation(OnOperationListener listener)
    {
        this.listener = listener;
    }

    public void like(String id)
    {
        if (isFavorite(id))
            MyApplication.favorite_list.remove(id);
        else
            MyApplication.favorite_list.add(id);
    }

    private void remove(NewsBrief news, File dir)
    {
        MyApplication.downloaded_list.remove(news.news_ID);
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
    public void download(NewsBrief news, File dir)
    {
        id = news.news_ID;
        final boolean[] success = {false};
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
                OnRequestListener<Integer> requestListener = new OnRequestListener<Integer>()
                {
                    @Override
                    public void onSuccess(Integer detail)
                    {
                        missions[0]--;
                        Log.e("progress", missions[0] + " " + detail);
                        if (detail.equals(1)) success[0] = true;
                        if (missions[0] == 0)
                        {
                            listener.onSuccess(DOWNLOAD, id);
                            downloading.remove(id);
                            if (success[0]) MyApplication.downloaded_list.add(id);
                        }
                    }

                    @Override
                    public void onFailure()
                    {
                        missions[0]--;
                        Log.e("progress", missions[0] + "");
                        if (missions[0] == 0)
                        {
                            downloading.remove(id);
                            if (success[0])
                            {
                                MyApplication.downloaded_list.add(id);
                                listener.onSuccess(DOWNLOAD, id);
                            }
                            else
                                listener.onFailure(DOWNLOAD, "");
                        }
                    }
                };
                requester.downloadDetail(id, directory, requestListener);
                for (int i = 0; i < news.newsPictures.size(); i++)
                {
                    String picUrl = news.newsPictures.get(i);
                    String suffix = "";
                    Pattern p = Pattern.compile("\\.[^\\.]+$");
                    Matcher m = p.matcher(picUrl);
                    if (m.find())
                        suffix = m.group();
                    requester.downloadPicture(picUrl, directory + i + suffix, new BitmapFactory.Options(), requestListener);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void requestLatest(Map<String, Integer> map)
    {
        NewsRequester requester = new NewsRequester();

        requester.requestLatest(map, new OnRequestListener<NewsBriefList>()
            {
                @Override
                public void onSuccess(NewsBriefList data)
                {
                    listener.onSuccess(LATEST, data);
                }

                @Override
                public void onFailure()
                {
                    listener.onFailure(LATEST, null);
                }
            });
    }

    @SuppressWarnings("unchecked")
    public void requestDetail(final String newsId)
    {
        NewsRequester requester = new NewsRequester();
        requester.requestDetail(newsId, new OnRequestListener<NewsDetail>()
        {
            @Override
            public void onSuccess(NewsDetail detail)
            {
                listener.onSuccess(DETAIL, detail);
            }

            @Override
            public void onFailure()
            {
                listener.onFailure(DETAIL, null);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void requestPicture(final String picUrl, final String cacheDir, final int pos, BitmapFactory.Options option)
    {
        NewsRequester requester = new NewsRequester();
        requester.downloadPicture(picUrl, cacheDir, option, new OnRequestListener<Integer>()
        {
            @Override
            public void onSuccess(Integer detail)
            {
                listener.onSuccess(PICTURE, pos);
            }

            @Override
            public void onFailure()
            {
                listener.onFailure(PICTURE, pos);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void requestSearch(String keyword, Map<String, Integer> map)
    {
        NewsRequester requester = new NewsRequester();
        requester.requestSearch(keyword, map, new OnRequestListener<NewsBriefList>()
        {
            @Override
            public void onSuccess(NewsBriefList detail)
            {
                listener.onSuccess(SEARCH, detail);
            }

            @Override
            public void onFailure()
            {
                listener.onFailure(SEARCH, null);
            }
        });
    }

    public interface OnOperationListener<T>
    {
        void onSuccess(String type, T detail);
        void onFailure(String type, T detail);
    }
}

package com.java.group6.controller;

import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.java.group6.controller.NewsRequester.OnRequestListener;
import com.java.group6.model.NewsBrief;
import com.java.group6.model.NewsBriefList;
import com.java.group6.model.NewsDetail;

public class Operation
{
    public static String LATEST = "latest";
    public static String DETAIL = "detail";
    public static String PICTURE = "picture";
    public static String SEARCH = "search";
    public static String DOWNLOAD = "download";

    final private static HashSet<String> downloading = new HashSet<>();

    public static boolean isFavorite(String id)
    {
        return MyApplication.favorite_list.contains(id);
    }

    public static boolean isDownloaded(String id)
    {
        return MyApplication.downloaded_list.contains(id);
    }

    public static boolean isRead(String id)
    {
        return MyApplication.read_list.contains(id);
    }

    public static boolean isDownloading(String id)
    {
        return downloading.contains(id);
    }

    private String id;
    private NewsBrief newsBrief;
    private OnOperationListener listener;

    public Operation(OnOperationListener listener)
    {
        this.listener = listener;
    }

    public void like(NewsBrief news)
    {
        String id = news.news_ID;
        if (isFavorite(id))
        {
            MyApplication.favorite_list.remove(id);
            MyApplication.favorite.remove(id);
        }
        else
        {
            MyApplication.favorite_list.add(id);
            MyApplication.favorite.put(id, news);
        }
    }

    private void remove(NewsBrief news, File dir)
    {
        MyApplication.downloaded_list.remove(news.news_ID);
        MyApplication.downloaded.remove(news.news_ID);
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
                if (!isOK) Log.e("remove newsBrief", f.toString() + " delete fail");
            }
            isOK = d.delete();
            if (!isOK) Log.e("remove newsBrief", "delete fail");
        }
    }

    static void loadList(String filename, HashSet<String> set)
    {
        try
        {
            set.clear();
            FileInputStream fi = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fi));
            String s;
            while ((s = reader.readLine()) != null)
            {
                set.add(s);
            }
            reader.close();
        }
        catch (IOException ignored) {}
    }

    static void saveList(String filename, HashSet<String> set)
    {
        try
        {
            FileOutputStream fo = new FileOutputStream(filename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fo));
            String s;
            for (String id : set)
            {
                writer.write(id + "\n");
            }
            writer.flush();
            writer.close();
        }
        catch (IOException ignored) {}
    }

    static void loadNewsBrief(HashSet<String> set, String dir, HashMap<String, NewsBrief> map)
    {
        if (set != null)
        {
            for (String id : set)
            {
                String filename = dir + "/" + id;
                try
                {
                    FileInputStream fi = new FileInputStream(filename);
                    ObjectInputStream si = new ObjectInputStream(fi);
                    NewsBrief brief = (NewsBrief) si.readObject();
                    si.close();
                    map.put(id, brief);
                } catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    static void saveNewsBrief(HashSet<String> set, String dir, HashMap<String, NewsBrief> map)
    {
        if (set != null)
        {
            for (String id : set)
            {
                String filename = dir + "/" + id;
                try
                {
                    FileOutputStream fo = new FileOutputStream(filename);
                    ObjectOutputStream so = new ObjectOutputStream(fo);
                    so.writeObject(map.get(id));
                    so.flush();
                    so.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void download(NewsBrief news, String directory, final int pos)
    {
        File dir = new File(directory);
        id = news.news_ID;
        newsBrief = news;
        final boolean[] success = {false};
        final int[] missions = new int[1];
        if (isDownloaded(news.news_ID))
            remove(news, dir);
        else
        {
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
                        if (detail.equals(1)) success[0] = true;
                        if (missions[0] == 0)
                        {
                            listener.onSuccess(DOWNLOAD, pos);
                            downloading.remove(id);
                            if (success[0])
                            {
                                MyApplication.downloaded_list.add(id);
                                MyApplication.downloaded.put(id, newsBrief);
                            }
                        }
                    }

                    @Override
                    public void onFailure()
                    {
                        missions[0]--;
                        if (missions[0] == 0)
                        {
                            downloading.remove(id);
                            if (success[0])
                            {
                                MyApplication.downloaded_list.add(id);
                                MyApplication.downloaded.put(id, newsBrief);
                                listener.onSuccess(DOWNLOAD, pos);
                            }
                            else
                                listener.onFailure(DOWNLOAD, pos);
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

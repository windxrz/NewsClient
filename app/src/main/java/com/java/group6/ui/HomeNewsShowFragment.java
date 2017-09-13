package com.java.group6.ui;

import android.os.Bundle;
import android.support.v4.view.NestedScrollingChild;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.group6.controller.MyApplication;
import com.java.group6.controller.Operation;
import com.java.group6.model.NewsBrief;
import com.java.group6.model.NewsBriefList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HomeNewsShowFragment extends NewsShowFragment
{
    int category = 0;
    private ArrayList<String> news_list = new ArrayList<>();
    boolean refreshed = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            category = savedInstanceState.getInt("category");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("category", category);
    }

    int getCategory()
    {
        return category;
    }

    void setCategory(int t)
    {
        if (category != t)
        {
            Log.e("HomeNewsShowFragment", category + "");
            category = t;
            listRefresh();
        }
    }

    @Override
    void listInitialize()
    {
        news_list.clear();
        news_list.addAll(MyApplication.cache_list.get(category));
        Collections.sort(news_list);
        refreshed = true;
        listAdd();
    }

    @Override
    void listAdd()
    {
        Map<String, Integer> map = new HashMap<>();
        LinearLayoutManager manager = (LinearLayoutManager) recycler_view.getLayoutManager();
        map.put("pageNo", manager.getItemCount() / 25 + 1);
        map.put("pageSize", 25);
        if (category >= 1 && category <= 12)
            map.put("category", category);
        Operation operation = new Operation(this);
        operation.requestLatest(map);
    }

    @Override
    public void onSuccess(String type, Object data)
    {
        super.onSuccess(type, data);
        if (type.equals(Operation.LATEST) && data instanceof NewsBriefList)
        {
            NewsBriefList list = (NewsBriefList) data;
            if (refreshed && category > 0)
            {
                HashSet<String> now = new HashSet<>();
                for (String id : MyApplication.cache_list.get(category))
                {
                    MyApplication.cache.remove(id);
                }
                for (NewsBrief news : list.list)
                {
                    now.add(news.news_ID);
                    MyApplication.cache.put(news.news_ID, news);
                }
                MyApplication.cache_list.set(category, now);
                news_list.clear();
                news_list.addAll(MyApplication.cache_list.get(category));
            }
            addAll(list.list);
            refreshed = false;
        }
    }

    @Override
    public void onFailure(String type, Object data)
    {
        super.onFailure(type, data);
        if (type.equals(Operation.LATEST))
        {
            if (refreshed && news_list.size() > 0)
            {
                ArrayList<NewsBrief> list = new ArrayList<>();
                for (int i = 0; i < news_list.size(); i++)
                {
                    list.add(MyApplication.cache.get(news_list.get(i)));
                }
                addAll(list);
            }
            else
            {
                fetchNewsListFail();
            }
            refreshed = false;
        }
    }

    @Override
    void listRefresh()
    {
        refreshed = true;
        clear();
        listInitialize();
        listAdd();
    }
}

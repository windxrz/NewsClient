package com.java.group6.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.group6.controller.Operation;
import com.java.group6.model.NewsBriefList;

import java.util.HashMap;
import java.util.Map;

public class SearchNewsShowFragment extends NewsShowFragment
{
    String keyword;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipe_refresh_layout.setEnabled(false);
        return view;
    }

    @Override
    public void onSuccess(String type, Object data)
    {
        if (type.equals(Operation.SEARCH) && data instanceof NewsBriefList)
        {
            NewsBriefList list = (NewsBriefList) data;
            addAll(list.list);
        }
    }

    @Override
    public void onFailure(String type, Object data)
    {
        if (type.equals(Operation.SEARCH))
        {
            fetchNewsListFail();
        }
    }

    @Override
    void listInitialize() {}

    @Override
    void listAdd()
    {
        Map<String, Integer> map = new HashMap<>();
        LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
        map.put("pageNo", manager.getItemCount() / 25 + 1);
        map.put("pageSize", 25);
        new Operation(this).requestSearch(keyword, map);
    }

    @Override
    void listRefresh() {}

    void setKeyword(String key)
    {
        clear();
        keyword = key;
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        new Operation(this).requestSearch(keyword, map);
    }
}

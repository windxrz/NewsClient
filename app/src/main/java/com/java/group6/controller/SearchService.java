package com.java.group6.controller;

import java.util.Map;

import com.java.group6.model.NewsBriefList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Interface of searching
 */

interface SearchService
{
    @GET("news/action/query/search")
    Call<NewsBriefList> getSearch(@Query("keyword") String keyword, @QueryMap Map<String, Integer> map);
}

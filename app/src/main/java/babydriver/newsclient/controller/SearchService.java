package babydriver.newsclient.controller;

import java.util.Map;

import babydriver.newsclient.model.NewsBriefList;
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

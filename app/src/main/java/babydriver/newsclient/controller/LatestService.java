package babydriver.newsclient.controller;

import java.util.Map;

import babydriver.newsclient.model.NewsBriefList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Interface of getting the latest news
 */

interface LatestService
{
    @GET("news/action/query/latest")
    Call<NewsBriefList> getLatest(@QueryMap Map<String, Integer> map);
}

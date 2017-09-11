package babydriver.newsclient.model;

import java.util.Map;

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

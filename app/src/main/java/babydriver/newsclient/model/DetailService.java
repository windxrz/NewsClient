package babydriver.newsclient.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface of getting detailed information of a piece of news
 */

interface DetailService
{
    @GET("news/action/query/detail")
    Call<NewsDetail> getDetail(@Query("newsId") String newsId);
}

package babydriver.newsclient.model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface of getting the latest news
 */

public interface LatestService
{
    @GET("news/action/query/latest")
    Call<NewsBriefList> getLatest(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);
}

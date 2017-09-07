package babydriver.newsclient.model;

import babydriver.newsclient.model.NewsBriefList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface of searching
 */

public interface SearchService
{
    @GET("news/action/query/search")
    Call<NewsBriefList> getSearch(@Query("keyword") String keyword, @Query("category") int category, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize);
}

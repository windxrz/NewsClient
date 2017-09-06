package babydriver.newsclient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provide news modules. Bridge between UI and HTTP API.
 */

public class NewsRequester
{
    private Retrofit retrofit;
    private LatestService latestService;
    private SearchService searchService;

    public NewsRequester()
    {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://166.111.68.66:2042/news/")
                .build();
        latestService = retrofit.create(LatestService.class);
        searchService = retrofit.create(SearchService.class);

    }

    public void requestLatest(int newsNum)
    {
        Call<NewsBriefList> latestCall = latestService.getLatest(1, newsNum);
        latestCall.enqueue(new Callback<NewsBriefList>()
                           {
                               @Override
                               public void onResponse(Call<NewsBriefList> call, Response<NewsBriefList> response)
                               {
                                   if (response.isSuccessful())
                                   {
                                       NewsBriefList newsBriefList = response.body();
                                       if (newsBriefList != null)
                                       {
                                           NewsBrief[] newsBriefs = newsBriefList.list;
                                       }
                                   }
                               }

                               @Override
                               public void onFailure(Call<NewsBriefList> call, Throwable t)
                               {

                               }
                           }

        );
    }

    public void requestSearch(String keyword, int category, int pageNo, int pageSize)
    {
        Call<NewsBriefList> searchCall = searchService.getSearch(keyword, category, pageNo, pageSize);
        searchCall.enqueue(new Callback<NewsBriefList>()
                           {
                               @Override
                               public void onResponse(Call<NewsBriefList> call, Response<NewsBriefList> response)
                               {
                                   if (response.isSuccessful())
                                   {
                                       NewsBriefList newsBriefList = response.body();
                                       if (newsBriefList != null)
                                       {
                                           NewsBrief[] newsBriefs = newsBriefList.list;
                                       }
                                   }
                               }

                               @Override
                               public void onFailure(Call<NewsBriefList> call, Throwable t)
                               {

                               }
                           }
        );
    }
}

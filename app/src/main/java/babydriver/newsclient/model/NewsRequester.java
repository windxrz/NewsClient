package babydriver.newsclient.model;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

import babydriver.newsclient.model.LatestService;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.SearchService;
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
    private DetailService detailService;

    public NewsRequester()
    {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://166.111.68.66:2042/")
                .build();
        latestService = retrofit.create(LatestService.class);
        searchService = retrofit.create(SearchService.class);
        detailService = retrofit.create(DetailService.class);

    }

    public void requestLatest(Map<String, Integer> map)
    {
        Call<NewsBriefList> latestCall = latestService.getLatest(map);
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

    public void requestSearch(String keyword, Map<String, Integer> map)
    {
        Call<NewsBriefList> searchCall = searchService.getSearch(keyword, map);
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

    public void requestDetail(String newsId)
    {
        Call<NewsDetail> detailCall = detailService.getDetail(newsId);
        detailCall.enqueue(new Callback<NewsDetail>()
        {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response)
            {
                if (response.isSuccessful())
                {
                    NewsDetail newsDetail = response.body();
                    if (newsDetail != null)
                    {

                    }
                }
            }

            @Override
            public void onFailure(Call<NewsDetail> call, Throwable t)
            {

            }
        });
    }
}

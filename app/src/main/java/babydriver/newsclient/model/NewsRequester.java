package babydriver.newsclient.model;

import android.support.annotation.NonNull;

import java.util.Map;

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
    private LatestService latestService;
    private SearchService searchService;
    private DetailService detailService;
    private onListRequestListener listListener;
    private onDetailRequestListener detailListener;

    public NewsRequester()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(NewsBriefListConverterFactory.create())
                .addConverterFactory(NewsDetailConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://166.111.68.66:2042/")
                .build();
        latestService = retrofit.create(LatestService.class);
        searchService = retrofit.create(SearchService.class);
        detailService = retrofit.create(DetailService.class);
    }

    public NewsRequester(onListRequestListener listListener)
    {
        this();
        this.listListener = listListener;
    }

    public NewsRequester(onDetailRequestListener detailListener)
    {
        this();
        this.detailListener = detailListener;
    }

    public NewsRequester(onListRequestListener listListener, onDetailRequestListener detailListener)
    {
        this();
        this.listListener = listListener;
        this.detailListener = detailListener;
    }

    public void requestLatest(Map<String, Integer> map)
    {
        Call<NewsBriefList> latestCall = latestService.getLatest(map);
        latestCall.enqueue(new Callback<NewsBriefList>()
                           {
                               @Override
                               public void onResponse(@NonNull Call<NewsBriefList> call, @NonNull Response<NewsBriefList> response)
                               {
                                   if (response.isSuccessful())
                                   {
                                       NewsBriefList newsBriefList = response.body();
                                       if (newsBriefList != null)
                                       {
                                           listListener.onSuccess(newsBriefList);
                                       }
                                   }
                               }

                               @Override
                               public void onFailure(@NonNull Call<NewsBriefList> call, @NonNull Throwable t)
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
                               public void onResponse(@NonNull Call<NewsBriefList> call, @NonNull Response<NewsBriefList> response)
                               {
                                   if (response.isSuccessful())
                                   {
                                       NewsBriefList newsBriefList = response.body();
                                       if (newsBriefList != null)
                                       {
                                           listListener.onSuccess(newsBriefList);

                                       }
                                   }
                               }

                               @Override
                               public void onFailure(@NonNull Call<NewsBriefList> call, @NonNull Throwable t)
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
            public void onResponse(@NonNull Call<NewsDetail> call, @NonNull Response<NewsDetail> response)
            {
                if (response.isSuccessful())
                {
                    NewsDetail newsDetail = response.body();
                    detailListener.onSuccess(newsDetail);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsDetail> call, @NonNull Throwable t)
            {

            }
        });
    }

    public interface onListRequestListener
    {
        void onSuccess(NewsBriefList list);
    }

    public interface onDetailRequestListener
    {
        void onSuccess(NewsDetail newsDetail);
    }

}
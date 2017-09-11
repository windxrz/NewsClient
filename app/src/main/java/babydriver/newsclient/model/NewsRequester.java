package babydriver.newsclient.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


class NewsRequester
{
    private LatestService latestService;
    private SearchService searchService;
    private DetailService detailService;
    private PictureService pictureService;

    NewsRequester()
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
        pictureService = retrofit.create(PictureService.class);
    }

    void requestLatest(Map<String, Integer> map, final OnRequestListener<NewsBriefList> listener)
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
                                           listener.onSuccess(newsBriefList);
                                       }
                                       else
                                           listener.onFailure();
                                   }
                                   else
                                       listener.onFailure();
                               }

                               @Override
                               public void onFailure(@NonNull Call<NewsBriefList> call, @NonNull Throwable t)
                               {
                                   listener.onFailure();
                               }
                           }

        );
    }

    void requestSearch(String keyword, Map<String, Integer> map, final OnRequestListener<NewsBriefList> listener)
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
                                           listener.onSuccess(newsBriefList);
                                       }
                                       else
                                           listener.onFailure();
                                   }
                                   else
                                       listener.onFailure();
                               }

                               @Override
                               public void onFailure(@NonNull Call<NewsBriefList> call, @NonNull Throwable t)
                               {
                                   listener.onFailure();
                               }
                           }
        );
    }

    void requestDetail(final String newsId, final OnRequestListener<NewsDetail> listener)
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
                    listener.onSuccess(newsDetail);
                }
                else
                    listener.onFailure();
            }

            @Override
            public void onFailure(@NonNull Call<NewsDetail> call, @NonNull Throwable t)
            {
                listener.onFailure();
            }
        });
    }

    void downloadPicture(String picUrl, final String cacheDir, final OnRequestListener<Integer> listener)
    {
        Call<ResponseBody> pictureCall = pictureService.downloadPic(picUrl);
        pictureCall.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                if (response.isSuccessful())
                {
                    savePicToDisk(cacheDir, response.body());
                    listener.onSuccess(0);
                }
                else
                    listener.onFailure();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t)
            {
                listener.onFailure();
            }
        });
    }

    void downloadDetail(final String newsId, final String cacheDir, final OnRequestListener<Integer> listener)
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
                    try
                    {
                        FileOutputStream fo = new FileOutputStream(cacheDir + "detail.txt");
                        ObjectOutputStream so = new ObjectOutputStream(fo);
                        so.writeObject(newsDetail);
                        so.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    listener.onSuccess(0);
                }
                else
                    listener.onFailure();
            }

            @Override
            public void onFailure(@NonNull Call<NewsDetail> call, @NonNull Throwable t)
            {
                listener.onFailure();
            }
        });
    }

    private void savePicToDisk(String cacheDir, ResponseBody body)
    {
        Bitmap bm;
        InputStream inputStream = null;
        File file = new File(cacheDir);
        try
        {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inPreferredConfig = Bitmap.Config.RGB_565;
            inputStream = body.byteStream();
            bm = BitmapFactory.decodeStream(inputStream, null, option);
            FileOutputStream outputStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            bm.recycle();
            outputStream.flush();
            outputStream.close();
        } catch (NullPointerException e)
        {
            Log.e("Exception", "NullPointerException");
        } catch (FileNotFoundException e)
        {
            Log.e("Exception", "FileNotFound");
        } catch (IOException e)
        {
            Log.e("Exception", "IOException when flushing/closing outputStream");
        } finally
        {
            if (inputStream != null)
                try
                {
                    inputStream.close();
                } catch (IOException e)
                {
                    Log.e("Exception", "IOException when closing inputStream");
                }
        }
    }

    interface OnRequestListener<T>
    {
        void onSuccess(T detail);
        void onFailure();
    }

}
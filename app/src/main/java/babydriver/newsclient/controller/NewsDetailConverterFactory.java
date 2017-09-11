package babydriver.newsclient.controller;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import babydriver.newsclient.model.NewsDetail;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Converter : to NewsDetail; Process news_Time and news_Pictures at the same time.
 */

class NewsDetailConverterFactory extends Converter.Factory
{
    private static final NewsDetailConverterFactory INSTANCE = new NewsDetailConverterFactory();

    static NewsDetailConverterFactory create()
    {
        return INSTANCE;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit)
    {
        if (type == NewsDetail.class)
            return NewsDetailConverterFactory.NewsDetailConverter.INSTANCE;
        return null;
    }

    private static class NewsDetailConverter implements Converter<ResponseBody, NewsDetail>
    {
        static final NewsDetailConverter INSTANCE = new NewsDetailConverter();

        @Override
        public NewsDetail convert(@NonNull ResponseBody value) throws IOException
        {
            NewsDetail newsDetail = new Gson().fromJson(value.string(), NewsDetail.class);
            newsDetail.process();
            return newsDetail;
        }
    }
}

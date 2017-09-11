package babydriver.newsclient.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Converter : to NewsBriefList; Process news_Time and news_Pictures at the same time.
 */

class NewsBriefListConverterFactory extends Converter.Factory
{
    private static final NewsBriefListConverterFactory INSTANCE = new NewsBriefListConverterFactory();

    static NewsBriefListConverterFactory create()
    {
        return INSTANCE;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit)
    {
        if (type == NewsBriefList.class)
            return NewsBriefListConverter.INSTANCE;
        return null;
    }

    private static class NewsBriefListConverter implements Converter<ResponseBody, NewsBriefList>
    {
        static final NewsBriefListConverter INSTANCE = new NewsBriefListConverter();

        @Override
        public NewsBriefList convert(@NonNull ResponseBody value) throws IOException
        {
            NewsBriefList newsBriefList = new Gson().fromJson(value.string(), NewsBriefList.class);
            for (NewsBrief newsBrief : newsBriefList.list)
            {
                newsBrief.process();
            }
            return newsBriefList;
        }
    }

}

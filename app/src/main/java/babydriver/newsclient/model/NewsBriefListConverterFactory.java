package babydriver.newsclient.model;

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

public class NewsBriefListConverterFactory extends Converter.Factory
{
    public static final NewsBriefListConverterFactory INSTANCE = new NewsBriefListConverterFactory();

    public static NewsBriefListConverterFactory create()
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
        public static final NewsBriefListConverter INSTANCE = new NewsBriefListConverter();

        @Override
        public NewsBriefList convert(ResponseBody value) throws IOException
        {
            NewsBriefList newsBriefList = new Gson().fromJson(value.string(), NewsBriefList.class);
            for (NewsBrief newsBrief : newsBriefList.list)
            {
                newsBrief.processTime();
                newsBrief.processPictures();
            }
            return newsBriefList;
        }
    }

}

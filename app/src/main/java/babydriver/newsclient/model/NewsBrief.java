package babydriver.newsclient.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model: Brief information of a piece of news
 */

public class NewsBrief
{
    public String newsClassTag;
    public String news_ID;
    public String news_Source;
    public String news_Title;
    public String news_Time;

    public Date newsTime;

    public String news_URL;
    public String news_Author;
    public String lang_Type;
    public String news_Pictures;

    public List<String> newsPictures;

    public String news_Video;
    public String news_Intro;

    NewsBrief()
    {
        news_Title = "fuck!";
    }

    void processTime()
    {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        try
        {
            newsTime = ft.parse(news_Time);
        } catch (ParseException e)
        {
            Log.e("Parse Exception", "Failed parsing news_Time");
        }
    }

    void processPictures()
    {
        newsPictures = new LinkedList<String>();
        Pattern p = Pattern.compile("https?://\\S+?\\.(png|jpg|jpeg|gif|bmp)");
        Matcher m = p.matcher(news_Pictures);
        while (m.find())
        {
            newsPictures.add(m.group());
        }
    }

    public String toString()
    {
        return news_Title;
    }
}

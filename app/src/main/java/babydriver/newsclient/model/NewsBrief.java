package babydriver.newsclient.model;

import android.app.Application;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import babydriver.newsclient.R;

/**
 * Model: Brief information of a piece of news
 */

public class NewsBrief implements Serializable
{
    public String newsClassTag;
    public String news_ID;
    public String news_Source;
    public String news_Title;
    private String news_Time;

    public Date newsTime;

    public String news_URL;
    public String news_Author;
    public String lang_Type;
    private String news_Pictures;

    public List<String> newsPictures = new LinkedList<>();

    public String news_Video;
    public String news_Intro;

    public NewsBrief(String title)
    {
        news_ID = "";
        news_Title = title;
        newsPictures.clear();
        news_Source = "";
        newsTime = null;
    }

    NewsBrief() {}

    private void processTime()
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

    private void processPictures()
    {
        Pattern p = Pattern.compile("https?://\\S+?\\.(png|jpg|jpeg|gif|bmp)");
        Matcher m = p.matcher(news_Pictures);
        while (m.find())
        {
            newsPictures.add(m.group());
        }
    }

    public void process()
    {
        processPictures();
        processTime();
    }

    public String toString()
    {
        return news_Title;
    }
}

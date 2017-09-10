package babydriver.newsclient.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model: Detail information of a piece of news
 */

public class NewsDetail extends NewsBrief
{
    public String news_Content;
    public String news_Journal;

    void processContent()
    {
        news_Content = news_Content.replaceAll("(?<!^)\\s{2,}(?!$)", "</p><p>");
        news_Content = news_Content.replaceAll("\\s{2,}", "");
        news_Content = "<p>" + news_Content + "</p>";
    }

    @Override
    void process()
    {
        super.process();
        processContent();
    }
}

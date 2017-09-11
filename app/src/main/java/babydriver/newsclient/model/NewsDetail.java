package babydriver.newsclient.model;

import java.io.Serializable;

/**
 * Model: Detail information of a piece of news
 */

public class NewsDetail extends NewsBrief implements Serializable
{
    public String news_Content;
    public String news_Journal;

    private void processContent()
    {
        news_Content = news_Content.replaceAll("(?<!^)\\s{2,}(?!$)", "</p><p>");
        news_Content = news_Content.replaceAll("\\s{2,}", "");
        news_Content = "<p>" + news_Content + "</p>";
    }

    @Override
    public void process()
    {
        super.process();
        processContent();
    }
}

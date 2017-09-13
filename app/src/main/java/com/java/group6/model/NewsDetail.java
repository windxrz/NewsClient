package com.java.group6.model;

import java.io.Serializable;
import java.util.List;

/**
 * Model: Detail information of a piece of news
 */

public class NewsDetail extends NewsBrief implements Serializable
{
    public List<Word> persons, locations, organizations;
    public String news_Content;
    public String news_Journal;
    public String pureContent;

    private void processContent()
    {
        pureContent = news_Content;
        news_Content = news_Content.replaceAll("(?<!^)\\s{2,}(?!$)", "</p><p>");
        news_Content = news_Content.replaceAll("\\s{2,}", "");
        news_Content = "<p>" + news_Content + "</p>";
    }

    private void processWords(List<Word> list)
    {
        for (Word w : list)
        {
            news_Content = news_Content.replace(w.word, "<a href=\"" +
                    "http://baike.baidu.com/item/" +
                    w.word +
                    "\">" + w.word + "</a>");
        }
    }

    @Override
    public void process()
    {
        super.process();
        processContent();
        processWords(persons);
        processWords(locations);
        processWords(organizations);
    }

    private class Word
    {
        public String word;
        public int count;
        public float score;
    }
}

package babydriver.newsclient.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsContent
{

    public static final List<NewsItem> ITEMS = new ArrayList<>();

    private static final Map<String, NewsItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static
    {
        Log.d("Test\n", "test");
        for (int i = 1; i <= COUNT; i++)
        {
            addItem(createNewsItem(i));
        }
    }

    private static void addItem(NewsItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static NewsItem createNewsItem(int position)
    {
        return new NewsItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++)
        {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class NewsItem
    {
        public final String id;
        public final String content;
        final String details;

        private NewsItem(String id, String content, String details)
        {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }
}

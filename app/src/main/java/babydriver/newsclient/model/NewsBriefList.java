package babydriver.newsclient.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import babydriver.newsclient.model.NewsBrief;

/**
 * Model: Response body of API when latest or search
 */

public class NewsBriefList
{
    public static List<NewsBrief> list = new ArrayList<>();
    public int pageNo;
    public int pageSize;
    public int totalPages;
    public int totalRecords;

    static
    {
        for (int i = 1; i <= 200; i++)
        {
            list.add(new NewsBrief());
        }
        NewsRequester requester = new NewsRequester();
        requester.requestLatest(1, 25);
        try
        {
            Thread.sleep(2000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}

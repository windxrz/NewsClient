package babydriver.newsclient.model;

import babydriver.newsclient.model.NewsBrief;

/**
 * Model: Response body of API when latest or search
 */

public class NewsBriefList
{
    public NewsBrief[] list;
    public int pageNo;
    public int pageSize;
    public int totalPages;
    public int totalRecords;
}

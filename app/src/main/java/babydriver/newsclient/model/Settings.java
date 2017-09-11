package babydriver.newsclient.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Settings
{
    public static boolean isPreviewShowPicture;
    static HashSet<String> favorite_list = new HashSet<>();
    static HashSet<String> downloaded_list = new HashSet<>();
    public static List<Integer> showCateNumList = new ArrayList<>();

    public static void setSettings()
    {
        isPreviewShowPicture = false;
        favorite_list.clear();
    }
}

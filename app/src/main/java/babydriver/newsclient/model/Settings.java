package babydriver.newsclient.model;

import java.util.HashSet;

public class Settings
{
    public static boolean isPreviewShowPicture;
    static HashSet<String> favorite_list = new HashSet<>();
    static HashSet<String> downloaded_list = new HashSet<>();

    public static void setSettings()
    {
        isPreviewShowPicture = false;
        favorite_list.clear();
    }
}

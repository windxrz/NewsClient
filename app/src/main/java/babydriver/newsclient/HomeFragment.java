package babydriver.newsclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import babydriver.newsclient.News.NewsContent.NewsItem;

public class HomeFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        NewsShowFragment news_show_fragment = new NewsShowFragment();
        FragmentTransaction traction = getChildFragmentManager().beginTransaction();
        traction.add(R.id.NewsShowFragment, news_show_fragment);
        traction.commit();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
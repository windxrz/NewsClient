package babydriver.newsclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import babydriver.newsclient.R;

public class HomeFragment extends Fragment
{
    NewsShowFragment news_show_fragment;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        news_show_fragment = new NewsShowFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        FragmentTransaction traction = getChildFragmentManager().beginTransaction();
        traction.add(R.id.NewsShowFragment, news_show_fragment);
        traction.commit();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
package babydriver.newsclient.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import babydriver.newsclient.R;

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
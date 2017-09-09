package babydriver.newsclient.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
        Bundle bundle = new Bundle();
        bundle.putString(NewsShowFragment.ARG_TYPE, "HomeFragment");
        bundle.putString(NewsShowFragment.ARG_KEY, "");
        news_show_fragment.setArguments(bundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FragmentTransaction traction = getChildFragmentManager().beginTransaction();
        traction.add(R.id.NewsShowFragment, news_show_fragment);
        traction.commit();
        final TabLayout tab_lay_out = view.findViewById(R.id.tab_layout);
        assert tab_lay_out != null;
        tab_lay_out.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {

                    news_show_fragment.setCategory(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab)
                {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab)
                {

                }
            });
        return view;
    }

}
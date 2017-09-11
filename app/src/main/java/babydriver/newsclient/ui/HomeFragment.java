package babydriver.newsclient.ui;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import babydriver.newsclient.R;
import babydriver.newsclient.model.Settings;

public class HomeFragment extends Fragment
{
    HomeNewsShowFragment home_news_show_fragment;
    TabLayout tab_lay_out;
    TabLayout.OnTabSelectedListener listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        home_news_show_fragment = new HomeNewsShowFragment();
    }

    public void refreshTabs()
    {
        tab_lay_out.removeAllTabs();
        if (listener != null)
            tab_lay_out.removeOnTabSelectedListener(listener);
        for (int cateNum : Settings.showCateNumList)
        {
            Log.e("add tab", cateNum + " added");
            tab_lay_out.addTab(tab_lay_out.newTab().setText(MyApplication.cateNames.get(cateNum)));
        }
        listener = new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                home_news_show_fragment.setCategory(Settings.showCateNumList.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        };
        tab_lay_out.addOnTabSelectedListener(listener);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("HomeFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FragmentTransaction traction = getChildFragmentManager().beginTransaction();
        traction.add(R.id.HomeNewsShowFragment, home_news_show_fragment);
        traction.commit();

        tab_lay_out = view.findViewById(R.id.tab_layout);
        assert tab_lay_out != null;

        refreshTabs();

        return view;
    }
}
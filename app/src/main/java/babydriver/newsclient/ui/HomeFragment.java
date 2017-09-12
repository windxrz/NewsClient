package babydriver.newsclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import babydriver.newsclient.R;
import babydriver.newsclient.controller.MyApplication;

public class HomeFragment extends Fragment
{
    HomeNewsShowFragment home_news_show_fragment;
    TabLayout tab_lay_out;
    TabLayout.OnTabSelectedListener listener = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
        {
            home_news_show_fragment = new HomeNewsShowFragment();
        }
        else
        {
            home_news_show_fragment = (HomeNewsShowFragment)
                    getChildFragmentManager().findFragmentByTag("home_news_show_fragment");
        }
    }

    public void refreshTabs()
    {
        int nowCate = home_news_show_fragment.getCategory();
        boolean selected = false;
        tab_lay_out.removeAllTabs();
        if (listener != null)
            tab_lay_out.removeOnTabSelectedListener(listener);
        for (int cateNum : MyApplication.showCateNumList)
        {
            TabLayout.Tab tab = tab_lay_out.newTab().setText(MyApplication.cateNames.get(cateNum));
            tab_lay_out.addTab(tab);
            if (cateNum == nowCate)
            {
                tab.select();
                selected = true;
            }
        }
        if (!selected)
        {
            TabLayout.Tab firstTab = tab_lay_out.getTabAt(0);
            if (firstTab != null)
            {
                firstTab.select();
                home_news_show_fragment.setCategory(0);
            }
        }
        listener = new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int category = MyApplication.showCateNumList.get(tab.getPosition());
                home_news_show_fragment.setCategory(category);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tab_lay_out = view.findViewById(R.id.tab_layout);
        assert tab_lay_out != null;
        if (savedInstanceState == null)
        {
            FragmentTransaction traction = getChildFragmentManager().beginTransaction();
            traction.add(R.id.HomeNewsShowFragment, home_news_show_fragment, "home_news_show_fragment");
            traction.commit();
        }
        refreshTabs();
        return view;
    }
}
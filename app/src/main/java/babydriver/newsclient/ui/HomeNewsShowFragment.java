package babydriver.newsclient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import babydriver.newsclient.model.NewsRequester;

public class HomeNewsShowFragment extends NewsShowFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipe_refresh_layout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).clear();
                NewsRequester requester = new NewsRequester();
                Map<String, Integer> map = new HashMap<>();
                map.put("pageNo", 1);
                map.put("pageSize", 25);
                if (category >= 1 && category <= 12)
                    map.put("category", category);
                previousTotal = 0;
                totalItemCount = 25;
                requester.requestLatest(map, mNewsBriefRequestListener);
            }
        });

        NewsRequester requester = new NewsRequester();
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        if (category >= 1 && category <= 12)
            map.put("category", category);
        requester.requestLatest(map, mNewsBriefRequestListener);
        return view;
    }
}

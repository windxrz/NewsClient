package babydriver.newsclient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.Operation;

public class HomeNewsShowFragment extends NewsShowFragment
{
    int category = 0;
    boolean loading = false;

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

        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        if (category >= 1 && category <= 12)
            map.put("category", category);
        requester.requestLatest(map, mNewsBriefRequestListener);

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recycler_view, int dx, int dy)
                {
                    super.onScrolled(recycler_view, dx, dy);

                    LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
                    totalItemCount = manager.getItemCount();
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    if (loading)
                    {
                        if (totalItemCount > previousTotal)
                        {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    else if (totalItemCount > 0 && lastVisibleItem == totalItemCount - 1)
                    {
                        loading = true;
                        Map<String, Integer> map = new HashMap<>();
                        map.put("pageNo", totalItemCount / 25 + 1);
                        map.put("pageSize", 25);
                        requester.requestLatest(map, mNewsBriefRequestListener);
                        final Toast toast = Toast.makeText(recycler_view.getContext(), R.string.FetchingNews, Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(
                                new Runnable()
                                {
                                    @Override
                                    public void run() {
                                        toast.cancel();
                                    }
                                }, 500);
                    }
                }
            });

        return view;
    }

    void setCategory(int t)
    {
        if (category != t)
        {
            category = t;
            clear();
            Map<String, Integer> map = new HashMap<>();
            map.put("pageNo", 1);
            map.put("pageSize", 25);
            if (t >= 1 && t <= 12) map.put("category", t);
            requester.requestLatest(map, mNewsBriefRequestListener);
        }
    }

    @Override
    public void onFailure(String info, String id)
    {
        super.onFailure(info, id);
        if (info.equals("NewsBriefList")) loading = false;
    }
}

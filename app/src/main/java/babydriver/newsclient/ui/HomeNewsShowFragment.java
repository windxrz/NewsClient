package babydriver.newsclient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import babydriver.newsclient.R;
import babydriver.newsclient.model.Operation;

public class HomeNewsShowFragment extends NewsShowFragment
{
    int category = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
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
            Operation operation = new Operation();
            operation.requestLatest(map, this);
        }
    }

    @Override
    public void onFailure(String info, Object id)
    {
        super.onFailure(info, id);
        if (info.equals("NewsBriefList")) loading = false;
    }

    @Override
    void listInitialize()
    {
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        if (category >= 1 && category <= 12)
            map.put("category", category);
        Operation operation = new Operation();
        operation.requestLatest(map, this);
    }

    @Override
    void listAdd()
    {
        Map<String, Integer> map = new HashMap<>();
        LinearLayoutManager manager = (LinearLayoutManager) recycler_view.getLayoutManager();
        map.put("pageNo", manager.getItemCount() / 25 + 1);
        map.put("pageSize", 25);
        Operation operation = new Operation();
        operation.requestLatest(map, this);
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

    @Override
    void listRefresh()
    {
        ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).clear();
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        if (category >= 1 && category <= 12)
            map.put("category", category);
        clear();
        Operation operation = new Operation();
        operation.requestLatest(map, this);
    }
}

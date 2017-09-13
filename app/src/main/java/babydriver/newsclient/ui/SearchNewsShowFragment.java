package babydriver.newsclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import babydriver.newsclient.R;
import babydriver.newsclient.controller.Operation;

public class SearchNewsShowFragment extends NewsShowFragment
{
    String keyword;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipe_refresh_layout.setEnabled(false);
        return view;
    }

    @Override
    void listInitialize() {}

    @Override
    void listAdd()
    {
        Map<String, Integer> map = new HashMap<>();
        LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
        map.put("pageNo", manager.getItemCount() / 25 + 1);
        map.put("pageSize", 25);
        new Operation(this).requestSearch(keyword, map);
//        final Toast toast = Toast.makeText(recycler_view.getContext(), R.string.FetchingNews, Toast.LENGTH_SHORT);
//        toast.show();
//        Handler handler = new Handler();
//        handler.postDelayed(
//                new Runnable()
//                {
//                    @Override
//                    public void run() {
//                        toast.cancel();
//                    }
//                }, 500);
    }

    @Override
    void listRefresh() {}

    void setKeyword(String key)
    {
        clear();
        keyword = key;
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        new Operation(this).requestSearch(keyword, map);
    }
}

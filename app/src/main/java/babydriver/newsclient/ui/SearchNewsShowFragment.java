package babydriver.newsclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import babydriver.newsclient.R;

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

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                boolean loading = false;

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
                        requester.requestSearch(keyword, map, mNewsBriefRequestListener);
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

    void setKeyword(String key)
    {
        clear();
        keyword = key;
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        requester.requestSearch(keyword, map, mNewsBriefRequestListener);
    }
}

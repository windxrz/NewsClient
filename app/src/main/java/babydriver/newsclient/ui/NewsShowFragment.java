package babydriver.newsclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.NewsRequester.onRequestListener;

public class NewsShowFragment extends Fragment implements NewsRequester.onRequestListener
{
    OnListFragmentInteractionListener mListener;
    onRequestListener<NewsBriefList> mNewsBriefRequestListener;
    onRequestListener<Integer> mBitmapRequestListener;
    RecyclerView recycler_view;
    SwipeRefreshLayout swipe_refresh_layout;
    int previousTotal = 0;
    int totalItemCount = 25;
    int category = 0;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener)
        {
            mListener = (OnListFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        mNewsBriefRequestListener = (onRequestListener<NewsBriefList>) this;
        mBitmapRequestListener = (onRequestListener<Integer>) this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        recycler_view = view.findViewById(R.id.recycler_view);
        Context context = recycler_view.getContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(context));
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(new ArrayList<NewsBrief>(), mListener, mBitmapRequestListener, this.getActivity()));
        recycler_view.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener()
            {

                boolean loading = false;

                @Override
                public void onScrolled(RecyclerView recycler_view, int dx, int dy)
                {
                    super.onScrolled(recycler_view, dx, dy);

                    LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
                    totalItemCount = manager.getItemCount();
                    if (totalItemCount < 25) totalItemCount = 25;
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    if (loading)
                    {
                        if (totalItemCount > previousTotal)
                        {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    else if (lastVisibleItem == totalItemCount - 1)
                    {
                        loading = true;
                        NewsRequester requester = new NewsRequester();
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
            }
        );

        swipe_refresh_layout = view.findViewById(R.id.refresh_layout);

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    private void addAll(List<NewsBrief> list)
    {
        swipe_refresh_layout.setRefreshing(false);
        ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).addAll(list);
    }

    private void fetchNewsListFail()
    {
        swipe_refresh_layout.setRefreshing(false);
        final Toast toast = Toast.makeText(recycler_view.getContext(), R.string.FetchingNewsFail, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable()
                {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 1000);
    }

    private void setPicture(int pos)
    {
        ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).setPicture(pos);
    }

    void setTop()
    {
        recycler_view.smoothScrollToPosition(0);
    }

    void setCategory(int t)
    {
        if (category != t)
        {
            category = t;
            ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).clear();
            NewsRequester requester = new NewsRequester();
            Map<String, Integer> map = new HashMap<>();
            map.put("pageNo", 1);
            map.put("pageSize", 25);
            map.put("category", t);
            previousTotal = 0;
            totalItemCount = 25;
            requester.requestLatest(map, mNewsBriefRequestListener);
        }
    }

    @Override
    public void onSuccess(Object data)
    {
        if (data instanceof NewsBriefList)
        {
            NewsBriefList list = (NewsBriefList) data;
            addAll(list.list);
        }
        if (data instanceof Integer)
        {
            int pos = (Integer)data;
            setPicture(pos);
        }
    }

    @Override
    public void onFailure(String info)
    {
        if (info.equals("NewsBriefList")) fetchNewsListFail();
    }

    interface OnListFragmentInteractionListener
    {
        void onListFragmentInteraction(NewsBrief item);
    }
}

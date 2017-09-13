package com.java.group6.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.java.group6.controller.MyApplication;
import com.java.group6.controller.Operation;
import com.java.group6.model.NewsBriefList;

import java.util.ArrayList;
import java.util.List;

import com.java.group6.model.NewsBrief;
import com.java.group6.R;

public abstract class NewsShowFragment extends Fragment
        implements Operation.OnOperationListener, MyNewsRecyclerViewAdapter.OnButtonClickedListener
{
    static NewsBrief nonNews;

    OnNewsClickedListener mNewsClickedListener;

    RecyclerView recycler_view;
    SwipeRefreshLayout swipe_refresh_layout;

    private boolean loading = false;
    private boolean refreshing = false;
    private int offset = 0;

    private ArrayList<NewsBrief> list = new ArrayList<>();

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        nonNews = new NewsBrief(getString(R.string.NonNews));
        if (context instanceof OnNewsClickedListener)
            mNewsClickedListener = (OnNewsClickedListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        int pos = -1;
        if (savedInstanceState != null)
        {
            list = (ArrayList<NewsBrief>) savedInstanceState.getSerializable("list");
            if (list == null) list = new ArrayList<>();
            pos = savedInstanceState.getInt("position");
        }
        recycler_view = view.findViewById(R.id.recycler_view);
        Context context = recycler_view.getContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(context));
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(list, this, mNewsClickedListener, this.getActivity()));
        recycler_view.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int pos;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
                int totalItemCount = manager.getItemCount();
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                boolean bar = ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).hasProgressBar();
                if (bar && totalItemCount - 2 == lastVisibleItem && recycler_view.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
                {
                    if (!loading) recycler_view.scrollBy(0, pos - offset);
                }
            }

            @Override
            public void onScrolled(RecyclerView recycler_view, int dx, int dy)
            {
                super.onScrolled(recycler_view, dx, dy);
                offset += dy;
                LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
                int totalItemCount = manager.getItemCount();
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                boolean bar = ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).hasProgressBar();

                if (!loading && totalItemCount > 0 && lastVisibleItem == totalItemCount - 1)
                {
                    if (bar)
                    {
                        loading = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                listAdd();
                            }
                        }, 500);
                    }
                    else
                    {
                        pos = offset;
                        Log.e("pos save", offset + "");
                        if (totalItemCount % 25 == 0)
                            ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).addProgressBar();
                    }
                }
            }
        });

        if (pos != -1)
        {
            final int finalPos = pos;
            recycler_view.post(new Runnable() {
                @Override
                public void run() {
                    recycler_view.scrollTo(0, finalPos);
                }
            });
        }
        swipe_refresh_layout = view.findViewById(R.id.refresh_layout);
        swipe_refresh_layout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                listRefresh();
            }
        });

        if (savedInstanceState == null)
            listInitialize();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", (ArrayList<NewsBrief>)((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).getList());
        outState.putInt("position", offset);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mNewsClickedListener = null;
    }

    void clear()
    {
        loading = false;
        refreshing = false;
        ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).clear();
    }

    void addAll(List<NewsBrief> list)
    {
        ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).removeProgressBar();
        if (list == null || list.size() == 0)
        {
            LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
            if (manager.getItemCount() == 0)
            {
                List<NewsBrief> mList = new ArrayList<>();
                mList.add(NewsShowFragment.nonNews);
                ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).addAll(mList);
            }
            final Toast toast = Toast.makeText(recycler_view.getContext(), R.string.AllNewsFetched, Toast.LENGTH_SHORT);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable()
                    {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 2000);
        }
        else
            ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).addAll(list);
    }

    private void fetchNewsListFail()
    {
        ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).removeProgressBar();
        loading = false;
        final Toast toast = Toast.makeText(recycler_view.getContext(), R.string.FetchingNewsFail, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toast.cancel();
                    }
                }, 1000);

    }

    void setTop()
    {
        recycler_view.smoothScrollToPosition(0);
    }

    void update()
    {
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSuccess(String type, Object data)
    {
        if (type.equals(Operation.LATEST) && data instanceof NewsBriefList)
        {
            swipe_refresh_layout.setRefreshing(false);
            loading = false;
            refreshing = false;
            NewsBriefList list = (NewsBriefList) data;
            addAll(list.list);
        }
        if (type.equals(Operation.SEARCH) && data instanceof NewsBriefList)
        {
            swipe_refresh_layout.setRefreshing(false);
            loading = false;
            refreshing = false;
            NewsBriefList list = (NewsBriefList) data;
            addAll(list.list);
        }
        if (type.equals(Operation.DOWNLOAD) && data instanceof Integer)
        {
            recycler_view.getAdapter().notifyItemChanged((int)data);
        }
    }

    @Override
    public void onFailure(String type, Object data)
    {
        if (type.equals(Operation.LATEST))
        {
            swipe_refresh_layout.setRefreshing(false);
            refreshing = false;
            fetchNewsListFail();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        String type = item.getTitle().toString();
        newsOperate(type);
        return super.onContextItemSelected(item);
    }

    abstract void listInitialize();

    abstract void listAdd();

    abstract void listRefresh();

    @Override
    public void onButtonClicked(String type)
    {
        newsOperate(type);
    }

    private void newsOperate(String type)
    {
        int pos = ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).getNews();
        NewsBrief news = ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).getList().get(pos);
        Operation operation = new Operation(this);
        if (type.equals(getString(R.string.like)) || type.equals(getString(R.string.unlike))) operation.like(news);
        if (type.equals(getString(R.string.download)) || type.equals(getString(R.string.delete)))
        {
            operation.download(news, MyApplication.newsDetail_directory, pos);
        }
        recycler_view.getAdapter().notifyItemChanged(pos);
    }

    interface OnNewsClickedListener
    {
        void onNewsClicked(NewsBrief item);
    }
}

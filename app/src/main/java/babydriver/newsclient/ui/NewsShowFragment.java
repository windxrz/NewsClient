package babydriver.newsclient.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import babydriver.newsclient.controller.MyApplication;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.controller.Operation;

public abstract  class NewsShowFragment extends Fragment
        implements Operation.OnOperationListener, MyNewsRecyclerViewAdapter.OnButtonClickedListener
{
    static NewsBrief nonNews;

    OnNewsClickedListener mNewsClickedListener;

    RecyclerView recycler_view;
    SwipeRefreshLayout swipe_refresh_layout;

    private boolean loading = false;
    private boolean refreshing = false;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        recycler_view = view.findViewById(R.id.recycler_view);
        Context context = recycler_view.getContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(context));
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(new ArrayList<NewsBrief>(), this, mNewsClickedListener, this, this.getActivity()));
        recycler_view.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            boolean last = false;

            @Override
            public void onScrolled(RecyclerView recycler_view, int dx, int dy)
            {
                super.onScrolled(recycler_view, dx, dy);

                LinearLayoutManager manager = (LinearLayoutManager)recycler_view.getLayoutManager();
                int totalItemCount = manager.getItemCount();
                int lastVisibleItem = manager.findLastVisibleItemPosition();
                if (!loading && totalItemCount > 0 && !last && lastVisibleItem == totalItemCount - 1)
                {
                    loading = true;
                    listAdd();
                }
                last = (lastVisibleItem == totalItemCount - 1);
            }
        });

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

        listInitialize();
        return view;
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

    private void addAll(List<NewsBrief> list)
    {
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
            ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).addAll(list);
    }

    private void fetchNewsListFail()
    {
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
        if (type.equals(Operation.PICTURE) && data instanceof Integer)
        {
            int pos = (Integer) data;
            setPicture(pos);
        }
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFailure(String type, Object data)
    {
        if (type.equals(Operation.LATEST))
        {
            swipe_refresh_layout.setRefreshing(false);
            loading = false;
            refreshing = false;
            fetchNewsListFail();
        }
        if (type.equals(Operation.PICTURE) && data instanceof Integer)
        {
            ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).add_fail_img((int)data);
        }
        recycler_view.getAdapter().notifyDataSetChanged();
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
        NewsBrief news = ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).getNews();
        Operation operation = new Operation(this);
        if (type.equals(getString(R.string.like)) || type.equals(getString(R.string.unlike))) operation.like(news.news_ID);
        if (type.equals(getString(R.string.download)) || type.equals(getString(R.string.delete)))
        {
            operation.download(news, getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        }
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    interface OnNewsClickedListener
    {
        void onNewsClicked(NewsBrief item);
    }
}

package babydriver.newsclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.NewsRequester.onRequestListener;
import babydriver.newsclient.model.Operation;

public class NewsShowFragment extends Fragment implements NewsRequester.onRequestListener
{
    static NewsBrief nonNews;
    OnNewsClickedListener mNewsClickedListener;
    onRequestListener<NewsBriefList> mNewsBriefRequestListener;
    onRequestListener<Integer> mBitmapRequestListener;
    RecyclerView recycler_view;
    SwipeRefreshLayout swipe_refresh_layout;
    NewsRequester requester;

    int previousTotal = 0;
    int totalItemCount = 0;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Context context)
    {
        super.onAttach(context);
        nonNews = new NewsBrief(getString(R.string.NonNews));
        if (context instanceof OnNewsClickedListener)
        {
            mNewsClickedListener = (OnNewsClickedListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        mNewsBriefRequestListener = (onRequestListener<NewsBriefList>) this;
        mBitmapRequestListener = (onRequestListener<Integer>) this;
        requester = new NewsRequester();
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
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(new ArrayList<NewsBrief>(), mNewsClickedListener, mBitmapRequestListener, this.getActivity()));
        recycler_view.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        swipe_refresh_layout = view.findViewById(R.id.refresh_layout);

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
        previousTotal = 0;
        totalItemCount = 0;
        ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).clear();
    }

    private void addAll(List<NewsBrief> list)
    {
        swipe_refresh_layout.setRefreshing(false);
        if (list == null || list.size() == 0)
        {
            if (totalItemCount == 0)
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

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        String operation = item.getTitle().toString();
        MyNewsRecyclerViewAdapter adapter = (MyNewsRecyclerViewAdapter)recycler_view.getAdapter();
        if (operation.equals(getString(R.string.like)) || operation.equals(getString(R.string.unlike))) Operation.like(adapter.getNews());
        if (operation.equals(getString(R.string.download)) || operation.equals(getString(R.string.delete))) Operation.download(adapter.getNews());
        recycler_view.getAdapter().notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    interface OnNewsClickedListener
    {
        void onNewsClicked(NewsBrief item);
    }

}

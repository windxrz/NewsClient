package babydriver.newsclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.NewsDetail;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.NewsRequester.OnRequestListener;
import babydriver.newsclient.model.Operation;

public class NewsShowFragment extends Fragment
        implements OnRequestListener, MyNewsRecyclerViewAdapter.OnButtonClickedListener
{
    static NewsBrief nonNews;
    OnNewsClickedListener mNewsClickedListener;
    OnRequestListener<NewsBriefList> mNewsBriefRequestListener;
    OnRequestListener<NewsDetail> mNewsDetailRequestListener;
    OnRequestListener<Integer> mBitmapRequestListener;
    OnRequestListener<String> mDownloadListener;
    RecyclerView recycler_view;
    SwipeRefreshLayout swipe_refresh_layout;
    NewsRequester requester;
    HashMap<String, Operation> operationMap = new HashMap<>();
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
        mNewsBriefRequestListener = (OnRequestListener<NewsBriefList>) this;
        mBitmapRequestListener = (OnRequestListener<Integer>) this;
        mNewsDetailRequestListener = (OnRequestListener<NewsDetail>) this;
        mDownloadListener = (OnRequestListener<String>) this;
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
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(new ArrayList<NewsBrief>(), this, mNewsClickedListener, mBitmapRequestListener, this.getActivity()));
        recycler_view.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        swipe_refresh_layout = view.findViewById(R.id.refresh_layout);

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mNewsClickedListener = null;
        mBitmapRequestListener = null;
        mNewsBriefRequestListener = null;
        mNewsDetailRequestListener = null;
        mDownloadListener = null;
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

    void update()
    {
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    void downloadFail(String id)
    {
        final Toast toast = Toast.makeText(recycler_view.getContext(), R.string.DownloadFail, Toast.LENGTH_SHORT);
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
        operationMap.remove(id);
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSuccess(String type, Object data)
    {
        if (type.equals(NewsRequester.normal))
        {
            if (data instanceof NewsBriefList)
            {
                NewsBriefList list = (NewsBriefList) data;
                addAll(list.list);
            }
            if (data instanceof Integer)
            {
                int pos = (Integer) data;
                setPicture(pos);
            }
        }
        if (type.equals(NewsRequester.download))
        {
            if (data instanceof String)
            {
                Operation op = operationMap.get(data);
                op.finish(true);
                if (op.isFinished())
                {
                    if (op.isSuccess())
                    {
                        operationMap.remove(data);
                        recycler_view.getAdapter().notifyDataSetChanged();
                    }
                    else
                        downloadFail((String) data);
                }
            }
        }
    }

    @Override
    public void onFailure(String info, String id)
    {
        if (info.equals(NewsRequester.download))
        {
            Operation op = operationMap.get(id);
            op.finish(false);
            if (op.isFinished()) downloadFail(id);
        }
        if (info.equals("NewsBriefList")) fetchNewsListFail();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        String type = item.getTitle().toString();
        newsOperate(type);
        return super.onContextItemSelected(item);
    }

    @Override
    public void onButtonClicked(String type)
    {
        newsOperate(type);
    }

    private void newsOperate(String type)
    {
        NewsBrief news = ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).getNews();
        Operation operation = new Operation();
        if (type.equals(getString(R.string.like)) || type.equals(getString(R.string.unlike))) operation.like(news.news_ID);
        if (type.equals(getString(R.string.download)) || type.equals(getString(R.string.delete)))
        {
            operationMap.put(news.news_ID, operation);
            operation.download(news, getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mDownloadListener);
        }
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    interface OnNewsClickedListener
    {
        void onNewsClicked(NewsBrief item);
    }

}

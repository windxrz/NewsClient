package babydriver.newsclient.ui;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import babydriver.newsclient.model.MyNewsRecyclerViewAdapter;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.NewsRequester.onListRequestListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NewsShowFragment extends Fragment
{

    public static final String ARG_NEWS_BRIEF_LIST = "news_brief_list";
    private OnListFragmentInteractionListener mListener;
    private onListRequestListener mRequestListener;
    RecyclerView recycler_view;
    SwipeRefreshLayout swipe_refresh_layout;
    int previousTotal = 0;
    int totalItemCount = 25;

    public NewsShowFragment() {}

    @SuppressWarnings("unused")
    public static NewsShowFragment newInstance(int columnCount)
    {
        return new NewsShowFragment();
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
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(new ArrayList<NewsBrief>(), mListener, this.getActivity()));
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
                        NewsRequester requester = new NewsRequester(mRequestListener);
                        Map<String, Integer> map = new HashMap<>();
                        map.put("pageNo", totalItemCount / 25 + 1);
                        map.put("pageSize", 25);
                        requester.requestLatest(map);
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
        swipe_refresh_layout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    ((MyNewsRecyclerViewAdapter)recycler_view.getAdapter()).clear();
                    NewsRequester requester = new NewsRequester(mRequestListener);
                    Map<String, Integer> map = new HashMap<>();
                    map.put("pageNo", 1);
                    map.put("pageSize", 25);
                    previousTotal = 0;
                    totalItemCount = 25;
                    requester.requestLatest(map);
                }
            });

        NewsRequester requester = new NewsRequester(mRequestListener);
        Map<String, Integer> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 25);
        requester.requestLatest(map);
        return view;
    }


    @Override
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
        if (context instanceof onListRequestListener)
        {
            mRequestListener = (onListRequestListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnRequestListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public void addAll(List<NewsBrief> list)
    {
        swipe_refresh_layout.setRefreshing(false);
        ((MyNewsRecyclerViewAdapter) recycler_view.getAdapter()).addAll(list);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener
    {
        void onListFragmentInteraction(NewsBrief item);
    }
}

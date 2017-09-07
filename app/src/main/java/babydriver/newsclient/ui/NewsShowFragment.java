package babydriver.newsclient.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        Bundle bundle = new Bundle();

        // Set the adapter
        recycler_view = view.findViewById(R.id.recycler_view);
        Context context = recycler_view.getContext();
        recycler_view.setLayoutManager(new LinearLayoutManager(context));
        recycler_view.setAdapter(new MyNewsRecyclerViewAdapter(new ArrayList<NewsBrief>(), mListener));

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

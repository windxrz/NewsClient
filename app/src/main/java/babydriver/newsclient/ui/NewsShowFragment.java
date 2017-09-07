package babydriver.newsclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import babydriver.newsclient.model.MyNewsRecyclerViewAdapter;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.NewsRequester;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NewsShowFragment extends Fragment
{

    public static final String ARG_NEWS_BRIEF_LIST = "news_brief_list";
    private NewsBriefList news_brief_list = new NewsBriefList();
    private OnListFragmentInteractionListener mListener;
    private onRequestListener mRequestListener;
    private NewsRequester requester;
    RecyclerView recyclerView;

    public NewsShowFragment() {}

    @SuppressWarnings("unused")
    public static NewsShowFragment newInstance(int columnCount)
    {
        NewsShowFragment fragment = new NewsShowFragment();
        return fragment;
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
        if (view instanceof RecyclerView)
        {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyNewsRecyclerViewAdapter(news_brief_list.list, mListener));
        }

        requester = new NewsRequester(mRequestListener);
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
        if (context instanceof onRequestListener)
        {
            mRequestListener = (onRequestListener) context;
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

    public void update(NewsBriefList list)
    {
        news_brief_list = list;
        recyclerView.setAdapter(new MyNewsRecyclerViewAdapter(news_brief_list.list, mListener));
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

    public interface onRequestListener
    {
        void onSuccess(NewsBriefList list);
    }
}

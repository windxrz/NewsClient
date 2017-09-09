package babydriver.newsclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.HashMap;
import java.util.Map;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsRequester;

public class SearchFragment extends Fragment
{
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (searchView != null) {
                    searchView.clearFocus(); // 不获取焦点
                }
                NewsRequester newsRequester = new NewsRequester();
                Map<String, Integer> map = new HashMap<String, Integer>();
//                newsRequester.requestSearch(query, map, );
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });
        return view;
    }
}

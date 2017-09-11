package babydriver.newsclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;

import babydriver.newsclient.R;

public class SearchFragment extends Fragment
{
    private SearchView search_view;
    SearchNewsShowFragment search_news_show_fragment;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        search_news_show_fragment = new SearchNewsShowFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        FragmentTransaction traction = getChildFragmentManager().beginTransaction();
        traction.add(R.id.SearchNewsShowFragment, search_news_show_fragment);
        traction.commit();

        search_view = view.findViewById(R.id.searchView);
        search_view.setSubmitButtonEnabled(true);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    if (search_view != null) {
                        search_view.clearFocus();
                        search_news_show_fragment.setKeyword(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText)
                {
                    if (newText.equals("")) search_news_show_fragment.clear();
                    return false;
                }
            });
        return view;
    }
}

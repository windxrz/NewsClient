package babydriver.newsclient.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.model.NewsBriefList;
import babydriver.newsclient.model.NewsRequester;

public class MainActivity extends AppCompatActivity
        implements NewsShowFragment.OnListFragmentInteractionListener,
                   NewsRequester.onRequestListener
{
    public final static String NEWS_ID = "babydriver.newsclient.NEWS_ID";
    HomeFragment home_fragment;
    SearchFragment search_fragment;
    AccountFragment account_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home_fragment = new HomeFragment();
        search_fragment = new SearchFragment();
        account_fragment = new AccountFragment();
        FragmentTransaction traction = getSupportFragmentManager().beginTransaction();
        traction.add(R.id.Fragment, home_fragment);
        traction.hide(home_fragment);
        traction.add(R.id.Fragment, search_fragment);
        traction.hide(search_fragment);
        traction.add(R.id.Fragment, account_fragment);
        traction.hide(account_fragment);
        traction.show(home_fragment);
        traction.commit();

        BottomNavigationView bottom_navigation_view = findViewById(R.id.navigation);
        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int id = item.getItemId();
                Fragment fragment = new Fragment();

                switch (id)
                {
                    case R.id.item_home:
                        fragment = home_fragment;
                        break;
                    case R.id.item_search:
                        fragment = search_fragment;
                        break;
                    case R.id.item_account:
                        fragment = account_fragment;
                        break;
                }
                FragmentManager fragment_manager = getSupportFragmentManager();
                FragmentTransaction transaction = fragment_manager.beginTransaction();
                transaction.hide(home_fragment);
                transaction.hide(search_fragment);
                transaction.hide(account_fragment);
                transaction.show(fragment).commit();
                return true;
            }
        });
    }

    public void onListFragmentInteraction(NewsBrief item)
    {
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra(NEWS_ID, item.news_ID);
        startActivity(intent);
    }

    @Override
    public void onSuccess(Object data)
    {
        if (data instanceof NewsBriefList)
        {
            NewsBriefList list = (NewsBriefList) data;
            home_fragment.news_show_fragment.addAll(list.list);
        }
        if (data instanceof Integer)
        {
            int pos = (Integer)data;
            home_fragment.news_show_fragment.setPicture(pos);
        }
    }
}

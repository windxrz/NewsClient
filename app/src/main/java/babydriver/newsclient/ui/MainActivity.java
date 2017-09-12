package babydriver.newsclient.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBrief;

public class MainActivity extends AppCompatActivity
        implements NewsShowFragment.OnNewsClickedListener
{
    public final static String NEWS_ID = "babydriver.newsclient.NEWS_ID";
    BottomNavigationView bottom_navigation_view;
    HomeFragment home_fragment = null;
    SearchFragment search_fragment = null;
    AccountFragment account_fragment = null;
    private BottomNavigationView.OnNavigationItemSelectedListener listener;
    private SharedPreferences sharedPreferences;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if (key.equals("category_select"))
            {
                home_fragment.refreshTabs();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottom_navigation_view = findViewById(R.id.navigation);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null)
        {
            home_fragment = new HomeFragment();
            search_fragment = new SearchFragment();
            account_fragment = new AccountFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.Fragment, home_fragment, "home_fragment");
            transaction.hide(home_fragment);
            transaction.add(R.id.Fragment, search_fragment, "search_fragment");
            transaction.hide(search_fragment);
            transaction.add(R.id.Fragment, account_fragment, "account_fragment");
            transaction.hide(account_fragment);
            transaction.show(home_fragment);
            transaction.commit();
        }
        else
        {
            home_fragment = (HomeFragment) fragmentManager.findFragmentByTag("home_fragment");
            search_fragment = (SearchFragment) fragmentManager.findFragmentByTag("search_fragment");
            account_fragment = (AccountFragment) fragmentManager.findFragmentByTag("account_fragment");
        }
    }

    @Override
    protected void onResume()
    {
        listener = new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            int home_time = 1;
            int search_time = 0;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int id = item.getItemId();
                Fragment fragment = new Fragment();

                switch (id)
                {
                    case R.id.item_home:
                        home_fragment.home_news_show_fragment.update();
                        search_time = 0;
                        if (home_time == 1)
                        {
                            home_fragment.home_news_show_fragment.setTop();
                        } else
                            home_time++;
                        fragment = home_fragment;
                        break;
                    case R.id.item_search:
                        home_time = 0;
                        search_fragment.search_news_show_fragment.update();
                        if (search_time == 1)
                        {
                            search_fragment.search_news_show_fragment.setTop();
                        } else
                            search_time++;
                        fragment = search_fragment;
                        break;
                    case R.id.item_account:
                        home_time = 0;
                        search_time = 0;
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
        };
        bottom_navigation_view.setOnNavigationItemSelectedListener(listener);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void onNewsClicked(NewsBrief item)
    {
        if (!item.news_ID.equals(""))
        {
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra(NEWS_ID, item.news_ID);
            startActivity(intent);
        }
    }
}

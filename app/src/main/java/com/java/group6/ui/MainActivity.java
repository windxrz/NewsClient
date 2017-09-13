package com.java.group6.ui;

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

import com.java.group6.controller.MyApplication;

import com.java.group6.R;

import com.java.group6.model.NewsBrief;

public class MainActivity extends AppCompatActivity
        implements NewsShowFragment.OnNewsClickedListener
{
    public final static String NEWS_ID = "com.java.group6.NEWS_ID";
    BottomNavigationView bottom_navigation_view;
    HomeFragment home_fragment = null;
    SearchFragment search_fragment = null;
    AccountFragment account_fragment = null;
    private SharedPreferences sharedPreferences;
    int current_fragment;

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
            current_fragment = R.id.item_home;
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
        MyApplication.load(this);
        BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                int id = item.getItemId();
                Fragment fragment = new Fragment();

                switch (id)
                {
                    case R.id.item_home:
                        home_fragment.home_news_show_fragment.update();
                        if (current_fragment == R.id.item_home)
                            home_fragment.home_news_show_fragment.setTop();
                        fragment = home_fragment;
                        current_fragment = R.id.item_home;
                        break;
                    case R.id.item_search:
                        search_fragment.search_news_show_fragment.update();
                        if (current_fragment == R.id.item_search)
                            search_fragment.search_news_show_fragment.setTop();
                        fragment = search_fragment;
                        current_fragment = R.id.item_search;
                        break;
                    case R.id.item_account:
                        fragment = account_fragment;
                        current_fragment = R.id.item_account;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            recreate();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MyApplication.save(this);
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
            MyApplication.read_list.add(item.news_ID);
            intent.putExtra(NEWS_ID, item.news_ID);
            startActivityForResult(intent, 1);
        }
    }
}

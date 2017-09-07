package babydriver.newsclient.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import babydriver.newsclient.model.NewsContent;
import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsRequester;

public class MainActivity extends AppCompatActivity implements NewsShowFragment.OnListFragmentInteractionListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeFragment home_fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.Fragment, home_fragment).commit();

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
                        fragment = new HomeFragment();
                        break;
                    case R.id.item_search:
                        fragment = new SearchFragment();
                        break;
                    case R.id.item_account:
                        fragment = new AccountFragment();
                        break;
                }
                FragmentManager fragment_manager = getSupportFragmentManager();
                FragmentTransaction transaction = fragment_manager.beginTransaction();
                transaction.replace(R.id.Fragment, fragment).commit();
                return true;
            }
        });
    }

    public void onListFragmentInteraction(NewsContent.NewsItem item) {}
}
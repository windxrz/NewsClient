package babydriver.newsclient.ui;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import babydriver.newsclient.R;
import babydriver.newsclient.controller.MyApplication;
import babydriver.newsclient.model.NewsBrief;

import static babydriver.newsclient.ui.MainActivity.NEWS_ID;

public class DownloadsActivity extends AppCompatActivity
        implements NewsShowFragment.OnNewsClickedListener

{
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_downloads);
        Toolbar toolbar = findViewById(R.id.toolbar_content);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.downloads);
        }
        DownloadsNewsShowFragment downloads_news_show_fragment = new DownloadsNewsShowFragment();
        FragmentTransaction traction = getSupportFragmentManager().beginTransaction();
        traction.add(R.id.DownloadsNewsShowFragment, downloads_news_show_fragment, "downloads");
        traction.commit();
    }

    public void onNewsClicked(NewsBrief item)
    {
        if (!item.news_ID.equals(""))
        {
            Intent intent = new Intent(this, ContentActivity.class);
            MyApplication.read_list.add(item.news_ID);
            intent.putExtra(NEWS_ID, item.news_ID);
            startActivity(intent);
        }
    }
}

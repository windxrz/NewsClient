package babydriver.newsclient.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsDetail;
import babydriver.newsclient.model.NewsRequester;

public class ContentActivity extends AppCompatActivity
{
    private TextView textView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String news_ID = intent.getStringExtra(MainActivity.NEWS_ID);
        NewsRequester newsRequester = new NewsRequester(new ContextSetter());
        newsRequester.requestDetail(news_ID);

        textView = findViewById(R.id.contentTextView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ContextSetter implements NewsRequester.onDetailRequestListener
    {
        public void onSuccess(NewsDetail newsDetail)
        {
            int sumLen;
            SpannableStringBuilder content = new SpannableStringBuilder(newsDetail.news_Title);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            RelativeSizeSpan doubleSizeSpan = new RelativeSizeSpan(2.0f);
            content.setSpan(boldSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(doubleSizeSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.append('\n');
            sumLen = content.length();

            content.append(newsDetail.news_Source).append(" ").append(newsDetail.news_Author).append('\n');
            RelativeSizeSpan halfSizeSpan = new RelativeSizeSpan(0.5f);
            content.setSpan(halfSizeSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.append('\n');
//            sumLen += newsDetail.news_Source.length() + newsDetail.news_Author.length() + 2;

            content.append(newsDetail.news_Content);
//            sumLen += newsDetail.news_Content.length();

            textView.setText(content);
        }
    }
}



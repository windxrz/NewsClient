package babydriver.newsclient.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsDetail;
import babydriver.newsclient.model.NewsRequester;

public class ContentActivity extends AppCompatActivity
{
//    private TextView textView;
    private WebView webView;
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
        NewsRequester newsRequester = new NewsRequester();
        newsRequester.requestDetail(news_ID, new ContextSetter());

//        textView = findViewById(R.id.contentTextView);;
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(0);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDefaultFontSize(45);
        webSettings.setMinimumFontSize(12);
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

    private class ContextSetter implements NewsRequester.onRequestListener<NewsDetail>
    {
        public void onSuccess(NewsDetail newsDetail)
        {
            /*
            int sumLen = 0;
            SpannableStringBuilder content = new SpannableStringBuilder();
            content.append(newsDetail.news_Title);
            TypefaceSpan titleFontSpan = new TypefaceSpan("serif");
//            RelativeSizeSpan doubleSizeSpan = new RelativeSizeSpan(2.0f);
            AbsoluteSizeSpan titleSizeSpan = new AbsoluteSizeSpan(25, true);
            content.setSpan(titleFontSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(titleSizeSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.append("\n\n");
            sumLen = content.length();

            SimpleDateFormat ft = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
            content.append("").append(newsDetail.news_Source)
                    .append(" ").append(newsDetail.news_Author)
                    .append(" ").append(newsDetail.news_Journal)
                    .append("\n").append(ft.format(newsDetail.newsTime))
                    .append('\n');
//            RelativeSizeSpan halfSizeSpan = new RelativeSizeSpan(0.5f);
            TypefaceSpan authorFontSpan = new TypefaceSpan("serif");
            AbsoluteSizeSpan authorSizeSpan = new AbsoluteSizeSpan(15, true);
            content.setSpan(authorFontSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(authorSizeSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.append('\n');
            sumLen = content.length();

            content.append(newsDetail.news_Content);
            TypefaceSpan contentFontSpan = new TypefaceSpan("serif");
            AbsoluteSizeSpan contentSizeSpan = new AbsoluteSizeSpan(17, true);
            content.setSpan(contentFontSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setSpan(contentSizeSpan, sumLen, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            sumLen += newsDetail.news_Content.length();

            textView.setText(content);
            */
            String content = "<html><body>" +
                    "<h2 style=\"font-weight:normal\">" + newsDetail.news_Title + "</h2>" +
                    "<div style=\"font-size:80%;color:grey\">" + newsDetail.news_Author + " " +
                    newsDetail.news_Journal + "</div>" +
                    "<hr /><br />" +
                    "<div  style=\"text-align:justify;\">" + newsDetail.news_Content + "</div>" +
                    "<br />" +
                    "<div style=\"font-size:80%;color:grey;text-align:right\">来源：" +
                    newsDetail.news_Source + "</div>" +
                    "</body></html>";
            webView.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
        }
    }

}




package babydriver.newsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsDetail;
import babydriver.newsclient.model.NewsRequester;

public class ContentActivity extends AppCompatActivity implements NewsRequester.onRequestListener
{
    NewsDetail newsDetail = null;
    private WebView webView;

    private final static String placeholder = "file:///android_res/drawable/placeholder.9.png";
    private String newsPath;
    private String content;
    private boolean willPictureShow = false;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String news_ID = intent.getStringExtra(MainActivity.NEWS_ID);
        NewsRequester newsRequester = new NewsRequester();
        newsRequester.requestDetail(news_ID, this);

//        textView = findViewById(R.id.contentTextView);;
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(0);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。
//        webSettings.setTextZoom(2);//设置文本的缩放倍数，默认为 100
//        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8"); //设置编码格式
        webSettings.setDefaultFontSize(45); //设置 WebView 字体的大小，默认大小为 16
        webSettings.setMinimumFontSize(12); //设置 WebView 支持的最小字体大小，默认为 8
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

    private void init()
    {
        File dir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        assert dir != null;
        File newsDir = new File(dir.getPath() + "/" + newsDetail.news_ID);
        if (!newsDir.exists())
            willPictureShow = newsDir.mkdir();
        newsPath = newsDir.getPath();

        content = "<html>" + "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\">" +
                "<style>img{max-width:100% !important; width:100%; height:auto;}</style>" +
                "</head><body>" +
                "<h2 style=\"font-weight:normal\">" + newsDetail.news_Title + "</h2>" +
                "<div style=\"font-size:80%;color:grey\">" + newsDetail.news_Author + " " +
                newsDetail.news_Journal + "</div>" +
                "<hr /><br />";
        int i = 0;
        for (String ignored : newsDetail.newsPictures)
        {
            content += "<p><img src=\"" + placeholder + "\" alt=\"" + i + "\"/></p>";
            i++;
        }
        content +=
                "<div  style=\"text-align:justify;\">" + newsDetail.news_Content + "</div>" +
                "<br />" +
                "<div style=\"font-size:80%;color:grey;text-align:right\">来源：" +
                newsDetail.news_Source + "</div>" +
                "</body></html>";
    }

    private void setContent()
    {
        webView.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
    }

    @SuppressWarnings("unchecked")
    private void updatePics()
    {
        if (!willPictureShow) return;
        int i = 0;
        for (String picUrl : newsDetail.newsPictures)
        {
            File picFile = new File(newsPath + "/" + i);
            if (!picFile.isFile())
                new NewsRequester().requestPicture(picUrl, picFile.getPath(), i, this);
            else
                updateSinglePic(picFile.getPath(), i);
            i++;
        }
    }

    private void updateSinglePic(String picDir, int num)
    {
        if (!willPictureShow) return;
        content = content.replace("<img src=\"" + placeholder + "\" alt=\"" + num + "\"/>",
                "<img src=\"" + picDir + "\" alt=\"" + num + "\"/>");
        setContent();
    }

    @Override
    public void onSuccess(Object data)
    {
        if (data instanceof NewsDetail)
        {
            newsDetail = (NewsDetail) data;
            init();
            setContent();
            updatePics();
        }
        else if (data instanceof Integer)
        {
            int i = (int)data;
            updateSinglePic(newsPath + "/" + i, i);
        }
    }

    @Override
    public void onFailure(String info)
    {

    }

}




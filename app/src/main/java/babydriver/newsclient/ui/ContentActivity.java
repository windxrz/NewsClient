package babydriver.newsclient.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import babydriver.newsclient.R;
import babydriver.newsclient.controller.MyApplication;
import babydriver.newsclient.model.NewsDetail;
import babydriver.newsclient.controller.Operation;

public class ContentActivity extends AppCompatActivity implements Operation.OnOperationListener
{
    NewsDetail newsDetail = null;
    private WebView webView;

    private final static String placeholder = "file:///android_res/drawable/placeholder.9.png";
    private String newsPath;
    private String content;
    private boolean willPictureShow = false;

    @Override
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
//        textView = findViewById(R.id.contentTextView);;
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(0);
        webView.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webSettings.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
//        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。
//        webSettings.setTextZoom(2);//设置文本的缩放倍数，默认为 100
//        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8"); //设置编码格式
        webSettings.setDefaultFontSize(16); //设置 WebView 字体的大小，默认大小为 16
        webSettings.setMinimumFontSize(12); //设置 WebView 支持的最小字体大小，默认为 8

        Intent intent = getIntent();
        String news_ID = intent.getStringExtra(MainActivity.NEWS_ID);
        boolean f = false;
        if (Operation.isDownloaded(news_ID))
        {
            try
            {
                FileInputStream fi = new FileInputStream(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + news_ID + "/detail.txt");
                ObjectInputStream si = new ObjectInputStream(fi);
                newsDetail = (NewsDetail)si.readObject();
                f = true;
            } catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        if (!f)
        {
            new Operation(this).requestDetail(news_ID);
        }
        else
        {
            init(getNewsDirectory());
            setContent();
            updatePics();
        }
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

    File getNewsDirectory()
    {
        if (Operation.isDownloaded(newsDetail.news_ID))
        {
            File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            assert dir != null;
            return new File(dir.getPath() + "/" + newsDetail.news_ID);
        }
        else
        {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            assert dir != null;
            return new File(dir.getPath() + "/" + newsDetail.news_ID + "_pics");
        }
    }

    private void init(File newsDir)
    {

        willPictureShow = MyApplication.isPreviewShowPicture && (newsDir.isDirectory() || newsDir.mkdir());
//        if (!newsDir.exists())
//            willPictureShow = newsDir.mkdir();
        newsPath = newsDir.getPath();

        content = "<html>" + "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\">" +
                "<style>img{max-width:100% !important; width:100%; height:auto;}</style>" +
                "</head><body>" +
                "<h2 style=\"font-weight:normal\">" + newsDetail.news_Title + "</h2>" +
                "<div style=\"font-size:80%;color:grey\">" + newsDetail.news_Author + " " +
                newsDetail.news_Journal + "</div>" +
                "<hr />";
        if (willPictureShow)
        {
            int i = 0;
            for (String ignored : newsDetail.newsPictures)
            {
                content += "<p><img src=\"" + placeholder + "\" alt=\"" + i + "\"/></p>";
                i++;
            }
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

    private void updatePics()
    {
        if (!willPictureShow) return;
        int i = 0;
        for (String picUrl : newsDetail.newsPictures)
        {
            String suffix = "";
            Pattern p = Pattern.compile("\\.[^\\.]+$");
            Matcher m = p.matcher(picUrl);
            if (m.find())
                suffix = m.group();
            File picFile = new File(newsPath + "/" + i + suffix);
            if (!picFile.isFile())
                new Operation(this).requestPicture(picUrl, picFile.getPath(), i, new BitmapFactory.Options());
            else
                updateSinglePic(picFile.getPath(), i);
            i++;
        }
    }

    private void updateSinglePic(String picDir, int num)
    {
        if (!willPictureShow) return;
        content = content.replace("<img src=\"" + placeholder + "\" alt=\"" + num + "\"/>",
                "<img src=\"" + "file://" + picDir + "\" alt=\"" + num + "\"/>");
        setContent();
    }

    @Override
    public void onSuccess(String type, Object data)
    {
        if (type.equals(Operation.DETAIL) && data instanceof NewsDetail)
        {
            newsDetail = (NewsDetail) data;
            init(getNewsDirectory());
            setContent();
            updatePics();
        }
        if (type.equals(Operation.PICTURE) && data instanceof Integer)
        {
            int i = (int) data;
            String picUrl = newsDetail.newsPictures.get(i);
            String suffix = "";
            Pattern p = Pattern.compile("\\.[^\\.]+$");
            Matcher m = p.matcher(picUrl);
            if (m.find())
                suffix = m.group();
            updateSinglePic(newsPath + "/" + i + suffix, i);
        }
    }

    @Override
    public void onFailure(String info, Object detail)
    {

    }

}




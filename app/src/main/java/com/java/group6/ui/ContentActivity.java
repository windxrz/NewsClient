package com.java.group6.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.java.group6.controller.MyApplication;
import com.java.group6.controller.Operation;
import com.java.group6.model.NewsDetail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.java.group6.R;

public class ContentActivity extends AppCompatActivity implements Operation.OnOperationListener
{
    NewsDetail newsDetail = null;
    private WebView webView;

    private final static String placeholder = "file:///android_res/drawable/placeholder.9.png";
    private String newsPath;
    private String content;
    private boolean willPictureShow = false;
    private boolean isDownloading = false;
    private boolean startedSpeaking = false;

    private SpeechSynthesizer mTts;
    private SynthesizerListener mSynListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_content_toolbar, menu);
        menu.add(0, 1, 0, getString(R.string.like));
        menu.add(0, 2, 0, getString(R.string.download));
        menu.add(0, 3, 0, getString(R.string.share));
        menu.add(0, 4, 0, getString(R.string.tts_start));
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            menu.add(0, 5, 0, getString(R.string.toDay));
        else
            menu.add(0, 5, 0, getString(R.string.toNight));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (newsDetail != null)
        {
            MenuItem likeItem = menu.findItem(1);
            MenuItem downloadItem = menu.findItem(2);
            MenuItem ttsItem = menu.findItem(4);
            MenuItem modeItem = menu.findItem(5);

            //  like/unlike
            if (Operation.isFavorite(newsDetail.news_ID))
            {
                likeItem.setTitle(getString(R.string.unlike));
            }
            else
            {
                likeItem.setTitle(getString(R.string.like));
            }

            //  download/delete
            if (Operation.isDownloaded(newsDetail.news_ID))
            {
                isDownloading = false;
                downloadItem.setTitle(getString(R.string.delete));
            }
            else if (Operation.isDownloading(newsDetail.news_ID))
            {
                isDownloading = true;
                SpannableString downloading = new SpannableString(getString(R.string.downloading));
                downloading.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, downloading.length(), 0);
                downloadItem.setTitle(downloading);
            }
            else
            {
                isDownloading = false;
                downloadItem.setTitle(getString(R.string.download));
            }

            //  TTS
            if (startedSpeaking)
            {
                ttsItem.setTitle(getString(R.string.tts_stop));
            }
            else
            {
                ttsItem.setTitle(getString(R.string.tts_start));
            }

            //  night/day
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            {
                modeItem.setTitle(getString(R.string.toDay));
            }
            else
            {
                modeItem.setTitle(getString(R.string.toNight));
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content);
        Toolbar toolbar = findViewById(R.id.toolbar_content);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.Content);
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
                FileInputStream fi = new FileInputStream(MyApplication.newsDetail_directory + "/" + news_ID + "/detail.txt");
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
    public void onResume()
    {
        super.onResume();
        startedSpeaking = false;
        mTts = SpeechSynthesizer.createSynthesizer(getApplicationContext(), null);
        mSynListener = new SynthesizerListener()
        {
            @Override
            public void onSpeakBegin()
            {
                startedSpeaking = true;
            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s)
            {
            }

            @Override
            public void onSpeakPaused()
            {
                startedSpeaking = false;
            }

            @Override
            public void onSpeakResumed()
            {
                startedSpeaking = true;
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2)
            {
            }

            @Override
            public void onCompleted(SpeechError speechError)
            {
                startedSpeaking = false;
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle)
            {
            }
        };
    }

    @Override
    public void onPause()
    {
        mTts.stopSpeaking();
        mTts.destroy();
        startedSpeaking = true;
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_OK, null);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Operation operation = new Operation(this);
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, null);
                finish();
                break;
            case 1:
                operation.like(newsDetail);
                break;
            case 2:
                if (!isDownloading)
                {
                    operation.download(newsDetail, MyApplication.newsDetail_directory, 0);
                }
                break;
            case 4:
                if (!startedSpeaking)
                {
                    startTTS();
                }
                else
                {
                    stopTTS();
                }
                break;
            case 5:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("night_switch", false);
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("night_switch", true);
                }
                editor.apply();
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startTTS()
    {
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaofeng");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTts.startSpeaking(newsDetail.news_Title + "! " + newsDetail.pureContent, mSynListener);
        startedSpeaking = true;
    }

    private void stopTTS()
    {
        mTts.stopSpeaking();
        startedSpeaking = false;
    }

    File getNewsDirectory()
    {
        if (Operation.isDownloaded(newsDetail.news_ID))
        {
            return new File(MyApplication.newsDetail_directory + "/" + newsDetail.news_ID);
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
                "<meta name=\"viewport\" content=\"width=device-width\">";

        String dateTime = "";
        if (newsDetail.newsTime != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            dateTime = sdf.format(newsDetail.newsTime);
        }

        String textColor = "black", linkColor = "#4169E1";
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            textColor = "silver";
            linkColor = "white";
        }

        content +=
                "<style type=\"text/css\">" +
                "img{max-width:100% !important; width:100%; height:auto;}" +
                "h2{color:" + textColor + "}" +
                "p{color:" + textColor +"; text-align:justify}" +
                "a{color:" + linkColor +"}" +
                "a:link {text-decoration: underline}" +
                "a:active {text-decoration:blink}" +
                "a:hover {text-decoration:underline}" +
                "a:visited {text-decoration: underline}" +
                "</style>" +
                "</head><body>" +
                "<h2 style=\"font-weight:normal\">" + newsDetail.news_Title + "</h2>" +
                "<div style=\"font-size:80%;color:grey\">" + newsDetail.news_Author + " " +
                newsDetail.news_Journal + "</div>" +
                "<div style=\"font-size:80%;color:grey\">" + dateTime +
                "</div>" +
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
                newsDetail.news_Content +
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
            Pattern p = Pattern.compile("\\.[^.]+$");
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
//            getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
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




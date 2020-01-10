package cn.com.hesc.hybridev2;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import static android.webkit.WebSettings.LOAD_NO_CACHE;

public class ItemActivity extends AppCompatActivity {

    private WebView webView;
    private Context mContext;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        mContext = this;

        url = getIntent().getExtras().getString("url","");

        initView();
    }

    private void initView() {
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}

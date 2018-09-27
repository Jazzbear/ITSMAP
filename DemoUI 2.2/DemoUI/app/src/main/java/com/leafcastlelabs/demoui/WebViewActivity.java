package com.leafcastlelabs.demoui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


public class WebViewActivity extends Activity {

    private Button btnLink1;
    private Button btnLink2;
    private Button btnLink3;

    private WebView webView;

    private Button btnOk;
    private Button btnCancel;

    private String currentPage = "https://www.orbitlab.dk"; //default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if(savedInstanceState!=null) {
            String current = savedInstanceState.getString("current_page");
            if (current != null) {
                currentPage = current;
            }
        }

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new customWebViewClient());

        btnLink1 = (Button)findViewById(R.id.btnLink1);
        btnLink1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebUrl("https://www.orbitlab.dk");
            }
        });

        btnLink2 = (Button)findViewById(R.id.btnLink2);
        btnLink2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebUrl("http://developer.android.com");
            }
        });

        btnLink3 = (Button)findViewById(R.id.btnLink3);
        btnLink3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebUrl("http://developer.android.com/guide/");
            }
        });

        btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(RESULT_OK, data);
                finish();
            }
        });

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        loadWebUrl(currentPage);
    }

    private void loadWebUrl(String url){

        currentPage = url;
        this.webView.loadUrl(url);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        currentPage = webView.getUrl();
        outState.putString("current_page", currentPage);

        super.onSaveInstanceState(outState);
    }

    private class customWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String host = Uri.parse(url).getHost();

            if(host.equals("developer.android.com")){
                return false;
            } else if(host.equals("orbitlab.dk")){
                return false;
            } else if(host.equals("google.com")){
                return false;
            } else if(host.equals("google.dk")){
                return false;
            }


            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}

package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.os.Bundle;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

public class WebViewActivity extends Activity implements ObservableScrollViewCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ObservableWebView scrollable = (ObservableWebView) findViewById(R.id.scrollable);
        scrollable.setScrollViewCallbacks(this);
        scrollable.loadUrl("file:///android_asset/lipsum.html");
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}

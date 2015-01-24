package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.os.Bundle;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class ListViewActivity extends Activity implements ObservableScrollViewCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        ObservableListView scrollable = (ObservableListView) findViewById(R.id.scrollable);
        scrollable.setScrollViewCallbacks(this);
        UiTestUtils.setDummyData(this, scrollable);
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

package com.github.ksoichiro.android.observablescrollview;

import android.app.Activity;
import android.os.Bundle;

public class ListViewActivity extends Activity implements ObservableScrollViewCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.github.ksoichiro.android.observablescrollview.test.R.layout.activity_listview);
        UiTestUtils.setDummyData(this, (ObservableListView) findViewById(com.github.ksoichiro.android.observablescrollview.test.R.id.scrollable));
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

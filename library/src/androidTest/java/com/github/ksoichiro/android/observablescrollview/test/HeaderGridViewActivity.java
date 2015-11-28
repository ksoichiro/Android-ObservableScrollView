package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class HeaderGridViewActivity extends Activity implements ObservableScrollViewCallbacks {

    public View headerView;
    public View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);
        ObservableGridView scrollable = (ObservableGridView) findViewById(R.id.scrollable);
        // Set padding view for GridView. This is the flexible space.
        headerView = new View(this);
        final int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                flexibleSpaceImageHeight);
        headerView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        headerView.setClickable(true);

        scrollable.addHeaderView(headerView);

        // Footer is also available.
        footerView = new View(this);
        FrameLayout.LayoutParams lpf = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            flexibleSpaceImageHeight);
        footerView.setLayoutParams(lpf);
        scrollable.addFooterView(footerView);

        scrollable.setScrollViewCallbacks(this);
        UiTestUtils.setDummyData(this, scrollable);
        scrollable.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
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

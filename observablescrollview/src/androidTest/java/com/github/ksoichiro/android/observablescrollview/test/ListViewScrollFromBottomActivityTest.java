package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;

public class ListViewScrollFromBottomActivityTest extends ActivityInstrumentationTestCase2<ListViewScrollFromBottomActivity> {

    private Activity activity;
    private ObservableListView scrollable;

    public ListViewScrollFromBottomActivityTest() {
        super(ListViewScrollFromBottomActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableListView) activity.findViewById(R.id.scrollable);
    }

    public void testScroll() throws Throwable {
        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();
    }
}

package com.github.ksoichiro.android.observablescrollview.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

public class ScrollViewActivityTest extends ActivityInstrumentationTestCase2<ScrollViewActivity> {

    private ObservableScrollView scrollable;

    public ScrollViewActivityTest() {
        super(ScrollViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        ScrollViewActivity activity = getActivity();
        scrollable = (ObservableScrollView) activity.findViewById(R.id.scrollable);
    }

    public void testScroll() throws Throwable {
        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

}

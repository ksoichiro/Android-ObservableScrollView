package com.github.ksoichiro.android.observablescrollview;

import android.test.ActivityInstrumentationTestCase2;

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
        scrollable = (ObservableScrollView) activity.findViewById(com.github.ksoichiro.android.observablescrollview.test.R.id.scrollable);
    }

    public void testScroll() throws Throwable {
        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

}

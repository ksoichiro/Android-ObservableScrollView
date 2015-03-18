package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

public class TouchInterceptionScrollViewActivityTest extends ActivityInstrumentationTestCase2<TouchInterceptionScrollViewActivity> {

    private Activity activity;
    private ObservableScrollView scrollable;

    public TouchInterceptionScrollViewActivityTest() {
        super(TouchInterceptionScrollViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableScrollView) activity.findViewById(R.id.scrollable);
    }

    public void testScroll() throws Throwable {
        TouchUtils.touchAndCancelView(this, scrollable);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

    public void testSaveAndRestoreInstanceState() throws Throwable {
        UiTestUtils.saveAndRestoreInstanceState(this, activity);
        testScroll();
    }
}

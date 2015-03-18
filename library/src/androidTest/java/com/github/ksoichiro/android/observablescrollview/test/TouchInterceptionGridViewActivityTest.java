package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;

public class TouchInterceptionGridViewActivityTest extends ActivityInstrumentationTestCase2<TouchInterceptionGridViewActivity> {

    private Activity activity;
    private ObservableGridView scrollable;

    public TouchInterceptionGridViewActivityTest() {
        super(TouchInterceptionGridViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableGridView) activity.findViewById(R.id.scrollable);
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

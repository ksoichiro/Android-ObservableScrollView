package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;

public class GridViewActivityTest extends ActivityInstrumentationTestCase2<GridViewActivity> {

    private Activity activity;
    private ObservableGridView scrollable;

    public GridViewActivityTest() {
        super(GridViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableGridView) activity.findViewById(R.id.scrollable);
    }

    public void testInitialize() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ObservableGridView(activity);
                new ObservableGridView(activity, null, 0);
            }
        });
    }

    public void testScroll() throws Throwable {
        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

    public void testSaveAndRestoreInstanceState() throws Throwable {
        UiTestUtils.saveAndRestoreInstanceState(this, activity);
        testScroll();
    }

    public void testScrollVerticallyTo() throws Throwable {
        final DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollable.scrollVerticallyTo((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, metrics));
            }
        });
        getInstrumentation().waitForIdleSync();

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollable.scrollVerticallyTo(0);
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}

package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;

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

    public void testInitialize() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                new TouchInterceptionFrameLayout(activity);
                new TouchInterceptionFrameLayout(activity, null, 0);
                if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                    new TouchInterceptionFrameLayout(activity, null, 0, 0);
                }
            }
        });
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

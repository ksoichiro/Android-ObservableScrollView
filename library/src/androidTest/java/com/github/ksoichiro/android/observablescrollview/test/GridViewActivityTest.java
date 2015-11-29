package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class GridViewActivityTest extends ActivityInstrumentationTestCase2<GridViewActivity> {

    private Activity activity;
    private ObservableGridView scrollable;
    private int[] callbackCounter;

    public GridViewActivityTest() {
        super(GridViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableGridView) activity.findViewById(R.id.scrollable);
        callbackCounter = new int[2];
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

    public void testNoCallbacks() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollable = (ObservableGridView) activity.findViewById(R.id.scrollable);
                scrollable.setScrollViewCallbacks(null);
            }
        });
        testScroll();
    }

    public void testCallbacks() throws Throwable {
        final ObservableScrollViewCallbacks[] callbacks = new ObservableScrollViewCallbacks[2];
        callbackCounter[0] = 0;
        callbackCounter[1] = 0;
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollable = (ObservableGridView) activity.findViewById(R.id.scrollable);
                callbacks[0] = new ObservableScrollViewCallbacks() {
                    @Override
                    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                        callbackCounter[0]++;
                    }

                    @Override
                    public void onDownMotionEvent() {
                    }

                    @Override
                    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                    }
                };
                scrollable.addScrollViewCallbacks(callbacks[0]);
                callbacks[1] = new ObservableScrollViewCallbacks() {
                    @Override
                    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                        callbackCounter[1]++;
                    }

                    @Override
                    public void onDownMotionEvent() {
                    }

                    @Override
                    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                    }
                };
                scrollable.addScrollViewCallbacks(callbacks[1]);
            }
        });
        testScroll();
        // Assert that all the callbacks are enabled and get called.
        assertTrue(0 < callbackCounter[0]);
        assertTrue(0 < callbackCounter[1]);

        // Remove one of the callbacks and scroll again to assert it's really removed.
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollable.removeScrollViewCallbacks(callbacks[0]);
            }
        });
        callbackCounter[0] = 0;
        callbackCounter[1] = 0;
        testScroll();
        assertTrue(0 == callbackCounter[0]);
        assertTrue(0 < callbackCounter[1]);

        // Clear all callbacks and assert they're really removed.
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollable.clearScrollViewCallbacks();
            }
        });
        callbackCounter[0] = 0;
        callbackCounter[1] = 0;
        testScroll();
        assertTrue(0 == callbackCounter[0]);
        assertTrue(0 == callbackCounter[1]);
    }

    public void testCannotAddHeaderOrFooterWhenAdapterIsAlreadySet() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    View view = new View(activity);
                    final int flexibleSpaceImageHeight = activity.getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, flexibleSpaceImageHeight);
                    view.setLayoutParams(lp);
                    view.setClickable(true);
                    scrollable.addHeaderView(view);
                    fail();
                } catch (IllegalStateException ignore) {
                }

                try {
                    View view = new View(activity);
                    final int flexibleSpaceImageHeight = activity.getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
                    FrameLayout.LayoutParams lpf = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, flexibleSpaceImageHeight);
                    view.setLayoutParams(lpf);
                    scrollable.addFooterView(view);
                    fail();
                } catch (IllegalStateException ignore) {
                }
            }
        });
    }
}

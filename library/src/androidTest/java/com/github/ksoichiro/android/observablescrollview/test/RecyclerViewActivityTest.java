package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class RecyclerViewActivityTest extends ActivityInstrumentationTestCase2<RecyclerViewActivity> {

    private Activity activity;
    private ObservableRecyclerView scrollable;
    private int[] callbackCounter;

    public RecyclerViewActivityTest() {
        super(RecyclerViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableRecyclerView) activity.findViewById(R.id.scrollable);
        callbackCounter = new int[2];
        getInstrumentation().waitForIdleSync();
    }

    public void testInitialize() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ObservableRecyclerView(activity);
                new ObservableRecyclerView(activity, null, 0);
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
                scrollable = (ObservableRecyclerView) activity.findViewById(R.id.scrollable);
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
                scrollable = (ObservableRecyclerView) activity.findViewById(R.id.scrollable);
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
}

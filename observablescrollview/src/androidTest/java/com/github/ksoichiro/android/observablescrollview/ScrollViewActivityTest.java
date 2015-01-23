package com.github.ksoichiro.android.observablescrollview;

import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.TypedValue;
import android.view.View;

public class ScrollViewActivityTest extends ActivityInstrumentationTestCase2<ScrollViewActivity> {

    private ScrollViewActivity activity;
    private ObservableScrollView scrollable;

    public ScrollViewActivityTest() {
        super(ScrollViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableScrollView) activity.findViewById(com.github.ksoichiro.android.observablescrollview.test.R.id.scrollable);
    }

    public void testScroll() throws Throwable {
        swipeVertically(this, scrollable, Direction.UP);
        getInstrumentation().waitForIdleSync();

        swipeVertically(this, scrollable, Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

    private void swipeVertically(InstrumentationTestCase test, View v, Direction direction) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);

        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();

        float distanceFromEdge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                activity.getResources().getDisplayMetrics());
        float x = xy[0] + (viewWidth / 2.0f);
        float yStart = xy[1] + ((direction == Direction.UP) ? (viewHeight - distanceFromEdge) : distanceFromEdge);
        float yEnd = xy[1] + ((direction == Direction.UP) ? distanceFromEdge : (viewHeight - distanceFromEdge));

        TouchUtils.drag(test, x, x, yStart, yEnd, 100);
    }

    public enum Direction {
        UP, DOWN
    }
}

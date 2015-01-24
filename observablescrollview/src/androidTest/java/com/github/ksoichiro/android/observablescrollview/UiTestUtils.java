package com.github.ksoichiro.android.observablescrollview;

import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.TypedValue;
import android.view.View;

public class UiTestUtils {

    public enum Direction {
        UP, DOWN
    }

    private UiTestUtils() {
    }

    public static void swipeVertically(InstrumentationTestCase test, View v, Direction direction) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);

        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();

        float distanceFromEdge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                v.getResources().getDisplayMetrics());
        float x = xy[0] + (viewWidth / 2.0f);
        float yStart = xy[1] + ((direction == Direction.UP) ? (viewHeight - distanceFromEdge) : distanceFromEdge);
        float yEnd = xy[1] + ((direction == Direction.UP) ? distanceFromEdge : (viewHeight - distanceFromEdge));

        TouchUtils.drag(test, x, x, yStart, yEnd, 100);
    }

}

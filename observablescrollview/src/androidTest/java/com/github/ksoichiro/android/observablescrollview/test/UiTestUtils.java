package com.github.ksoichiro.android.observablescrollview.test;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class UiTestUtils {

    private static final int NUM_OF_ITEMS = 100;
    private static final int NUM_OF_ITEMS_FEW = 3;

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

    public static ArrayList<String> getDummyData() {
        return getDummyData(NUM_OF_ITEMS);
    }

    public static ArrayList<String> getDummyData(int num) {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + i);
        }
        return items;
    }

    public static void setDummyData(Context context, ListView listView) {
        setDummyData(context, listView, NUM_OF_ITEMS);
    }

    public static void setDummyDataFew(Context context, ListView listView) {
        setDummyData(context, listView, NUM_OF_ITEMS_FEW);
    }

    public static void setDummyData(Context context, ListView listView, int num) {
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getDummyData(num)));
    }
}

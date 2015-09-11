package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

public class UiTestUtils {

    private static final int NUM_OF_ITEMS = 100;
    private static final int NUM_OF_ITEMS_FEW = 3;
    private static final int DRAG_STEP_COUNT = 50;

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private UiTestUtils() {
    }

    public static void saveAndRestoreInstanceState(final InstrumentationTestCase test, final Activity activity) throws Throwable {
        test.runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bundle outState = new Bundle();
                test.getInstrumentation().callActivityOnSaveInstanceState(activity, outState);
                test.getInstrumentation().callActivityOnPause(activity);
                test.getInstrumentation().callActivityOnResume(activity);
                test.getInstrumentation().callActivityOnRestoreInstanceState(activity, outState);
            }
        });
        test.getInstrumentation().waitForIdleSync();
    }

    public static void swipeHorizontally(InstrumentationTestCase test, View v, Direction direction) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);

        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();

        float distanceFromEdge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                v.getResources().getDisplayMetrics());
        float xStart = xy[0] + ((direction == Direction.LEFT) ? (viewWidth - distanceFromEdge) : distanceFromEdge);
        float xEnd = xy[0] + ((direction == Direction.LEFT) ? distanceFromEdge : (viewWidth - distanceFromEdge));
        float y = xy[1] + (viewHeight / 2.0f);

        TouchUtils.drag(test, xStart, xEnd, y, y, DRAG_STEP_COUNT);
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

        TouchUtils.drag(test, x, x, yStart, yEnd, DRAG_STEP_COUNT);
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

    public static void setDummyData(Context context, GridView gridView) {
        gridView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getDummyData()));
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

    public static void setDummyDataWithHeader(Context context, ListView listView, View headerView) {
        listView.addHeaderView(headerView);
        setDummyData(context, listView);
    }

    public static void setDummyData(Context context, RecyclerView recyclerView) {
        setDummyData(context, recyclerView, NUM_OF_ITEMS);
    }

    public static void setDummyDataFew(Context context, RecyclerView recyclerView) {
        setDummyData(context, recyclerView, NUM_OF_ITEMS_FEW);
    }

    public static void setDummyData(Context context, RecyclerView recyclerView, int num) {
        recyclerView.setAdapter(new SimpleRecyclerAdapter(context, getDummyData(num)));
    }

    public static void setDummyDataWithHeader(Context context, RecyclerView recyclerView, View headerView) {
        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(context, getDummyData(), headerView));
    }
}

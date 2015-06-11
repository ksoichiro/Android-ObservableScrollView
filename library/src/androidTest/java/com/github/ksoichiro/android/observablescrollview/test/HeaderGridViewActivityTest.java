package com.github.ksoichiro.android.observablescrollview.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;

import java.util.ArrayList;

public class HeaderGridViewActivityTest extends ActivityInstrumentationTestCase2<HeaderGridViewActivity> {

    private HeaderGridViewActivity activity;
    private ObservableGridView scrollable;

    public HeaderGridViewActivityTest() {
        super(HeaderGridViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableGridView) activity.findViewById(R.id.scrollable);
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

    public void testHeaderViewFeatures() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                assertEquals(1, scrollable.getHeaderViewCount());
                ListAdapter adapter = scrollable.getAdapter();
                assertTrue(adapter instanceof ObservableGridView.HeaderViewGridAdapter);
                ObservableGridView.HeaderViewGridAdapter hvgAdapter = (ObservableGridView.HeaderViewGridAdapter) adapter;
                assertEquals(1, hvgAdapter.getHeadersCount());
                assertNotNull(hvgAdapter.getWrappedAdapter());
                assertTrue(hvgAdapter.areAllItemsEnabled());
                assertFalse(hvgAdapter.isEmpty());
                Object data = hvgAdapter.getItem(0);
                assertNull(data);
                assertNotNull(hvgAdapter.getView(0, null, scrollable));
                assertNotNull(hvgAdapter.getFilter());
                assertTrue(scrollable.removeHeaderView(activity.headerView));
                assertEquals(0, scrollable.getHeaderViewCount());
                assertEquals(0, hvgAdapter.getHeadersCount());
                assertFalse(scrollable.removeHeaderView(activity.headerView));

                activity.headerView = new View(activity);
                final int flexibleSpaceImageHeight = activity.getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        flexibleSpaceImageHeight);
                activity.headerView.setLayoutParams(lp);

                // This is required to disable header's list selector effect
                activity.headerView.setClickable(true);

                scrollable.addHeaderView(activity.headerView);
            }
        });
    }

    public void testHeaderViewGridExceptions() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    new ObservableGridView.HeaderViewGridAdapter(null, null);
                    fail();
                } catch (IllegalArgumentException e) {
                    assertEquals("headerViewInfos cannot be null", e.getMessage());
                }
                ListAdapter adapter = scrollable.getAdapter();
                ObservableGridView.HeaderViewGridAdapter hvgAdapter = (ObservableGridView.HeaderViewGridAdapter) adapter;
                try {
                    hvgAdapter.setNumColumns(0);
                    fail();
                } catch (IllegalArgumentException e) {
                    assertEquals("Number of columns must be 1 or more", e.getMessage());
                }
                ArrayList<ObservableGridView.FixedViewInfo> headerViewInfos = new ArrayList<>();
                ObservableGridView.HeaderViewGridAdapter adapter1 = new ObservableGridView.HeaderViewGridAdapter(headerViewInfos, null);
                assertTrue(adapter1.isEmpty());
                try {
                    adapter1.isEnabled(-1);
                    fail();
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
                try {
                    adapter1.getItem(-1);
                    fail();
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
                try {
                    adapter1.getView(0, null, null);
                    fail();
                } catch (IllegalArgumentException e) {
                    assertEquals("Parent cannot be null", e.getMessage());
                }
                try {
                    adapter1.getView(-1, null, scrollable);
                    fail();
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
            }
        });
    }
}

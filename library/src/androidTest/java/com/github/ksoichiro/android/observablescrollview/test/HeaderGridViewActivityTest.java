package com.github.ksoichiro.android.observablescrollview.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                assertEquals(1, scrollable.getFooterViewCount());
                ListAdapter adapter = scrollable.getAdapter();
                assertTrue(adapter instanceof ObservableGridView.HeaderViewGridAdapter);
                ObservableGridView.HeaderViewGridAdapter hvgAdapter = (ObservableGridView.HeaderViewGridAdapter) adapter;
                assertEquals(1, hvgAdapter.getHeadersCount());
                assertEquals(1, hvgAdapter.getFootersCount());
                assertNotNull(hvgAdapter.getWrappedAdapter());
                assertTrue(hvgAdapter.areAllItemsEnabled());
                assertFalse(hvgAdapter.isEmpty());
                Object data = hvgAdapter.getItem(0);
                assertNull(data);
                assertNotNull(hvgAdapter.getView(0, null, scrollable));
                assertNotNull(hvgAdapter.getView(1, null, scrollable));
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

                assertEquals(100/* items */ + 2/* header */ + 2/* footer */, hvgAdapter.getCount());
                assertEquals(1, hvgAdapter.getHeadersCount());
                assertEquals(2, hvgAdapter.getNumColumns());
                // If the header is added by addHeader(View),
                // HeaderViewGridAdapter doesn't contain any associated data.
                // headerData does NOT mean the view.
                // If we want to get the view, we should use getView().
                assertNull(hvgAdapter.getItem(0));
                assertNull(hvgAdapter.getItem(1));

                assertEquals(1, hvgAdapter.getFootersCount());
                assertNull(hvgAdapter.getItem(100/* items */ + 2/* header */ + 2/* footer */ - 1 - 1));
                assertNull(hvgAdapter.getItem(100/* items */ + 2/* header */ + 2/* footer */ - 1));
            }
        });
        // Scroll to bottom and try removing re-adding the footer view.
        for (int i = 0; i < 10; i++) {
            UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        }
        getInstrumentation().waitForIdleSync();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListAdapter adapter = scrollable.getAdapter();
                ObservableGridView.HeaderViewGridAdapter hvgAdapter = (ObservableGridView.HeaderViewGridAdapter) adapter;

                assertTrue(scrollable.removeFooterView(activity.footerView));
                assertEquals(0, scrollable.getFooterViewCount());
                assertEquals(0, hvgAdapter.getFootersCount());
                assertFalse(scrollable.removeFooterView(activity.footerView));

                activity.footerView = new View(activity);
                final int flexibleSpaceImageHeight = activity.getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
                FrameLayout.LayoutParams lpf = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    flexibleSpaceImageHeight);
                activity.footerView.setLayoutParams(lpf);
                scrollable.addFooterView(activity.footerView);
            }
        });
    }

    public void testHeaderViewGridExceptions() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    new ObservableGridView.HeaderViewGridAdapter(null, null, null);
                } catch (IllegalArgumentException e) {
                    fail();
                }
                ListAdapter adapter = scrollable.getAdapter();
                ObservableGridView.HeaderViewGridAdapter hvgAdapter = (ObservableGridView.HeaderViewGridAdapter) adapter;
                try {
                    hvgAdapter.setNumColumns(0);
                } catch (IllegalArgumentException e) {
                    fail();
                }
                ArrayList<ObservableGridView.FixedViewInfo> headerViewInfos = new ArrayList<>();
                ObservableGridView.HeaderViewGridAdapter adapter1 = new ObservableGridView.HeaderViewGridAdapter(headerViewInfos, null, null);
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
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
                try {
                    adapter1.getView(-1, null, scrollable);
                    fail();
                } catch (ArrayIndexOutOfBoundsException ignore) {
                }
            }
        });
    }

    public void testHeaderViewGridAdapter() throws Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    new ObservableGridView.HeaderViewGridAdapter(null, null, null);
                } catch (IllegalArgumentException ignore) {
                    fail();
                }
            }
        });
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ObservableGridView.FixedViewInfo> list = new ArrayList<>();
                Map<String, String> map = new LinkedHashMap<>();
                map.put("text", "A");
                List<Map<String, ?>> data = new ArrayList<>();
                data.add(map);
                ObservableGridView.HeaderViewGridAdapter adapter =
                    new ObservableGridView.HeaderViewGridAdapter(
                        list,
                        null,
                        new SimpleAdapter(
                            activity,
                            data,
                            android.R.layout.simple_list_item_1,
                            new String[]{"text"},
                            new int[]{android.R.id.text1}));
                assertFalse(adapter.removeHeader(null));
                assertEquals(1, adapter.getCount());
            }
        });
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ObservableGridView.FixedViewInfo> list = new ArrayList<>();
                ObservableGridView.HeaderViewGridAdapter adapter =
                    new ObservableGridView.HeaderViewGridAdapter(
                        list,
                        null,
                        null);
                assertEquals(0, adapter.getCount());
                try {
                    adapter.isEnabled(1);
                    fail();
                } catch (IndexOutOfBoundsException ignore) {
                }
                try {
                    adapter.getItem(1);
                    fail();
                } catch (IndexOutOfBoundsException ignore) {
                }
            }
        });
    }
}

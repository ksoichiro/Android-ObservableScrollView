package com.github.ksoichiro.android.observablescrollview.test;

import android.test.ActivityInstrumentationTestCase2;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;

public class ListViewActivityTest extends ActivityInstrumentationTestCase2<ListViewActivity> {

    private ObservableListView scrollable;

    public ListViewActivityTest() {
        super(ListViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        final ListViewActivity activity = getActivity();
        scrollable = (ObservableListView) activity.findViewById(R.id.scrollable);
    }

    public void testScroll() throws Throwable {
        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

}

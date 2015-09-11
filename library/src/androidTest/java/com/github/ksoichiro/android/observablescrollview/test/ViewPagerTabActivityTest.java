package com.github.ksoichiro.android.observablescrollview.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

public class ViewPagerTabActivityTest extends ActivityInstrumentationTestCase2<ViewPagerTabActivity> {

    private ViewPagerTabActivity activity;

    public ViewPagerTabActivityTest() {
        super(ViewPagerTabActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
    }

    public void testScroll() throws Throwable {
        for (int i = 0; i < 3; i++) {
            UiTestUtils.swipeHorizontally(this, activity.findViewById(R.id.pager), UiTestUtils.Direction.LEFT);
            getInstrumentation().waitForIdleSync();
            scroll();
        }
        for (int i = 0; i < 3; i++) {
            UiTestUtils.swipeHorizontally(this, activity.findViewById(R.id.pager), UiTestUtils.Direction.RIGHT);
            getInstrumentation().waitForIdleSync();
            scroll();
        }
    }

    public void scroll() throws Throwable {
        View scrollable = ((View) activity.getCurrentScrollable()).findViewById(R.id.scroll);

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.UP);
        getInstrumentation().waitForIdleSync();

        UiTestUtils.swipeVertically(this, scrollable, UiTestUtils.Direction.DOWN);
        getInstrumentation().waitForIdleSync();
    }

    public void testSaveAndRestoreInstanceState() throws Throwable {
        for (int i = 0; i < 3; i++) {
            UiTestUtils.saveAndRestoreInstanceState(this, activity);
            scroll();

            UiTestUtils.swipeHorizontally(this, activity.findViewById(R.id.pager), UiTestUtils.Direction.LEFT);
            getInstrumentation().waitForIdleSync();
        }
    }
}

package com.github.ksoichiro.android.observablescrollview.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

public class RecyclerViewActivityTest extends ActivityInstrumentationTestCase2<RecyclerViewActivity> {

    private Activity activity;
    private ObservableRecyclerView scrollable;

    public RecyclerViewActivityTest() {
        super(RecyclerViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        activity = getActivity();
        scrollable = (ObservableRecyclerView) activity.findViewById(R.id.scrollable);
        getInstrumentation().waitForIdleSync();
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
}

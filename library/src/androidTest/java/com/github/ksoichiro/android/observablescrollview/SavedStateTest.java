package com.github.ksoichiro.android.observablescrollview;

import android.os.Parcel;
import android.test.InstrumentationTestCase;
import android.util.SparseIntArray;
import android.view.AbsSavedState;

public class SavedStateTest extends InstrumentationTestCase {

    public void testGridViewSavedState() throws Throwable {
        Parcel parcel = Parcel.obtain();
        ObservableGridView.SavedState state1 = new ObservableGridView.SavedState(AbsSavedState.EMPTY_STATE);
        state1.prevFirstVisiblePosition = 1;
        state1.prevFirstVisibleChildHeight = 2;
        state1.prevScrolledChildrenHeight = 3;
        state1.prevScrollY = 4;
        state1.scrollY = 5;
        state1.childrenHeights = new SparseIntArray();
        state1.childrenHeights.put(0, 10);
        state1.childrenHeights.put(1, 20);
        state1.childrenHeights.put(2, 30);
        state1.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ObservableGridView.SavedState state2 = ObservableGridView.SavedState.CREATOR.createFromParcel(parcel);
        assertNotNull(state2);
        assertEquals(state1.prevFirstVisiblePosition, state2.prevFirstVisiblePosition);
        assertEquals(state1.prevFirstVisibleChildHeight, state2.prevFirstVisibleChildHeight);
        assertEquals(state1.prevScrolledChildrenHeight, state2.prevScrolledChildrenHeight);
        assertEquals(state1.prevScrollY, state2.prevScrollY);
        assertEquals(state1.scrollY, state2.scrollY);
        assertNotNull(state1.childrenHeights);
        assertEquals(3, state1.childrenHeights.size());
        assertEquals(10, state1.childrenHeights.get(0));
        assertEquals(20, state1.childrenHeights.get(1));
        assertEquals(30, state1.childrenHeights.get(2));
    }

    public void testListViewSavedState() throws Throwable {
        Parcel parcel = Parcel.obtain();
        ObservableListView.SavedState state1 = new ObservableListView.SavedState(AbsSavedState.EMPTY_STATE);
        state1.prevFirstVisiblePosition = 1;
        state1.prevFirstVisibleChildHeight = 2;
        state1.prevScrolledChildrenHeight = 3;
        state1.prevScrollY = 4;
        state1.scrollY = 5;
        state1.childrenHeights = new SparseIntArray();
        state1.childrenHeights.put(0, 10);
        state1.childrenHeights.put(1, 20);
        state1.childrenHeights.put(2, 30);
        state1.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ObservableListView.SavedState state2 = ObservableListView.SavedState.CREATOR.createFromParcel(parcel);
        assertNotNull(state2);
        assertEquals(state1.prevFirstVisiblePosition, state2.prevFirstVisiblePosition);
        assertEquals(state1.prevFirstVisibleChildHeight, state2.prevFirstVisibleChildHeight);
        assertEquals(state1.prevScrolledChildrenHeight, state2.prevScrolledChildrenHeight);
        assertEquals(state1.prevScrollY, state2.prevScrollY);
        assertEquals(state1.scrollY, state2.scrollY);
        assertNotNull(state1.childrenHeights);
        assertEquals(3, state1.childrenHeights.size());
        assertEquals(10, state1.childrenHeights.get(0));
        assertEquals(20, state1.childrenHeights.get(1));
        assertEquals(30, state1.childrenHeights.get(2));
    }

    public void testRecyclerViewSavedState() throws Throwable {
        Parcel parcel = Parcel.obtain();
        ObservableRecyclerView.SavedState state1 = new ObservableRecyclerView.SavedState(AbsSavedState.EMPTY_STATE);
        state1.prevFirstVisiblePosition = 1;
        state1.prevFirstVisibleChildHeight = 2;
        state1.prevScrolledChildrenHeight = 3;
        state1.prevScrollY = 4;
        state1.scrollY = 5;
        state1.childrenHeights = new SparseIntArray();
        state1.childrenHeights.put(0, 10);
        state1.childrenHeights.put(1, 20);
        state1.childrenHeights.put(2, 30);
        state1.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ObservableRecyclerView.SavedState state2 = ObservableRecyclerView.SavedState.CREATOR.createFromParcel(parcel);
        assertNotNull(state2);
        assertEquals(state1.prevFirstVisiblePosition, state2.prevFirstVisiblePosition);
        assertEquals(state1.prevFirstVisibleChildHeight, state2.prevFirstVisibleChildHeight);
        assertEquals(state1.prevScrolledChildrenHeight, state2.prevScrolledChildrenHeight);
        assertEquals(state1.prevScrollY, state2.prevScrollY);
        assertEquals(state1.scrollY, state2.scrollY);
        assertNotNull(state1.childrenHeights);
        assertEquals(3, state1.childrenHeights.size());
        assertEquals(10, state1.childrenHeights.get(0));
        assertEquals(20, state1.childrenHeights.get(1));
        assertEquals(30, state1.childrenHeights.get(2));
    }

    public void testScrollViewSavedState() throws Throwable {
        Parcel parcel = Parcel.obtain();
        ObservableScrollView.SavedState state1 = new ObservableScrollView.SavedState(AbsSavedState.EMPTY_STATE);
        state1.prevScrollY = 1;
        state1.scrollY = 2;
        state1.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ObservableScrollView.SavedState state2 = ObservableScrollView.SavedState.CREATOR.createFromParcel(parcel);
        assertNotNull(state2);
        assertEquals(state1.prevScrollY, state2.prevScrollY);
        assertEquals(state1.scrollY, state2.scrollY);
    }

    public void testWebViewSavedState() throws Throwable {
        Parcel parcel = Parcel.obtain();
        ObservableWebView.SavedState state1 = new ObservableWebView.SavedState(AbsSavedState.EMPTY_STATE);
        state1.prevScrollY = 1;
        state1.scrollY = 2;
        state1.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        ObservableWebView.SavedState state2 = ObservableWebView.SavedState.CREATOR.createFromParcel(parcel);
        assertNotNull(state2);
        assertEquals(state1.prevScrollY, state2.prevScrollY);
        assertEquals(state1.scrollY, state2.scrollY);
    }
}

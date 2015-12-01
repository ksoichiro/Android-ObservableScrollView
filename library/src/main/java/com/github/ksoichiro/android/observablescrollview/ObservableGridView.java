/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright (C) 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ksoichiro.android.observablescrollview;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * GridView that its scroll position can be observed.
 */
public class ObservableGridView extends GridView implements Scrollable {

    // Fields that should be saved onSaveInstanceState
    private int mPrevFirstVisiblePosition;
    private int mPrevFirstVisibleChildHeight = -1;
    private int mPrevScrolledChildrenHeight;
    private int mPrevScrollY;
    private int mScrollY;
    private SparseIntArray mChildrenHeights;

    // Fields that don't need to be saved onSaveInstanceState
    private ObservableScrollViewCallbacks mCallbacks;
    private List<ObservableScrollViewCallbacks> mCallbackCollection;
    private ScrollState mScrollState;
    private boolean mFirstScroll;
    private boolean mDragging;
    private boolean mIntercepted;
    private MotionEvent mPrevMoveEvent;
    private ViewGroup mTouchInterceptionViewGroup;
    private ArrayList<FixedViewInfo> mHeaderViewInfos;
    private ArrayList<FixedViewInfo> mFooterViewInfos;

    private OnScrollListener mOriginalScrollListener;
    private OnScrollListener mScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOriginalScrollListener != null) {
                mOriginalScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mOriginalScrollListener != null) {
                mOriginalScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            // AbsListView#invokeOnItemScrollListener calls onScrollChanged(0, 0, 0, 0)
            // on Android 4.0+, but Android 2.3 is not. (Android 3.0 is unknown)
            // So call it with onScrollListener.
            onScrollChanged();
        }
    };

    public ObservableGridView(Context context) {
        super(context);
        init();
    }

    public ObservableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObservableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        mPrevFirstVisiblePosition = ss.prevFirstVisiblePosition;
        mPrevFirstVisibleChildHeight = ss.prevFirstVisibleChildHeight;
        mPrevScrolledChildrenHeight = ss.prevScrolledChildrenHeight;
        mPrevScrollY = ss.prevScrollY;
        mScrollY = ss.scrollY;
        mChildrenHeights = ss.childrenHeights;
        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.prevFirstVisiblePosition = mPrevFirstVisiblePosition;
        ss.prevFirstVisibleChildHeight = mPrevFirstVisibleChildHeight;
        ss.prevScrolledChildrenHeight = mPrevScrolledChildrenHeight;
        ss.prevScrollY = mPrevScrollY;
        ss.scrollY = mScrollY;
        ss.childrenHeights = mChildrenHeights;
        return ss;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (hasNoCallbacks()) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // Whether or not motion events are consumed by children,
                // flag initializations which are related to ACTION_DOWN events should be executed.
                // Because if the ACTION_DOWN is consumed by children and only ACTION_MOVEs are
                // passed to parent (this view), the flags will be invalid.
                // Also, applications might implement initialization codes to onDownMotionEvent,
                // so call it here.
                mFirstScroll = mDragging = true;
                dispatchOnDownMotionEvent();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (hasNoCallbacks()) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIntercepted = false;
                mDragging = false;
                dispatchOnUpOrCancelMotionEvent(mScrollState);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPrevMoveEvent == null) {
                    mPrevMoveEvent = ev;
                }
                float diffY = ev.getY() - mPrevMoveEvent.getY();
                mPrevMoveEvent = MotionEvent.obtainNoHistory(ev);
                if (getCurrentScrollY() - diffY <= 0) {
                    // Can't scroll anymore.

                    if (mIntercepted) {
                        // Already dispatched ACTION_DOWN event to parents, so stop here.
                        return false;
                    }

                    // Apps can set the interception target other than the direct parent.
                    final ViewGroup parent;
                    if (mTouchInterceptionViewGroup == null) {
                        parent = (ViewGroup) getParent();
                    } else {
                        parent = mTouchInterceptionViewGroup;
                    }

                    // Get offset to parents. If the parent is not the direct parent,
                    // we should aggregate offsets from all of the parents.
                    float offsetX = 0;
                    float offsetY = 0;
                    for (View v = this; v != null && v != parent; v = (View) v.getParent()) {
                        offsetX += v.getLeft() - v.getScrollX();
                        offsetY += v.getTop() - v.getScrollY();
                    }
                    final MotionEvent event = MotionEvent.obtainNoHistory(ev);
                    event.offsetLocation(offsetX, offsetY);

                    if (parent.onInterceptTouchEvent(event)) {
                        mIntercepted = true;

                        // If the parent wants to intercept ACTION_MOVE events,
                        // we pass ACTION_DOWN event to the parent
                        // as if these touch events just have began now.
                        event.setAction(MotionEvent.ACTION_DOWN);

                        // Return this onTouchEvent() first and set ACTION_DOWN event for parent
                        // to the queue, to keep events sequence.
                        post(new Runnable() {
                            @Override
                            public void run() {
                                parent.dispatchTouchEvent(event);
                            }
                        });
                        return false;
                    }
                    // Even when this can't be scrolled anymore,
                    // simply returning false here may cause subView's click,
                    // so delegate it to super.
                    return super.onTouchEvent(ev);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        ListAdapter mAdapter = getAdapter();
        if (mAdapter != null && !(mAdapter instanceof HeaderViewGridAdapter)) {
            throw new IllegalStateException(
                "Cannot add header view to grid -- setAdapter has already been called.");
        }

        ViewGroup.LayoutParams lyp = v.getLayoutParams();

        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());

        if (lyp != null) {
            v.setLayoutParams(new FrameLayout.LayoutParams(lyp.width, lyp.height));
            fl.setLayoutParams(new AbsListView.LayoutParams(lyp.width, lyp.height));
        }
        fl.addView(v);
        info.view = v;
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);

        if (mAdapter != null) {
            ((HeaderViewGridAdapter) mAdapter).notifyDataSetChanged();
        }
    }

    public int getFooterViewCount() {
        return mFooterViewInfos.size();
    }

    public boolean removeFooterView(View v) {
        if (mFooterViewInfos.size() > 0) {
            boolean result = false;
            ListAdapter adapter = getAdapter();
            if (adapter != null && ((HeaderViewGridAdapter) adapter).removeFooter(v)) {
                result = true;
            }
            removeFixedViewInfo(v, mFooterViewInfos);
            return result;
        }
        return false;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        // Don't set l to super.setOnScrollListener().
        // l receives all events through mScrollListener.
        mOriginalScrollListener = l;
    }

    @Override
    public void setScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        mCallbacks = listener;
    }

    @Override
    public void addScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        if (mCallbackCollection == null) {
            mCallbackCollection = new ArrayList<>();
        }
        mCallbackCollection.add(listener);
    }

    @Override
    public void removeScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        if (mCallbackCollection != null) {
            mCallbackCollection.remove(listener);
        }
    }

    @Override
    public void clearScrollViewCallbacks() {
        if (mCallbackCollection != null) {
            mCallbackCollection.clear();
        }
    }

    @Override
    public void setTouchInterceptionViewGroup(ViewGroup viewGroup) {
        mTouchInterceptionViewGroup = viewGroup;
    }

    @Override
    public void scrollVerticallyTo(int y) {
        scrollTo(0, y);
    }

    @Override
    public int getCurrentScrollY() {
        return mScrollY;
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
        // Ignore, since the header rows depend on not being clipped
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (0 < mHeaderViewInfos.size()) {
            HeaderViewGridAdapter headerViewGridAdapter = new HeaderViewGridAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
            int numColumns = getNumColumnsCompat();
            if (1 < numColumns) {
                headerViewGridAdapter.setNumColumns(numColumns);
            }
            super.setAdapter(headerViewGridAdapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();
        if (adapter != null && !(adapter instanceof HeaderViewGridAdapter)) {
            throw new IllegalStateException("Cannot add header view to grid -- setAdapter has already been called.");
        }
        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());
        fl.addView(v);
        info.view = v;
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            ((HeaderViewGridAdapter) adapter).notifyDataSetChanged();
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public int getHeaderViewCount() {
        return mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if (mHeaderViewInfos.size() > 0) {
            boolean result = false;
            ListAdapter adapter = getAdapter();
            if (adapter != null && adapter instanceof HeaderViewGridAdapter && ((HeaderViewGridAdapter) adapter).removeHeader(v)) {
                result = true;
            }
            removeFixedViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter instanceof HeaderViewGridAdapter) {
            ((HeaderViewGridAdapter) adapter).setNumColumns(getNumColumnsCompat());
        }
    }

    private void init() {
        mChildrenHeights = new SparseIntArray();
        mHeaderViewInfos = new ArrayList<>();
        mFooterViewInfos = new ArrayList<>();
        super.setClipChildren(false);
        super.setOnScrollListener(mScrollListener);
    }

    private int getNumColumnsCompat() {
        if (Build.VERSION.SDK_INT >= 11) {
            return getNumColumns();
        } else {
            int columns = 0;
            if (getChildCount() > 0) {
                int width = getChildAt(0).getMeasuredWidth();
                if (width > 0) {
                    columns = getWidth() / width;
                }
            }
            return columns > 0 ? columns : AUTO_FIT;
        }
    }

    private void onScrollChanged() {
        if (hasNoCallbacks()) {
            return;
        }
        if (getChildCount() > 0) {
            int firstVisiblePosition = getFirstVisiblePosition();
            for (int i = getFirstVisiblePosition(), j = 0; i <= getLastVisiblePosition(); i++, j++) {
                if (mChildrenHeights.indexOfKey(i) < 0 || getChildAt(j).getHeight() != mChildrenHeights.get(i)) {
                    if (i % getNumColumnsCompat() == 0) {
                        mChildrenHeights.put(i, getChildAt(j).getHeight());
                    }
                }
            }

            View firstVisibleChild = getChildAt(0);
            if (firstVisibleChild != null) {
                if (mPrevFirstVisiblePosition < firstVisiblePosition) {
                    // scroll down
                    int skippedChildrenHeight = 0;
                    if (firstVisiblePosition - mPrevFirstVisiblePosition != 1) {
                        for (int i = firstVisiblePosition - 1; i > mPrevFirstVisiblePosition; i--) {
                            if (0 < mChildrenHeights.indexOfKey(i)) {
                                skippedChildrenHeight += mChildrenHeights.get(i);
                            }
                        }
                    }
                    mPrevScrolledChildrenHeight += mPrevFirstVisibleChildHeight + skippedChildrenHeight;
                    mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                } else if (firstVisiblePosition < mPrevFirstVisiblePosition) {
                    // scroll up
                    int skippedChildrenHeight = 0;
                    if (mPrevFirstVisiblePosition - firstVisiblePosition != 1) {
                        for (int i = mPrevFirstVisiblePosition - 1; i > firstVisiblePosition; i--) {
                            if (0 < mChildrenHeights.indexOfKey(i)) {
                                skippedChildrenHeight += mChildrenHeights.get(i);
                            }
                        }
                    }
                    mPrevScrolledChildrenHeight -= firstVisibleChild.getHeight() + skippedChildrenHeight;
                    mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                } else if (firstVisiblePosition == 0) {
                    mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                    mPrevScrolledChildrenHeight = 0;
                }
                if (mPrevFirstVisibleChildHeight < 0) {
                    mPrevFirstVisibleChildHeight = 0;
                }
                mScrollY = mPrevScrolledChildrenHeight - firstVisibleChild.getTop() + getPaddingTop();
                mPrevFirstVisiblePosition = firstVisiblePosition;

                dispatchOnScrollChanged(mScrollY, mFirstScroll, mDragging);
                if (mFirstScroll) {
                    mFirstScroll = false;
                }

                if (mPrevScrollY < mScrollY) {
                    mScrollState = ScrollState.UP;
                } else if (mScrollY < mPrevScrollY) {
                    mScrollState = ScrollState.DOWN;
                } else {
                    mScrollState = ScrollState.STOP;
                }
                mPrevScrollY = mScrollY;
            }
        }
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; ++i) {
            FixedViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }
    }

    private boolean hasNoCallbacks() {
        return mCallbacks == null && mCallbackCollection == null;
    }

    private class FullWidthFixedViewLayout extends FrameLayout {
        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int targetWidth = ObservableGridView.this.getMeasuredWidth()
                - ObservableGridView.this.getPaddingLeft()
                - ObservableGridView.this.getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth,
                MeasureSpec.getMode(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    static class SavedState extends BaseSavedState {
        int prevFirstVisiblePosition;
        int prevFirstVisibleChildHeight = -1;
        int prevScrolledChildrenHeight;
        int prevScrollY;
        int scrollY;
        SparseIntArray childrenHeights;

        /**
         * Called by onSaveInstanceState.
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Called by CREATOR.
         */
        private SavedState(Parcel in) {
            super(in);
            prevFirstVisiblePosition = in.readInt();
            prevFirstVisibleChildHeight = in.readInt();
            prevScrolledChildrenHeight = in.readInt();
            prevScrollY = in.readInt();
            scrollY = in.readInt();
            childrenHeights = new SparseIntArray();
            final int numOfChildren = in.readInt();
            if (0 < numOfChildren) {
                for (int i = 0; i < numOfChildren; i++) {
                    final int key = in.readInt();
                    final int value = in.readInt();
                    childrenHeights.put(key, value);
                }
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(prevFirstVisiblePosition);
            out.writeInt(prevFirstVisibleChildHeight);
            out.writeInt(prevScrolledChildrenHeight);
            out.writeInt(prevScrollY);
            out.writeInt(scrollY);
            final int numOfChildren = childrenHeights == null ? 0 : childrenHeights.size();
            out.writeInt(numOfChildren);
            if (0 < numOfChildren) {
                for (int i = 0; i < numOfChildren; i++) {
                    out.writeInt(childrenHeights.keyAt(i));
                    out.writeInt(childrenHeights.valueAt(i));
                }
            }
        }

        public static final Parcelable.Creator<SavedState> CREATOR
            = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private void dispatchOnDownMotionEvent() {
        if (mCallbacks != null) {
            mCallbacks.onDownMotionEvent();
        }
        if (mCallbackCollection != null) {
            for (int i = 0; i < mCallbackCollection.size(); i++) {
                ObservableScrollViewCallbacks callbacks = mCallbackCollection.get(i);
                callbacks.onDownMotionEvent();
            }
        }
    }

    private void dispatchOnScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (mCallbacks != null) {
            mCallbacks.onScrollChanged(scrollY, firstScroll, dragging);
        }
        if (mCallbackCollection != null) {
            for (int i = 0; i < mCallbackCollection.size(); i++) {
                ObservableScrollViewCallbacks callbacks = mCallbackCollection.get(i);
                callbacks.onScrollChanged(scrollY, firstScroll, dragging);
            }
        }
    }

    private void dispatchOnUpOrCancelMotionEvent(ScrollState scrollState) {
        if (mCallbacks != null) {
            mCallbacks.onUpOrCancelMotionEvent(scrollState);
        }
        if (mCallbackCollection != null) {
            for (int i = 0; i < mCallbackCollection.size(); i++) {
                ObservableScrollViewCallbacks callbacks = mCallbackCollection.get(i);
                callbacks.onUpOrCancelMotionEvent(scrollState);
            }
        }
    }

    public static class FixedViewInfo {
        public View view;
        public ViewGroup viewContainer;
        public Object data;
        public boolean isSelectable;
    }

    public static class HeaderViewGridAdapter implements WrapperListAdapter, Filterable {
        private final DataSetObservable mDataSetObservable = new DataSetObservable();
        private final ListAdapter mAdapter;
        static final ArrayList<FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<>();

        // This ArrayList is assumed to NOT be null.
        ArrayList<FixedViewInfo> mHeaderViewInfos;
        ArrayList<FixedViewInfo> mFooterViewInfos;
        private int mNumColumns = 1;
        private int mRowHeight = -1;
        boolean mAreAllFixedViewsSelectable;
        private final boolean mIsFilterable;
        private boolean mCachePlaceHoldView = true;
        // From Recycle Bin or calling getView, this a question...
        private boolean mCacheFirstHeaderView = false;

        public HeaderViewGridAdapter(ArrayList<FixedViewInfo> headerViewInfos, ArrayList<FixedViewInfo> footViewInfos, ListAdapter adapter) {
            mAdapter = adapter;
            mIsFilterable = adapter instanceof Filterable;
            if (headerViewInfos == null) {
                mHeaderViewInfos = EMPTY_INFO_LIST;
            } else {
                mHeaderViewInfos = headerViewInfos;
            }

            if (footViewInfos == null) {
                mFooterViewInfos = EMPTY_INFO_LIST;
            } else {
                mFooterViewInfos = footViewInfos;
            }
            mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos)
                && areAllListInfosSelectable(mFooterViewInfos);
        }

        public int getNumColumns() {
            return mNumColumns;
        }

        public void setNumColumns(int numColumns) {
            if (numColumns < 1) {
                return;
            }
            if (mNumColumns != numColumns) {
                mNumColumns = numColumns;
                notifyDataSetChanged();
            }
        }

        public void setRowHeight(int height) {
            mRowHeight = height;
        }

        public int getHeadersCount() {
            return mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return mFooterViewInfos.size();
        }

        /**
         * @return True if this adapter doesn't contain any data.  This is used to determine
         * whether the empty view should be displayed.  A typical implementation will return
         * getCount() == 0 but since getCount() includes the headers and footers, specialized
         * adapters might want a different behavior.
         */
        @Override
        public boolean isEmpty() {
            return (mAdapter == null || mAdapter.isEmpty());
        }

        private boolean areAllListInfosSelectable(ArrayList<FixedViewInfo> infos) {
            if (infos != null) {
                for (FixedViewInfo info : infos) {
                    if (!info.isSelectable) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean removeHeader(View v) {
            for (int i = 0; i < mHeaderViewInfos.size(); i++) {
                FixedViewInfo info = mHeaderViewInfos.get(i);
                if (info.view == v) {
                    mHeaderViewInfos.remove(i);
                    mAreAllFixedViewsSelectable =
                        areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos);
                    mDataSetObservable.notifyChanged();
                    return true;
                }
            }
            return false;
        }

        public boolean removeFooter(View v) {
            for (int i = 0; i < mFooterViewInfos.size(); i++) {
                FixedViewInfo info = mFooterViewInfos.get(i);
                if (info.view == v) {
                    mFooterViewInfos.remove(i);
                    mAreAllFixedViewsSelectable =
                        areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos);
                    mDataSetObservable.notifyChanged();
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getCount() {
            if (mAdapter != null) {
                return (getFootersCount() + getHeadersCount()) * mNumColumns + getAdapterAndPlaceHolderCount();
            } else {
                return (getFootersCount() + getHeadersCount()) * mNumColumns;
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return mAdapter == null || mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
        }

        private int getAdapterAndPlaceHolderCount() {
            return (int) (Math.ceil(1f * mAdapter.getCount() / mNumColumns) * mNumColumns);
        }

        @Override
        public boolean isEnabled(int position) {
            // Header (negative positions will throw an IndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                return position % mNumColumns == 0
                    && mHeaderViewInfos.get(position / mNumColumns).isSelectable;
            }

            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition < adapterCount) {
                    return adjPosition < mAdapter.getCount() && mAdapter.isEnabled(adjPosition);
                }
            }

            // Footer (off-limits positions will throw an IndexOutOfBoundsException)
            final int footerPosition = adjPosition - adapterCount;
            return footerPosition % mNumColumns == 0
                && mFooterViewInfos.get(footerPosition / mNumColumns).isSelectable;
        }

        @Override
        public Object getItem(int position) {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                if (position % mNumColumns == 0) {
                    return mHeaderViewInfos.get(position / mNumColumns).data;
                }
                return null;
            }

            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition < adapterCount) {
                    if (adjPosition < mAdapter.getCount()) {
                        return mAdapter.getItem(adjPosition);
                    } else {
                        return null;
                    }
                }
            }

            // Footer (off-limits positions will throw an IndexOutOfBoundsException)
            final int footerPosition = adjPosition - adapterCount;
            if (footerPosition % mNumColumns == 0) {
                return mFooterViewInfos.get(footerPosition).data;
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (mAdapter != null && position >= numHeadersAndPlaceholders) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                View headerViewContainer = mHeaderViewInfos
                    .get(position / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return headerViewContainer;
                } else {
                    if (convertView == null) {
                        convertView = new View(parent.getContext());
                    }
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(headerViewContainer.getHeight());
                    return convertView;
                }
            }
            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition < adapterCount) {
                    if (adjPosition < mAdapter.getCount()) {
                        return mAdapter.getView(adjPosition, convertView, parent);
                    } else {
                        if (convertView == null) {
                            convertView = new View(parent.getContext());
                        }
                        convertView.setVisibility(View.INVISIBLE);
                        convertView.setMinimumHeight(mRowHeight);
                        return convertView;
                    }
                }
            }
            // Footer
            final int footerPosition = adjPosition - adapterCount;
            if (footerPosition < getCount()) {
                View footViewContainer = mFooterViewInfos
                    .get(footerPosition / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return footViewContainer;
                } else {
                    if (convertView == null) {
                        convertView = new View(parent.getContext());
                    }
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(footViewContainer.getHeight());
                    return convertView;
                }
            }
            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public int getItemViewType(int position) {

            final int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            final int adapterViewTypeStart = mAdapter == null ? 0 : mAdapter.getViewTypeCount() - 1;
            int type = AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
            if (mCachePlaceHoldView) {
                // Header
                if (position < numHeadersAndPlaceholders) {
                    if (position == 0) {
                        if (mCacheFirstHeaderView) {
                            type = adapterViewTypeStart + mHeaderViewInfos.size() + mFooterViewInfos.size() + 1 + 1;
                        }
                    }
                    if (position % mNumColumns != 0) {
                        type = adapterViewTypeStart + (position / mNumColumns + 1);
                    }
                }
            }

            // Adapter
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = getAdapterAndPlaceHolderCount();
                if (adjPosition >= 0 && adjPosition < adapterCount) {
                    if (adjPosition < mAdapter.getCount()) {
                        type = mAdapter.getItemViewType(adjPosition);
                    } else {
                        if (mCachePlaceHoldView) {
                            type = adapterViewTypeStart + mHeaderViewInfos.size() + 1;
                        }
                    }
                }
            }

            if (mCachePlaceHoldView) {
                // Footer
                final int footerPosition = adjPosition - adapterCount;
                if (footerPosition >= 0 && footerPosition < getCount() && (footerPosition % mNumColumns) != 0) {
                    type = adapterViewTypeStart + mHeaderViewInfos.size() + 1 + (footerPosition / mNumColumns + 1);
                }
            }

            return type;
        }

        /**
         * Content view, content view holder, header[0], header and footer placeholder(s).
         */
        @Override
        public int getViewTypeCount() {
            int count = mAdapter == null ? 1 : mAdapter.getViewTypeCount();
            if (mCachePlaceHoldView) {
                int offset = mHeaderViewInfos.size() + 1 + mFooterViewInfos.size();
                if (mCacheFirstHeaderView) {
                    offset += 1;
                }
                count += offset;
            }

            return count;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        @Override
        public Filter getFilter() {
            if (mIsFilterable) {
                return ((Filterable) mAdapter).getFilter();
            }
            return null;
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return mAdapter;
        }

        public void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }
    }
}

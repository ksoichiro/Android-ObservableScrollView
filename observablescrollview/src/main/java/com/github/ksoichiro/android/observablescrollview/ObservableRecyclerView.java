/*
 * Copyright 2014 Soichiro Kashima
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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.internal.LogUtils;

public class ObservableRecyclerView extends RecyclerView {
    private static final String TAG = ObservableRecyclerView.class.getSimpleName();

    private ObservableScrollViewCallbacks mCallbacks;
    private int mPrevFirstVisiblePosition;
    private int mPrevFirstVisibleChildHeight = -1;
    private int mPrevScrolledChildrenHeight;
    private SparseIntArray mChildrenHeights;
    private int mPrevScrollY;
    private int mScrollY;
    private ScrollState mScrollState;
    private boolean mFirstScroll;
    private boolean mDragging;

    public ObservableRecyclerView(Context context) {
        super(context);
        init();
    }

    public ObservableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObservableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mCallbacks != null) {
            if (getChildCount() > 0) {
                int firstVisiblePosition = getChildPosition(getChildAt(0));
                int lastVisiblePosition = getChildPosition(getChildAt(getChildCount() - 1));
                for (int i = firstVisiblePosition, j = 0; i <= lastVisiblePosition; i++, j++) {
                    if (mChildrenHeights.indexOfKey(i) < 0 || getChildAt(j).getHeight() != mChildrenHeights.get(i)) {
                        mChildrenHeights.put(i, getChildAt(j).getHeight());
                    }
                }

                View firstVisibleChild = getChildAt(0);
                if (firstVisibleChild != null) {
                    if (mPrevFirstVisiblePosition < firstVisiblePosition) {
                        // scroll down
                        int skippedChildrenHeight = 0;
                        if (firstVisiblePosition - mPrevFirstVisiblePosition != 1) {
                            LogUtils.v(TAG, "Skipped some children while scrolling down: " + (firstVisiblePosition - mPrevFirstVisiblePosition));
                            for (int i = firstVisiblePosition - 1; i > mPrevFirstVisiblePosition; i--) {
                                if (0 < mChildrenHeights.indexOfKey(i)) {
                                    skippedChildrenHeight += mChildrenHeights.get(i);
                                    LogUtils.v(TAG, "Calculate skipped child height at " + i + ": " + mChildrenHeights.get(i));
                                } else {
                                    LogUtils.v(TAG, "Could not calculate skipped child height at " + i);
                                    // Approximate each item's height to the first visible child.
                                    // It may be incorrect, but without this, scrollY will be broken
                                    // when scrolling from the bottom.
                                    skippedChildrenHeight += firstVisibleChild.getHeight();
                                }
                            }
                        }
                        mPrevScrolledChildrenHeight += mPrevFirstVisibleChildHeight + skippedChildrenHeight;
                        mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                    } else if (firstVisiblePosition < mPrevFirstVisiblePosition) {
                        // scroll up
                        int skippedChildrenHeight = 0;
                        if (mPrevFirstVisiblePosition - firstVisiblePosition != 1) {
                            LogUtils.v(TAG, "Skipped some children while scrolling up: " + (mPrevFirstVisiblePosition - firstVisiblePosition));
                            for (int i = mPrevFirstVisiblePosition - 1; i > firstVisiblePosition; i--) {
                                if (0 < mChildrenHeights.indexOfKey(i)) {
                                    skippedChildrenHeight += mChildrenHeights.get(i);
                                    LogUtils.v(TAG, "Calculate skipped child height at " + i + ": " + mChildrenHeights.get(i));
                                } else {
                                    LogUtils.v(TAG, "Could not calculate skipped child height at " + i);
                                    // Approximate each item's height to the first visible child.
                                    // It may be incorrect, but without this, scrollY will be broken
                                    // when scrolling from the bottom.
                                    skippedChildrenHeight += firstVisibleChild.getHeight();
                                }
                            }
                        }
                        mPrevScrolledChildrenHeight -= firstVisibleChild.getHeight() + skippedChildrenHeight;
                        mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                    } else if (firstVisiblePosition == 0) {
                        mPrevFirstVisibleChildHeight = firstVisibleChild.getHeight();
                    }
                    if (mPrevFirstVisibleChildHeight < 0) {
                        mPrevFirstVisibleChildHeight = 0;
                    }
                    mScrollY = mPrevScrolledChildrenHeight - firstVisibleChild.getTop();
                    mPrevFirstVisiblePosition = firstVisiblePosition;

                    LogUtils.v(TAG, "first: " + firstVisiblePosition + " scrollY: " + mScrollY + " first height: " + firstVisibleChild.getHeight() + " first top: " + firstVisibleChild.getTop());
                    mCallbacks.onScrollChanged(mScrollY, mFirstScroll, mDragging);
                    if (mFirstScroll) {
                        mFirstScroll = false;
                    }

                    if (mPrevScrollY < mScrollY) {
                        //down
                        mScrollState = ScrollState.UP;
                    } else if (mScrollY < mPrevScrollY) {
                        //up
                        mScrollState = ScrollState.DOWN;
                    } else {
                        mScrollState = ScrollState.STOP;
                    }
                    mPrevScrollY = mScrollY;
                } else {
                    LogUtils.v(TAG, "first: " + firstVisiblePosition);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mCallbacks != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mFirstScroll = mDragging = true;
                    mCallbacks.onDownMotionEvent();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDragging = false;
                    mCallbacks.onUpOrCancelMotionEvent(mScrollState);
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    public void setScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        mCallbacks = listener;
    }

    public int getCurrentScrollY() {
        return mScrollY;
    }

    private void init() {
        mChildrenHeights = new SparseIntArray();
    }

    static class SavedState extends BaseSavedState {
        int prevFirstVisiblePosition;
        int prevFirstVisibleChildHeight = -1;
        int prevScrolledChildrenHeight;
        int prevScrollY;
        int scrollY;
        SparseIntArray childrenHeights;

        SavedState(Parcelable superState) {
            super(superState);
        }

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
}

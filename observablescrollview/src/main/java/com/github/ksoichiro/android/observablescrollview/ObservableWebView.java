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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * WebView that its scroll position can be observed.
 */
public class ObservableWebView extends WebView implements Scrollable {

    // Fields that should be saved onSaveInstanceState
    private int mPrevScrollY;
    private int mScrollY;

    // Fields that don't need to be saved onSaveInstanceState
    private ObservableScrollViewCallbacks mCallbacks;
    private ScrollState mScrollState;
    private boolean mFirstScroll;
    private boolean mDragging;

    public ObservableWebView(Context context) {
        super(context);
    }

    public ObservableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        mPrevScrollY = ss.prevScrollY;
        mScrollY = ss.scrollY;
        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.prevScrollY = mPrevScrollY;
        ss.scrollY = mScrollY;
        return ss;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mCallbacks != null) {
            mScrollY = t;

            mCallbacks.onScrollChanged(t, mFirstScroll, mDragging);
            if (mFirstScroll) {
                mFirstScroll = false;
            }

            if (mPrevScrollY < t) {
                mScrollState = ScrollState.UP;
            } else if (t < mPrevScrollY) {
                mScrollState = ScrollState.DOWN;
            } else {
                mScrollState = ScrollState.STOP;
            }
            mPrevScrollY = t;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mCallbacks != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // Whether or not motion events are consumed by children,
                    // flag initializations which are related to ACTION_DOWN events should be executed.
                    // Because if the ACTION_DOWN is consumed by children and only ACTION_MOVEs are
                    // passed to parent (this view), the flags will be invalid.
                    // Also, applications might implement initialization codes to onDownMotionEvent,
                    // so call it here.
                    mFirstScroll = mDragging = true;
                    mCallbacks.onDownMotionEvent();
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mCallbacks != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDragging = false;
                    mCallbacks.onUpOrCancelMotionEvent(mScrollState);
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void setScrollViewCallbacks(ObservableScrollViewCallbacks listener) {
        mCallbacks = listener;
    }

    @Override
    public void setTouchInterceptionViewGroup(ViewGroup viewGroup) {
    }

    @Override
    public void scrollVerticallyTo(int y) {
        scrollTo(0, y);
    }

    @Override
    public int getCurrentScrollY() {
        return mScrollY;
    }

    static class SavedState extends BaseSavedState {
        int prevScrollY;
        int scrollY;

        /**
         * Called by onSaveInstanceState.
         */
        private SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Called by CREATOR.
         */
        private SavedState(Parcel in) {
            super(in);
            prevScrollY = in.readInt();
            scrollY = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(prevScrollY);
            out.writeInt(scrollY);
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

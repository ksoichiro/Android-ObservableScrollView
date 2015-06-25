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

package com.github.ksoichiro.android.observablescrollview.samples;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Another implementation of FillGap Activity.
 * These examples uses TouchInterceptionFrameLayout to enable scrolling effect
 * even when there are few items and cannot be scrolled.
 *
 * Known issue: this example just moves TouchInterceptionFrameLayout on dragging
 * and it has no velocity, so as soon as the UP/CANCEL event occurred,
 * translation will be stopped.
 */
public abstract class FillGap3BaseActivity<S extends Scrollable> extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final String STATE_TRANSLATION_Y = "translationY";

    protected View mHeader;
    protected View mHeaderBar;
    private View mImageView;
    private View mHeaderBackground;
    private TextView mTitle;
    private S mScrollable;
    private TouchInterceptionFrameLayout mInterceptionLayout;

    // Fields that needs to saved
    private float mInitialTranslationY;

    // Fields that just keep constants like resource values
    protected int mActionBarSize;
    protected int mFlexibleSpaceImageHeight;
    protected int mIntersectionHeight;
    private int mHeaderBarHeight;

    // Temporary states
    private float mScrollYOnDownMotion;

    private float mPrevTranslationY;
    private boolean mGapIsChanging;
    private boolean mGapHidden;
    private boolean mReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();
        mHeaderBarHeight = getResources().getDimensionPixelSize(R.dimen.header_bar_height);

        // Even when the top gap has began to change, header bar still can move
        // within mIntersectionHeight.
        mIntersectionHeight = getResources().getDimensionPixelSize(R.dimen.intersection_height);

        mImageView = findViewById(R.id.image);
        mHeader = findViewById(R.id.header);
        mHeaderBar = findViewById(R.id.header_bar);
        mHeaderBackground = findViewById(R.id.header_background);

        mScrollable = createScrollable();

        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.scroll_wrapper);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(getTitle());
        ViewHelper.setTranslationY(mTitle, (mHeaderBarHeight - mActionBarSize) / 2);
        setTitle(null);

        if (savedInstanceState == null) {
            mInitialTranslationY = mFlexibleSpaceImageHeight - mHeaderBarHeight;
        }

        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                mReady = true;
                updateViews(mInitialTranslationY, false);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInitialTranslationY = savedInstanceState.getFloat(STATE_TRANSLATION_Y);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putFloat(STATE_TRANSLATION_Y, ViewHelper.getTranslationY(mInterceptionLayout));
        super.onSaveInstanceState(outState);
    }

    protected abstract int getLayoutResId();
    protected abstract S createScrollable();

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            return getMinInterceptionLayoutY() < (int) ViewHelper.getY(mInterceptionLayout)
                    || (moving && mScrollable.getCurrentScrollY() - diffY < 0);
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
            mScrollYOnDownMotion = mScrollable.getCurrentScrollY();
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            float translationY = ViewHelper.getTranslationY(mInterceptionLayout) - mScrollYOnDownMotion + diffY;
            float minTranslationY = getMinInterceptionLayoutY();
            if (translationY < minTranslationY) {
                translationY = minTranslationY;
            } else if (mFlexibleSpaceImageHeight - mHeaderBarHeight < translationY) {
                translationY = mFlexibleSpaceImageHeight - mHeaderBarHeight;
            }

            updateViews(translationY, true);
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
        }
    };

    protected void updateViews(float translationY, boolean animated) {
        // If it's ListView, onScrollChanged is called before ListView is laid out (onGlobalLayout).
        // This causes weird animation when onRestoreInstanceState occurred,
        // so we check if it's laid out already.
        if (!mReady) {
            return;
        }
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);

        // Translate image
        ViewHelper.setTranslationY(mImageView, (translationY - (mFlexibleSpaceImageHeight - mHeaderBarHeight)) / 2);

        // Translate title
        ViewHelper.setTranslationY(mTitle, Math.min(mIntersectionHeight, (mHeaderBarHeight - mActionBarSize) / 2));

        // Show/hide gap
        boolean scrollUp = translationY < mPrevTranslationY;
        if (scrollUp) {
            if (translationY <= mActionBarSize) {
                changeHeaderBackgroundHeightAnimated(false, animated);
            }
        } else {
            if (mActionBarSize <= translationY) {
                changeHeaderBackgroundHeightAnimated(true, animated);
            }
        }
        mPrevTranslationY = translationY;
    }

    private void changeHeaderBackgroundHeightAnimated(boolean shouldShowGap, boolean animated) {
        if (mGapIsChanging) {
            return;
        }
        final int heightOnGapShown = mHeaderBar.getHeight();
        final int heightOnGapHidden = mHeaderBar.getHeight() + mActionBarSize;
        final float from = mHeaderBackground.getLayoutParams().height;
        final float to;
        if (shouldShowGap) {
            if (!mGapHidden) {
                // Already shown
                return;
            }
            to = heightOnGapShown;
        } else {
            if (mGapHidden) {
                // Already hidden
                return;
            }
            to = heightOnGapHidden;
        }
        if (animated) {
            ViewPropertyAnimator.animate(mHeaderBackground).cancel();
            ValueAnimator a = ValueAnimator.ofFloat(from, to);
            a.setDuration(100);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float height = (float) animation.getAnimatedValue();
                    changeHeaderBackgroundHeight(height, to, heightOnGapHidden);
                }
            });
            a.start();
        } else {
            changeHeaderBackgroundHeight(to, to, heightOnGapHidden);
        }
    }

    private void changeHeaderBackgroundHeight(float height, float to, float heightOnGapHidden) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mHeaderBackground.getLayoutParams();
        lp.height = (int) height;
        lp.topMargin = (int) (mHeaderBar.getHeight() - height);
        mHeaderBackground.requestLayout();
        mGapIsChanging = (height != to);
        if (!mGapIsChanging) {
            mGapHidden = (height == heightOnGapHidden);
        }
    }

    private float getMinInterceptionLayoutY() {
        return mActionBarSize - mIntersectionHeight;
        // If you want to move header bar to the top, return 0 instead.
        //return 0;
    }
}

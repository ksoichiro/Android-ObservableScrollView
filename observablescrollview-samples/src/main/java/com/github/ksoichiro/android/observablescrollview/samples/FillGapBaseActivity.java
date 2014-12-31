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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public abstract class FillGapBaseActivity<S extends Scrollable> extends BaseActivity implements ObservableScrollViewCallbacks {

    protected View mHeader;
    protected int mFlexibleSpaceImageHeight;
    protected View mHeaderBar;
    protected int mActionBarSize;
    protected int mIntersectionHeight;

    private View mImageHolder;
    private View mHeaderBackground;
    private int mPrevScrollY;
    private boolean mGapIsChanging;
    private boolean mGapHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();

        // Even when the top gap has began to change, header bar still can move
        // within mIntersectionHeight.
        mIntersectionHeight = getResources().getDimensionPixelSize(R.dimen.intersection_height);

        mImageHolder = findViewById(R.id.image_holder);
        mHeader = findViewById(R.id.header);
        mHeaderBar = findViewById(R.id.header_bar);
        mHeaderBackground = findViewById(R.id.header_background);

        S scrollable = createScrollable();

        ((TextView) findViewById(R.id.title)).setText(getTitle());
        setTitle(null);

        ScrollUtils.addOnGlobalLayoutListener((View) scrollable, new Runnable() {
            @Override
            public void run() {
                onScrollChanged(0, false, false);
            }
        });
    }

    protected abstract int getLayoutResId();
    protected abstract S createScrollable();

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate image
        ViewHelper.setTranslationY(mImageHolder, -scrollY / 2);

        // Translate header
        ViewHelper.setTranslationY(mHeader, getHeaderTranslationY(scrollY));

        // Show/hide gap
        final int headerHeight = mHeaderBar.getHeight();
        boolean scrollUp = mPrevScrollY < scrollY;
        if (scrollUp) {
            if (mFlexibleSpaceImageHeight - headerHeight - mActionBarSize <= scrollY) {
                changeHeaderBackgroundHeight(false);
            }
        } else {
            if (scrollY <= mFlexibleSpaceImageHeight - headerHeight - mActionBarSize) {
                changeHeaderBackgroundHeight(true);
            }
        }
        mPrevScrollY = scrollY;
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private float getHeaderTranslationY(int scrollY) {
        final int headerHeight = mHeaderBar.getHeight();
        int headerTranslationY = mActionBarSize - mIntersectionHeight;
        if (0 <= -scrollY + mFlexibleSpaceImageHeight - headerHeight - mActionBarSize + mIntersectionHeight) {
            headerTranslationY = -scrollY + mFlexibleSpaceImageHeight - headerHeight;
        }
        return headerTranslationY;
    }

    private void changeHeaderBackgroundHeight(boolean shouldShowGap) {
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
        ViewPropertyAnimator.animate(mHeaderBackground).cancel();
        ValueAnimator a = ValueAnimator.ofFloat(from, to);
        a.setDuration(100);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = (float) animation.getAnimatedValue();
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mHeaderBackground.getLayoutParams();
                lp.height = (int) height;
                lp.topMargin = (int) (mHeaderBar.getHeight() - height);
                mHeaderBackground.requestLayout();
                mGapIsChanging = (height != to);
                if (!mGapIsChanging) {
                    mGapHidden = (height == heightOnGapHidden);
                }
            }
        });
        a.start();
    }
}

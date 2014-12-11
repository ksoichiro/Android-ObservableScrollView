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

import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class FlexibleSpaceWithImage2ScrollViewActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private View mImageView;
    private View mHeaderBar;
    private View mGapFiller;
    private ObservableScrollView mScrollView;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;
    private int mPrevScrollY;
    private boolean mGapFilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexiblespacewithimage2scrollview);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();

        mImageView = findViewById(R.id.image);
        mHeaderBar = findViewById(R.id.header_bar);
        mGapFiller = findViewById(R.id.filler);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        ((TextView) findViewById(R.id.title)).setText(getTitle());
        setTitle(null);

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                onScrollChanged(0, false, false);
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate image
        ViewHelper.setTranslationY(mImageView, Math.min(0, -scrollY / 2));

        // Translate header bar
        int headerBarTranslationY = Math.max(mActionBarSize, mFlexibleSpaceImageHeight - mHeaderBar.getHeight() - scrollY);
        ViewHelper.setTranslationY(mHeaderBar, headerBarTranslationY);

        // Translate gap filler
        if (scrollY <= mFlexibleSpaceImageHeight - mHeaderBar.getHeight() - mActionBarSize) {
            ViewPropertyAnimator.animate(mGapFiller).cancel();
            ViewHelper.setTranslationY(mGapFiller, headerBarTranslationY);
        } else if (mFlexibleSpaceImageHeight - mHeaderBar.getHeight() - mActionBarSize < scrollY
                && scrollY <= mFlexibleSpaceImageHeight - mActionBarSize) {
            boolean scrollUp = mPrevScrollY < scrollY;

            if (!mGapFilled && scrollUp) {
                mGapFilled = true;
                hideGap();
            } else if (mGapFilled && !scrollUp) {
                mGapFilled = false;
                showGap();
            }
        } else {
            if (!mGapFilled) {
                mGapFilled = true;
            }
            ViewPropertyAnimator.animate(mGapFiller).cancel();
            ViewHelper.setTranslationY(mGapFiller, Math.max(0, -scrollY + mFlexibleSpaceImageHeight - mHeaderBar.getHeight()));
        }
        mPrevScrollY = scrollY;
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showGap() {
        ViewPropertyAnimator.animate(mGapFiller).cancel();
        ViewPropertyAnimator.animate(mGapFiller).translationY(mActionBarSize).setDuration(200).start();
    }

    private void hideGap() {
        ViewPropertyAnimator.animate(mGapFiller).cancel();
        ViewPropertyAnimator.animate(mGapFiller).translationY(0).setDuration(200).start();
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}

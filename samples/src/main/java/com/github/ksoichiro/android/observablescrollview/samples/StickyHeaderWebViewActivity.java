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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class StickyHeaderWebViewActivity extends BaseActivity {

    private View mHeaderView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private boolean mFirstScroll;
    private boolean mDragging;
    private int mBaseTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickyheaderwebview);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(mScrollViewScrollCallbacks);

        ObservableWebView mWebView = (ObservableWebView) findViewById(R.id.web);
        mWebView.setScrollViewCallbacks(mWebViewScrollCallbacks);
        mWebView.loadUrl("file:///android_asset/lipsum.html");
    }

    private ObservableScrollViewCallbacks mScrollViewScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            if (mDragging) {
                int toolbarHeight = mToolbarView.getHeight();
                if (mFirstScroll) {
                    mFirstScroll = false;
                    float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                    if (-toolbarHeight < currentHeaderTranslationY) {
                        mBaseTranslationY = scrollY;
                    }
                }
                float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
                ViewPropertyAnimator.animate(mHeaderView).cancel();
                ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
            }
        }

        @Override
        public void onDownMotionEvent() {
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
            mDragging = false;
            mBaseTranslationY = 0;

            if (scrollState == ScrollState.DOWN) {
                showToolbar();
            } else if (scrollState == ScrollState.UP) {
                int toolbarHeight = mToolbarView.getHeight();
                int scrollY = mScrollView.getCurrentScrollY();
                if (toolbarHeight <= scrollY) {
                    hideToolbar();
                } else {
                    showToolbar();
                }
            } else {
                // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
                if (!toolbarIsShown() && !toolbarIsHidden()) {
                    // Toolbar is moving but doesn't know which to move:
                    // you can change this to hideToolbar()
                    showToolbar();
                }
            }
        }
    };

    private ObservableScrollViewCallbacks mWebViewScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        }

        @Override
        public void onDownMotionEvent() {
            // Workaround: WebView inside a ScrollView absorbs down motion events, so observing
            // down motion event from the WebView is required.
            mFirstScroll = mDragging = true;
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        }
    };

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
    }
}

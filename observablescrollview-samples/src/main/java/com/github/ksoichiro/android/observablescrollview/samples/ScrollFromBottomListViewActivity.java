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

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a sample of using ListView that scrolls from the bottom.
 * Currently we cannot get the correct 'scrollY', but it's now almost correct with the fix in the issue #3.
 * We can also use 'AbsListView#computeVerticalScrollOffset()' instead of 'scrollY' from 'onScrollChanged()'.
 * But please note that 'computeVerticalScrollOffset' is not the scroll position but the scroll bar's thumb,
 * so it cannot be used in some cases.
 * See the discussion below for details:
 * https://github.com/ksoichiro/Android-ObservableScrollView/issues/3
 */
public class ScrollFromBottomListViewActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private View mHeaderView;
    private View mToolbarView;
    private ObservableListView mListView;
    private int mBaseTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbarcontrollistview);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);

        mListView = (ObservableListView) findViewById(R.id.list);
        mListView.setScrollViewCallbacks(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        mListView.addHeaderView(inflater.inflate(R.layout.padding, null)); // toolbar
        mListView.addHeaderView(inflater.inflate(R.layout.padding, null)); // sticky view
        List<String> items = new ArrayList<String>();
        for (int i = 1; i <= 100; i++) {
            items.add("Item " + i);
        }
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

        ViewTreeObserver vto = mListView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int count = mListView.getAdapter().getCount() - 1;
                int position = count == 0 ? 1 : count > 0 ? count : 0;
                mListView.smoothScrollToPosition(position);
                mListView.setSelection(position);
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                if (-toolbarHeight < currentHeaderTranslationY && toolbarHeight < scrollY) {
                    mBaseTranslationY = scrollY;
                }
            }
            int headerTranslationY = Math.min(0, Math.max(-toolbarHeight, -(scrollY - mBaseTranslationY)));
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (scrollState == ScrollState.UP) {
            if (toolbarHeight < mListView.getCurrentScrollY()) {
                if (headerTranslationY != -toolbarHeight) {
                    ViewPropertyAnimator.animate(mHeaderView).cancel();
                    ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
                }
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarHeight < mListView.getCurrentScrollY()) {
                if (headerTranslationY != 0) {
                    ViewPropertyAnimator.animate(mHeaderView).cancel();
                    ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
                }
            }
        }
    }
}
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

/**
 * This sample doesn't include any scrolling effects.
 * With this sample, you can check how the callbacks occur
 * when the children views handle touch events.
 * <p/>
 * At least, ScrollView has an issue: when we touch a child which has OnClickListener
 * and drag it to expect its parent ScrollView generates onDownMotionEvent() and
 * onScrollChanged([WHATEVER], true, true), but they won't be generated.
 * <p/>
 * https://github.com/ksoichiro/Android-ObservableScrollView/issues/18
 */
public class HandleTouchScrollViewActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    private static final String TAG = HandleTouchScrollViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handletouchscrollview);

        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        int[] ids = new int[]{R.id.button1, R.id.button2, R.id.button3};
        for (int i = 0; i < ids.length; i++) {
            final int number = i + 1;
            findViewById(ids[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click(number);
                }
            });
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.v(TAG, "onScrollChanged: scrollY: " + scrollY + " firstScroll: " + firstScroll + " dragging: " + dragging);
    }

    @Override
    public void onDownMotionEvent() {
        Log.v(TAG, "onDownMotionEvent");
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.v(TAG, "onUpOrCancelMotionEvent: scrollState: " + scrollState);
    }

    private void click(int i) {
        String message = "Button " + i + " is clicked";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.v(TAG, "click: " + message);
    }
}

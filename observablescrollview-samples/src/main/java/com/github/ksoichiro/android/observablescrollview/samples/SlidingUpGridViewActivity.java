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

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

public class SlidingUpGridViewActivity extends SlidingUpBaseActivity<ObservableGridView> implements ObservableScrollViewCallbacks {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_slidingupgridview;
    }

    @Override
    protected ObservableGridView createScrollable() {
        ObservableGridView gridView = (ObservableGridView) findViewById(R.id.scroll);
        gridView.setScrollViewCallbacks(this);
        setDummyData(gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SlidingUpGridViewActivity.this, "Item " + (position + 1) + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return gridView;
    }
}

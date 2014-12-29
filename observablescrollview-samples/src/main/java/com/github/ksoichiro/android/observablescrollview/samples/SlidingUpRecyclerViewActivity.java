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

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;

public class SlidingUpRecyclerViewActivity extends SlidingUpBaseActivity<ObservableRecyclerView> implements ObservableScrollViewCallbacks {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_slidinguprecyclerview;
    }

    @Override
    protected ObservableRecyclerView createScrollable() {
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) findViewById(R.id.scroll);
        recyclerView.setScrollViewCallbacks(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CustomAdapter(this, getDummyData()));
        return recyclerView;
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<String> mItems;

        public CustomAdapter(Context context, ArrayList<String> items) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mItems = items;
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mContext, mInflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.textView.setText(mItems.get(position));
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            Context context;

            public ViewHolder(Context context, View view) {
                super(view);
                this.context = context;
                this.textView = (TextView) view.findViewById(android.R.id.text1);
                this.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(getPosition() + 1);
                    }
                });
            }

            private void click(int i) {
                Toast.makeText(context, "Button " + i + " is clicked", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

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

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment {
    public static ArrayList<String> getDummyData() {
        return BaseActivity.getDummyData();
    }

    protected void setDummyData(ListView listView) {
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getDummyData()));
    }

    protected void setDummyDataWithHeader(ListView listView, View headerView) {
        setDummyData(listView);
        listView.addHeaderView(headerView);
    }

    protected void setDummyData(GridView gridView) {
        gridView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getDummyData()));
    }

    protected void setDummyData(RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleRecyclerAdapter(getActivity(), getDummyData()));
    }

    protected void setDummyDataWithHeader(RecyclerView recyclerView, View headerView) {
        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(getActivity(), getDummyData(), headerView));
    }
}

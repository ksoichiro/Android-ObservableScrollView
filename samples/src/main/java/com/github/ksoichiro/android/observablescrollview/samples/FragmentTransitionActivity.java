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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

/**
 * This doesn't include ObservableScrollViews,
 * just shows how to show/hide Toolbar on the parent Activity of Fragment
 * to help you implement a screen that uses Fragments.
 */
public class FragmentTransitionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmenttransition);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        initFragment();
    }

    /**
     * Fragment should be added programmatically.
     * Using fragment tag in XML causes IllegalStateException on rotation of screen
     * or restoring states of activity.
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(FragmentTransitionDefaultFragment.FRAGMENT_TAG) == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment, new FragmentTransitionDefaultFragment(), FragmentTransitionDefaultFragment.FRAGMENT_TAG);
            ft.commit();
            fm.executePendingTransactions();
        }
    }
}

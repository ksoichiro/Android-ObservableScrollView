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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private static final String CATEGORY_SAMPLES = "com.github.ksoichiro.android.observablescrollview.samples";
    private static final String TAG_CLASS_NAME = "className";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_INTENT = "intent";

    private static final Comparator<Map<String, Object>> DISPLAY_NAME_COMPARATOR = new Comparator<Map<String, Object>>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
            return collator.compare(lhs.get("className"), rhs.get("className"));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new SimpleAdapter(this, getData(),
                R.layout.list_item_main,
                new String[]{TAG_CLASS_NAME, TAG_DESCRIPTION,},
                new int[]{R.id.className, R.id.description,}));
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem menu) {
        int id = menu.getItemId();
        if (id == R.id.menu_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }
        return false;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY_SAMPLES);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (list == null) {
            return data;
        }

        for (ResolveInfo info : list) {
            CharSequence labelSeq = info.loadLabel(pm);
            String label = labelSeq != null
                    ? labelSeq.toString()
                    : info.activityInfo.name;

            String[] labelPath = label.split("/");

            String nextLabel = labelPath[0];

            if (labelPath.length == 1) {
                String nameLabel = info.activityInfo.name.replace(info.activityInfo.packageName + "", "");
                if (nameLabel.startsWith(".")) {
                    nameLabel = nameLabel.substring(1);
                }
                addItem(data,
                        nameLabel,
                        nextLabel,
                        activityIntent(
                                info.activityInfo.applicationInfo.packageName,
                                info.activityInfo.name));
            }
        }

        Collections.sort(data, DISPLAY_NAME_COMPARATOR);

        return data;
    }

    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    protected void addItem(List<Map<String, Object>> data, String className, String description,
                           Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        temp.put(TAG_CLASS_NAME, className);
        temp.put(TAG_DESCRIPTION, description);
        temp.put(TAG_INTENT, intent);
        data.add(temp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);

        Intent intent = (Intent) map.get(TAG_INTENT);
        startActivity(intent);
    }
}
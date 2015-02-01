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

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

/**
 * Almost same as FillGapBaseActivity,
 * but in this activity, when swiping up, the filled space shrinks
 * and the header bar moves to the top.
 */
public abstract class FillGap2BaseActivity<S extends Scrollable> extends FillGapBaseActivity<S> {
    protected float getHeaderTranslationY(int scrollY) {
        return ScrollUtils.getFloat(-scrollY + mFlexibleSpaceImageHeight - mHeaderBar.getHeight(), 0, Float.MAX_VALUE);
    }
}
